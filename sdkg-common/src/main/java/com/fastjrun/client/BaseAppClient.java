package com.fastjrun.client;

import java.util.ResourceBundle;

public abstract class BaseAppClient extends BaseHttpClient {

    protected String appSource;
    protected String appVersion;
    protected String deviceId;
    protected String appKey;

    protected String appUrlPre;

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getAppSource() {
        return appSource;
    }

    public void setAppSource(String appSource) {
        this.appSource = appSource;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getAppUrlPre() {
        return appUrlPre;
    }

    public void setAppUrlPre(String appUrlPre) {
        this.appUrlPre = appUrlPre;
    }

    @Override
    public void initSDKConfig(String apiworld) {
        ResourceBundle rb = ResourceBundle.getBundle(apiworld + "-sdk");
        this.appKey = rb.getString(apiworld + ".appKey");
        this.appVersion = rb.getString(apiworld + ".appVersion");
        this.appSource = rb.getString(apiworld + ".appSource");
        this.deviceId = rb.getString(apiworld + ".deviceId");
        this.appUrlPre = rb.getString(apiworld + ".appUrlPre");
    }

    @Override
    protected String generateUrlSuffix() {
        StringBuilder sb = new StringBuilder();
        sb.append("/").append(this.appKey);
        sb.append("/").append(this.appVersion);
        sb.append("/").append(this.appSource);
        sb.append("/").append(this.deviceId);
        long txTime = System.currentTimeMillis();
        sb.append("/").append(txTime);
        return sb.toString();
    }
}
