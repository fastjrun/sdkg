/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.client;


import java.util.List;
import java.util.Map;
import com.fastjrun.client.exchange.BaseHTTPExchange;
import com.fastjrun.client.util.BaseHTTPUtilClient;



public abstract class BaseHTTPClient<U extends BaseHTTPUtilClient, M extends BaseHTTPExchange> extends
        BaseClient {

    protected abstract void initUtilClient(String baseUrl);

    protected U baseClient;

    protected M baseExchange;

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
        return (T) this.baseExchange.parseObjectFromResponse(result,valueType);
    }

    protected <T>  List<T> parseListFromResponse(String result, Class<T> valueType) {
        return this.baseExchange.parseListFromResponse(result, valueType);

    }

    public String execute(String path, String method,
                          Map<String,
                                  String>
                                  queryParams,
                          Map<String, String> headParams,
                          Map<String, String> cookieParams, Object requestBody) {
        String bodyData = this.baseExchange.generateRequestBody(requestBody);
        String urlSuffix = this.generateUrlSuffix();
        String result =
                this.baseClient.process(path + urlSuffix, method, queryParams, headParams, cookieParams, bodyData);

        return result;
    }

    // 和request结构有关系
    protected abstract String generateUrlSuffix();

}
