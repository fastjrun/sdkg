package com.fastjrun.client;

import com.fastjrun.helper.EncryptHelper;
import com.fastjrun.util.FastJsonObjectMapper;

import java.util.ResourceBundle;

public class DefaultApiClient extends DefaultResponseHttpHandleClient {

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

    @Override
    public void initSDKConfig() {
        ResourceBundle rb = ResourceBundle.getBundle("api-sdk");
        String baseUrl = rb.getString("apiServer.baseUrl");
        this.accessKey = rb.getString("apiServer.accessKey");
        this.accessKeySn = rb.getString("apiServer.accessKeySn");
        this.initUtilClient(baseUrl);
        this.objectMapper = new FastJsonObjectMapper();
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
