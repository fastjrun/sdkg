package com.fastjrun.codeg.processer;

import com.sun.codemodel.JType;

public abstract class BaseRequestWithHeadProcessor extends BaseRequestProcessor {

    static final String API_REQUEST_CLASS_NAME = "com.fastjrun.dto.ApiRequest";

    static final String API_REQUEST_HEAD_CLASS_NAME = "com.fastjrun.dto.ApiRequestHead";

    static final String APP_REQUEST_CLASS_NAME = "com.fastjrun.dto.AppRequest";

    static final String APP_REQUEST_HEAD_CLASS_NAME = "com.fastjrun.dto.AppRequestHead";

    protected String baseRequestClassName;

    protected JType requestHeadClass;

    public String getBaseRequestClassName() {
        return baseRequestClassName;
    }

    public void setBaseRequestClassName(String baseRequestClassName) {
        this.baseRequestClassName = baseRequestClassName;
    }

    public JType getRequestBodyClass() {
        return requestBodyClass;
    }

    public void setRequestBodyClass(JType requestBodyClass) {
        this.requestBodyClass = requestBodyClass;
    }

    public JType getRequestHeadClass() {
        return requestHeadClass;
    }

    public void setRequestHeadClass(JType requestHeadClass) {
        this.requestHeadClass = requestHeadClass;
    }

    @Override
    public void parseRequestClass() {
        if (this.requestBodyClass != null && this.requestHeadClass != cm.VOID) {
            this.requestClass = cm.ref(this.baseRequestClassName).narrow(this.requestBodyClass);
        } else {
            this.requestClass = cm.ref(baseRequestClassName);
        }
    }
}
