package com.fastjrun.client;

import java.util.ResourceBundle;

public abstract class BaseGenericClient extends BaseHttpClient {

    protected String genericUrlPre;

    public String getGenericUrlPre() {
        return genericUrlPre;
    }

    public void setGenericUrlPre(String genericUrlPre) {
        this.genericUrlPre = genericUrlPre;
    }

    @Override
    public void initSDKConfig(String apiworld) {
        ResourceBundle rb = ResourceBundle.getBundle(apiworld + "-sdk");
        this.genericUrlPre = rb.getString(apiworld + ".genericUrlPre");
    }

    @Override
    protected String generateUrlSuffix() {
        return "";
    }
}
