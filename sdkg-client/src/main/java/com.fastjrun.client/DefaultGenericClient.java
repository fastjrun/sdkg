package com.fastjrun.client;

import com.fastjrun.util.FastJsonObjectMapper;

import java.util.ResourceBundle;

public class DefaultGenericClient extends DefaultResponseHttpHandleClient {

    @Override
    public void initSDKConfig() {
        ResourceBundle rb = ResourceBundle.getBundle("api-sdk");
        String baseUrl = rb.getString("genericServer.baseUrl");
        this.initUtilClient(baseUrl);
        this.objectMapper = new FastJsonObjectMapper();
    }

    @Override
    protected String generateUrlSuffix() {
        return "";
    }
}
