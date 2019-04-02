package com.fastjrun.codeg.processer;

import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JType;

public abstract class BaseRequestProcessor implements RequestProcessor {

    protected String baseRequestClassName;

    protected JType requestBodyClass;

    protected JType requestClass;

    protected JType requestHeadClass;

    public JType getRequestHeadClass() {
        return requestHeadClass;
    }

    public void setRequestHeadClass(JType requestHeadClass) {
        this.requestHeadClass = requestHeadClass;
    }

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

    @Override
    public void parseRequestClass(JCodeModel cm) {
        if (this.requestBodyClass != null && this.requestHeadClass != cm.VOID) {
            this.requestClass = cm.ref(this.baseRequestClassName).narrow(this.requestBodyClass);
        } else {
            this.requestClass = cm.ref(baseRequestClassName);
        }
    }
}
