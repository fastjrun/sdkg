package com.fastjrun.client;

import java.util.List;
import java.util.Map;

import com.fastjrun.exchange.DefaultHTTPExchange;

/*
 * *
 *  * 注意：本内容仅限于公司内部传阅，禁止外泄以及用于其他的商业目的
 *  *
 *  * @author 崔莹峰
 *  * @Copyright 2018 快嘉框架. All rights reserved.
 *
 */

public abstract class BaseHttpExchangeHandleClient extends
        BaseExchangeHandleClient<BaseHttpUtilClient> {

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
