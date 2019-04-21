/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.client;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import com.fastjrun.common.client.BaseHTTPClient;
import com.fastjrun.helper.EncryptHelper;
import com.fastjrun.exchange.DefaultHTTPExchange;
import com.fastjrun.common.util.DefaultHTTPUtilClient;

public class DefaultHTTPApiClient extends
        BaseHTTPClient<DefaultHTTPUtilClient, DefaultHTTPExchange> {

    protected String accessKey;

    protected String accessKeySn;

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getAccessKeySn() {
        return accessKeySn;
    }

    public void setAccessKeySn(String accessKeySn) {
        this.accessKeySn = accessKeySn;
    }

    public DefaultHTTPApiClient() {
        this.baseClient = new DefaultHTTPUtilClient();
        this.baseExchange = new DefaultHTTPExchange();
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
        String baseUrl = rb.getString("apiServer.baseUrl");
        this.accessKey = rb.getString("apiServer.accessKey");
        this.accessKeySn = rb.getString("apiServer.accessKeySn");
        this.initUtilClient(baseUrl);
    }

    @Override
    protected String generateUrlSuffix() {
        StringBuilder sb = new StringBuilder();
        sb.append("/").append(this.accessKey);
        long txTime = System.currentTimeMillis();
        sb.append("/").append(txTime);
        try {
            String md5Hash = EncryptHelper.md5Digest(this.getAccessKeySn()
                    + txTime);
            sb.append("/").append(md5Hash);
        } catch (Exception e) {
            log.warn("", e);
        }
        return sb.toString();
    }
}
