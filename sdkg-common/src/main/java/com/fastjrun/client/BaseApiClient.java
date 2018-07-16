package com.fastjrun.client;

import java.util.ResourceBundle;

import com.fastjrun.helper.EncryptHelper;

public abstract class BaseApiClient extends BaseHttpClient {

    protected String accessKey;

    protected String accessKeySn;

    protected String apiUrlPre;

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

    public String getApiUrlPre() {
        return apiUrlPre;
    }

    public void setApiUrlPre(String apiUrlPre) {
        this.apiUrlPre = apiUrlPre;
    }

    @Override
    public void initSDKConfig(String apiworld) {
        ResourceBundle rb = ResourceBundle.getBundle(apiworld + "-sdk");
        this.accessKey = rb.getString(apiworld + ".accessKey");
        this.accessKeySn = rb.getString(apiworld + ".accessKeySn");
        this.apiUrlPre = rb.getString(apiworld + ".apiUrlPre");
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
