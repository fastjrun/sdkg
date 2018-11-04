/*
 * Copyright (C) 2018 Fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.client.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.fastjrun.common.ClientException;
import com.fastjrun.common.CodeMsgConstants;

import okhttp3.ConnectionPool;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DefaultHTTPUtilClient extends BaseHTTPUtilClient {

    private static final int DEFAULT_CONNECT_TIMEOUT = 5000;
    private static final int DEFAULT_READ_TIMEOUT = 30000;
    private static final int DEFAULT_WRITE_TIMEOUT = 10000;
    private static final int DEFAULT_KEEPALIVE_TIME = 5 * 60 * 1000;

    protected Map<String, String> requestHeaderDefault;
    private int maxTotal = 100;
    private boolean redirectable = true;

    private OkHttpClient okHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        return builder.connectTimeout(DEFAULT_CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
                .writeTimeout(DEFAULT_WRITE_TIMEOUT, TimeUnit.MILLISECONDS)
                .readTimeout(DEFAULT_READ_TIMEOUT, TimeUnit.MILLISECONDS)
                .connectionPool(new ConnectionPool(maxTotal,
                        DEFAULT_KEEPALIVE_TIME,
                        TimeUnit.MILLISECONDS))
                .followRedirects(redirectable)
                .build();
    }

    public String process(String path, String method, Map<String, String> queryParams,
                          Map<String, String> headParams, Map<String, String> cookieParams, String requestBody) {
        StringBuilder sb = new StringBuilder(this.baseUrl).append(path);
        OkHttpClient httpClient = okHttpClient();
        String result;

        if (queryParams != null && queryParams.size() > 0) {
            int i = 0;
            for (String key : queryParams.keySet()) {
                String queryParam = queryParams.get(key);
                String data;
                try {
                    data = URLEncoder.encode(queryParam, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    log.error("{}", e);
                    throw new ClientException(CodeMsgConstants.CodeMsg.ClIENT_REQUEST_QUERYSTRING_ENCODE_FAIL);
                }
                if (i > 0) {
                    sb.append("&");
                } else if (i == 0) {
                    sb.append("?");
                }
                i++;
                log.debug("key={};value={};encodeValue={}", key, queryParams.get(key), data);
                sb.append(key).append("=").append(data);
            }
        }
        Headers.Builder headBuilder = new Headers.Builder();
        Request.Builder requestBuilder = new Request.Builder();

        try {

            if (this.requestHeaderDefault != null && this.requestHeaderDefault.size() > 0) {
                for (String key : requestHeaderDefault.keySet()) {
                    log.debug("key={};value={}", key, requestHeaderDefault.get(key));
                    headBuilder.add(key, requestHeaderDefault.get(key));
                }
            }
            if (headParams != null && headParams.size() > 0) {
                for (String key : headParams.keySet()) {
                    log.debug("key={};value={}", key, headParams.get(key));
                    headBuilder.add(key, headParams.get(key));

                }
            }
            requestBuilder.url(sb.toString()).headers(headBuilder.build());

            if (cookieParams != null && cookieParams.size() > 0) {
                for (String key : cookieParams.keySet()) {
                    log.debug("key={};value={}", key, cookieParams.get(key));
                    requestBuilder.addHeader("Cookie", key + "=" + cookieParams.get(key));

                }
            }

            if (!method.toUpperCase().equals("GET")) {
                if (requestBody != null && requestBody.length() > 0) {
                    MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
                    RequestBody body = RequestBody.create(mediaType, requestBody);
                    if (method.toUpperCase().equals("POST")) {
                        requestBuilder.post(body);
                    } else if (method.toUpperCase().equals("PUT")) {
                        requestBuilder.put(body);
                    } else if (method.toUpperCase().equals("DELETE")) {
                        requestBuilder.delete(body);
                    }
                }
            }
            Request request = requestBuilder.build();
            Response response = httpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                result = response.body().string();
                if (log.isDebugEnabled()) {
                    log.debug(result);
                }
            } else {
                throw new ClientException(CodeMsgConstants.CodeMsg.ClIENT_NETWORK_RESPONSE_NOT_OK);
            }

        } catch (MalformedURLException e) {
            log.error("{}", e);
            throw new ClientException(CodeMsgConstants.CodeMsg.ClIENT_NETWORK_NOT_AVAILABLE);
        } catch (IOException e) {
            log.error("{}", e);
            throw new ClientException(CodeMsgConstants.CodeMsg.ClIENT_NETWORK_NOT_AVAILABLE);
        }
        if (result.equals("")) {
            throw new ClientException(CodeMsgConstants.CodeMsg.ClIENT_RESPONSE_EMPTY);
        }

        return result;
    }

    protected String process(String urlReq, String method) {

        return this.process(urlReq, method, null, requestHeaderDefault, null, null);
    }
}
