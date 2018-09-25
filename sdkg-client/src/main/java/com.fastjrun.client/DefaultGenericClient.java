package com.fastjrun.client;

import java.util.ResourceBundle;

import com.fastjrun.util.FastJsonObjectMapper;

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
