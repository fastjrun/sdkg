package com.fastjrun.codeg.processer;

import com.sun.codemodel.JType;

public abstract class BaseRequestProcessor implements RequestProcessor {

    protected String baseRequestClassName;

    protected JType requestBodyClass;

    protected JType requestClass;

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

    public JType getRequestClass() {
        return requestClass;
    }

    public void setRequestClass(JType requestClass) {
        this.requestClass = requestClass;
    }
}
