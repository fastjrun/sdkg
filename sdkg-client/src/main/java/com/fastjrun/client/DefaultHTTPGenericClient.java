package com.fastjrun.client;

import java.util.ResourceBundle;

import com.fastjrun.client.exchange.DefaultHTTPExchangeHandleClient;

public class DefaultHTTPGenericClient extends DefaultHTTPExchangeHandleClient {

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
