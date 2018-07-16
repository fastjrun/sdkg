package com.fastjrun.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import com.fastjrun.common.ClientException;
import com.fastjrun.common.CodeMsgContants;

import net.sf.json.JSONObject;

public abstract class BaseHttpClient extends BaseClient {

    protected JSONObject parseResponseBody(String reqStr, String urlReq, String method,
                                           Map<String, String> requestProperties) {
        String line;
        PrintWriter out;
        BufferedReader in;
        String result = "";

        URL request;
        try {
            if (method.toUpperCase().equals("GET")) {
                if (reqStr != null && !reqStr.equals("")) {
                    String data = URLEncoder.encode(reqStr, "UTF-8");
                    urlReq = urlReq + "/" + data;
                }
            }
            request = new URL(urlReq);
            HttpURLConnection connection = (HttpURLConnection) request.openConnection();
            connection.setRequestMethod(method);
            requestProperties.keySet().parallelStream()
                    .forEach(n -> connection.setRequestProperty(n, requestProperties.get(n)));
            if (!method.toUpperCase().equals("GET")) {
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setUseCaches(false);
                connection.setInstanceFollowRedirects(true);
                connection.connect();
                out = new PrintWriter(connection.getOutputStream());
                // 发送请求参数
                out.print(reqStr);
                // flush输出流的缓冲
                out.flush();

            }
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while ((line = in.readLine()) != null) {
                result += line;
            }
            connection.disconnect();
            log.debug(result);
        } catch (MalformedURLException e) {
            log.error("{}", e);
            throw new ClientException(CodeMsgContants.CodeMsg.CLIENT_NETWORK_NOT_AVAILABLE);
        } catch (IOException e) {
            log.error("{}", e);
            throw new ClientException(CodeMsgContants.CodeMsg.CLIENT_NETWORK_NOT_AVAILABLE);
        }
        if (result.equals("")) {
            throw new ClientException(CodeMsgContants.CodeMsg.CLIENT_EMPTY_RESPONSE);
        }
        JSONObject responseJsonObject = JSONObject.fromObject(result);

        JSONObject responseJsonHead = responseJsonObject.getJSONObject("head");
        if (responseJsonHead == null) {
            throw new ClientException(CodeMsgContants.CodeMsg.CLIENT_EMPTY_RESPONSE_HEAD);
        }
        String code = responseJsonHead.getString("code");
        if (code == null || code.equals("")) {
            throw new ClientException(CodeMsgContants.CodeMsg.CLIENT_EMPTY_RESPONSE_HEAD_CODE);
        }
        if (code.equals("0000")) {
            JSONObject responseJsonBody = responseJsonObject.getJSONObject("body");
            if(responseJsonBody!=null){
                return responseJsonObject
                        .getJSONObject("body");
            }else{
                return new JSONObject();
            }


        }
        String msg = responseJsonHead.getString("msg");
        if (msg == null) {
            msg = "";
        }

        log.error("code = {},msg = {}", code, msg);

        throw new ClientException(CodeMsgContants.CodeMsg.CLIENT_SYSTEM_EXCEPTION);
    }

    protected JSONObject process(String reqStr, String urlReq, String method) {
        Map<String, String> requestProperties = new HashMap<String, String>();
        requestProperties.put("Content-Type", "application/json");
        requestProperties.put("User-Agent",
                "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 "
                        + "Safari/537.36");

        requestProperties.put("Accept", "*/*");
        return this.parseResponseBody(reqStr, urlReq, method, requestProperties);
    }

    protected abstract String generateUrlSuffix();
}
