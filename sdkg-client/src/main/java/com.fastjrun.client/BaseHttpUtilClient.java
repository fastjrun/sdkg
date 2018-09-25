package com.fastjrun.client;

import java.util.Map;

public abstract class BaseHttpUtilClient extends BaseUtilClient {

    protected String baseUrl;

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    protected abstract String process(String path, String method, Map<String, String> queryParams,
                                      Map<String, String> headParams, Map<String, String> cookieParams,
                                      String requestBody);

}
