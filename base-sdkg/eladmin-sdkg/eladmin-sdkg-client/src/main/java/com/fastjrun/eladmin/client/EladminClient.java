/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.eladmin.client;

import com.fastjrun.client.BaseHTTPClient;
import com.fastjrun.client.util.DefaultHTTPUtilClient;
import com.fastjrun.eladmin.exchange.EladminHTTPExchange;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class EladminClient extends BaseHTTPClient<DefaultHTTPUtilClient, EladminHTTPExchange> {

    public EladminClient() {
        this.baseClient = new DefaultHTTPUtilClient();
        this.baseExchange = new EladminHTTPExchange();
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
