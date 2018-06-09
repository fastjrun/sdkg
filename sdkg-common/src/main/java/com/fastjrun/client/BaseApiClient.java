package com.fastjrun.client;

import java.util.ResourceBundle;

import com.fastjrun.helper.EncryptHelper;

public abstract class BaseApiClient extends BaseHttpWithResHeadClient {

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

    public void initSDKConfig(String apiworld) {
        ResourceBundle rb = ResourceBundle.getBundle(apiworld + "-sdk");
        this.accessKey = rb.getString(apiworld + ".accessKey");
        this.accessKeySn = rb.getString(apiworld + ".accessKeySn");
        this.apiUrlPre = rb.getString(apiworld + ".apiUrlPre");
    }
}
