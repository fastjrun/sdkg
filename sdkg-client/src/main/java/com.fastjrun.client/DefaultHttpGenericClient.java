package com.fastjrun.client;

import java.util.ResourceBundle;

public class DefaultHttpGenericClient extends DefaultGenericHttpExchangeHandleClient {

    @Override
    public void initSDKConfig() {
        ResourceBundle rb = ResourceBundle.getBundle("api-sdk");
        String baseUrl = rb.getString("genericServer.baseUrl");
        this.initUtilClient(baseUrl);
        this.initExchange();
    }

    @Override
    protected String generateUrlSuffix() {
        return "";
    }
}
