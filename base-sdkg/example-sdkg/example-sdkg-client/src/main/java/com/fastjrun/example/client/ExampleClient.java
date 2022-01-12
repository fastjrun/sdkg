/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.example.client;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import com.fastjrun.client.BaseHTTPClient;
import com.fastjrun.example.exchange.ExampleExchange;
import com.fastjrun.client.util.DefaultHTTPUtilClient;

public class ExampleClient extends BaseHTTPClient<DefaultHTTPUtilClient, ExampleExchange> {

    public ExampleClient() {
        this.baseClient = new DefaultHTTPUtilClient();
        this.baseExchange = new ExampleExchange();
    }

    @Override
    protected void initUtilClient(String baseUrl) {
        baseClient.setBaseUrl(baseUrl);
        Map<String, String> requestHeaderDefault = new HashMap<>();
        requestHeaderDefault.put("Content-Type", "application/json;charset=UTF-8");
        baseClient.setRequestHeaderDefault(requestHeaderDefault);
    }

    @Override
    public void initSDKConfig() {
        ResourceBundle rb = ResourceBundle.getBundle("api-sdk");
        String baseUrl = rb.getString("genericServer.baseUrl");
        this.initUtilClient(baseUrl);
    }

    @Override
    protected String generateUrlSuffix() {
        return "";
    }
}
