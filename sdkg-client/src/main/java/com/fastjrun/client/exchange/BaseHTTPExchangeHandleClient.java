/*
 * Copyright (C) 2018 Fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.client.exchange;

import java.util.List;
import java.util.Map;

import com.fastjrun.client.util.BaseHTTPUtilClient;
import com.fastjrun.exchange.DefaultHTTPExchange;

public abstract class BaseHTTPExchangeHandleClient extends
        BaseExchangeHandleClient<BaseHTTPUtilClient> {

    DefaultHTTPExchange defaultHTTPExchange;

    public void process(String path, String method,
                        Map<String,
                                String>
                                queryParams,
                        Map<String, String> headParams,
                        Map<String, String> cookieParams, Object requestBody) {

        this.execute(path, method, queryParams, headParams, cookieParams,
                requestBody);
    }

    public <T, V> T process(String path, String method,
                            Map<String,
                                    String>
                                    queryParams,
                            Map<String, String> headParams,
                            Map<String, String> cookieParams, V requestBody, Class<T> classType) {

        String result = this.execute(path, method, queryParams, headParams, cookieParams,
                requestBody);

        return this.parseObjectFromResponse(result, classType);
    }

    public <T> List<T> processList(String path, String method,
                                   Map<String,
                                           String>
                                           queryParams,
                                   Map<String, String> headParams,
                                   Map<String, String> cookieParams, Object requestBody, Class<T> classType) {

        String result = this.execute(path, method, queryParams, headParams, cookieParams,
                requestBody);

        return this.parseListFromResponse(result, classType);
    }

    protected <T> T parseObjectFromResponse(String result, Class<T> valueType) {
        return this.defaultHTTPExchange.parseObjectFromResponse(result, valueType);
    }

    protected <T> List<T> parseListFromResponse(String result, Class<T> valueType) {
        return this.defaultHTTPExchange.parseListFromResponse(result, valueType);

    }

    public String execute(String path, String method,
                          Map<String,
                                  String>
                                  queryParams,
                          Map<String, String> headParams,
                          Map<String, String> cookieParams, Object requestBody) {
        String bodyDate = this.defaultHTTPExchange.generateRequestBody(requestBody);
        String urlSuffix = this.generateUrlSuffix();
        String result =
                this.baseClient.process(path + urlSuffix, method, queryParams, headParams, cookieParams, bodyDate);

        return result;
    }

    // 和request结构有关系
    protected abstract String generateUrlSuffix();

}
