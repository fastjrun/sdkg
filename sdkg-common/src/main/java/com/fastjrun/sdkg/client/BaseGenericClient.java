package com.fastjrun.sdkg.client;

import java.util.Map;
import java.util.ResourceBundle;

import net.sf.json.JSONObject;

public abstract class BaseGenericClient extends BaseHttpClient {

    protected String genericUrlPre;

    public String getGenericUrlPre() {
        return genericUrlPre;
    }

    public void setGenericUrlPre(String genericUrlPre) {
        this.genericUrlPre = genericUrlPre;
    }

    public void initSDKConfig(String apiworld) {
        ResourceBundle rb = ResourceBundle.getBundle(apiworld + "-sdk");
        this.genericUrlPre = rb.getString(apiworld + ".genericUrlPre");
    }

    @Override
    protected JSONObject process(String reqStr, String urlReq, String method,
            Map<String, String> requestProperties) {
        JSONObject responseJsonObject = this.processInternal(reqStr, urlReq,
                method, requestProperties);
        return responseJsonObject;
    }
}
