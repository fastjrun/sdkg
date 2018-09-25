package com.fastjrun.dto;

import java.io.Serializable;

public class AppRequestHead implements Serializable {

    private static final long serialVersionUID = -4242620759722491454L;

    private String deviceId;

    private Long txTime;

    // appKey
    private String appKey;

    // ios,android
    private String appSource;

    // 版本号
    private String appVersion;

    public Long getTxTime() {
        return txTime;
    }

    public void setTxTime(Long txTime) {
        this.txTime = txTime;
    }

    public String getAppSource() {
        return appSource;
    }

    public void setAppSource(String appSource) {
        this.appSource = appSource;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(("BaseAppRequestHead" + " ["));
        sb.append("appKey=").append(this.appKey);
        sb.append(",appSource=").append(this.appSource);
        sb.append(",appVersion=").append(this.appVersion);
        sb.append(",deviceId=").append(this.deviceId);
        sb.append(",txTime=").append(this.txTime);
        sb.append("]");
        return sb.toString();
    }
}
