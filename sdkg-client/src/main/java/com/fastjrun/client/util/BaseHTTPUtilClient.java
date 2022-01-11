/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.client.util;

import java.util.Map;

public abstract class BaseHTTPUtilClient extends BaseUtilClient {

    protected String baseUrl;

    protected Map<String, String> requestHeaderDefault;

    public Map<String, String> getRequestHeaderDefault() {
        return requestHeaderDefault;
    }

    public void setRequestHeaderDefault(Map<String, String> requestHeaderDefault) {
        this.requestHeaderDefault = requestHeaderDefault;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public abstract String process(String path, String method, Map<String, String> queryParams,
                                   Map<String, String> headParams, Map<String, String> cookieParams,
                                   String requestBody);

}
