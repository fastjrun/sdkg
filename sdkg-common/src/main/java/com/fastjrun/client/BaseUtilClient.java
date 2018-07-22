package com.fastjrun.client;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class BaseUtilClient {

    protected final Logger log = LogManager.getLogger(this.getClass());

    protected String baseUrl;

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    protected abstract String process(String path, String method, Map<String, String> queryParams,
                                      Map<String, String> headParams, String requestBody);

}
