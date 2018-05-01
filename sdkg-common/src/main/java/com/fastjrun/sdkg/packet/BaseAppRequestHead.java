package com.fastjrun.sdkg.packet;

import java.io.Serializable;

public class BaseAppRequestHead implements Serializable {

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
        sb.append("appKey");
        sb.append("=");
        sb.append(this.appKey);
        sb.append(",");
        sb.append("appSource");
        sb.append("=");
        sb.append(this.appSource);
        sb.append(",");
        sb.append("appVersion");
        sb.append("=");
        sb.append(this.appVersion);
        sb.append(",");
        sb.append("deviceId");
        sb.append("=");
        sb.append(this.deviceId);
        sb.append(",");
        sb.append("txTime");
        sb.append("=");
        sb.append(this.txTime);
        sb.append("]");
        return sb.toString();
    }
}
