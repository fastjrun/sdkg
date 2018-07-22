package com.fastjrun.client;

import java.util.ResourceBundle;

import com.fastjrun.util.FastJsonObjectMapper;

public class DefaultGenericClient extends DefaultResponseHandleClient {

    @Override
    public void initSDKConfig(String apiWorld) {
        ResourceBundle rb = ResourceBundle.getBundle(apiWorld + "-sdk");
        String baseUrl = rb.getString(apiWorld + ".baseUrl");
        this.initUtilClient(baseUrl);
        this.objectMapper = new FastJsonObjectMapper();
    }

    @Override
    protected String generateUrlSuffix() {
        return "";
    }
}
