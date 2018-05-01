package com.fastjrun.sdkg.client;

import java.util.ResourceBundle;

public abstract class BaseAppClient extends BaseHttpWithResHeadClient {

    private String appSource;
    protected String appVersion;
    protected String deviceId;
    private String appKey;

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

    public void initSDKConfig(String apiworld) {
        ResourceBundle rb = ResourceBundle.getBundle(apiworld + "-sdk");
        this.appKey = rb.getString(apiworld + ".appKey");
        this.appVersion = rb.getString(apiworld + ".appVersion");
        this.appSource = rb.getString(apiworld + ".appSource");
        this.deviceId = rb.getString(apiworld + ".deviceId");
        this.appUrlPre = rb.getString(apiworld + ".appUrlPre");
    }
}
