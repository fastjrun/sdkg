package com.fastjrun.codeg.processer;

import com.helger.jcodemodel.AbstractJType;
import com.helger.jcodemodel.JCodeModel;

public abstract class BaseRequestProcessor implements RequestProcessor {

    protected String baseRequestClassName;

    protected AbstractJType requestBodyClass;

    protected AbstractJType requestClass;

    protected AbstractJType requestHeadClass;

    public AbstractJType getRequestHeadClass() {
        return requestHeadClass;
    }

    public void setRequestHeadClass(AbstractJType requestHeadClass) {
        this.requestHeadClass = requestHeadClass;
    }

    public String getBaseRequestClassName() {
        return baseRequestClassName;
    }

    public void setBaseRequestClassName(String baseRequestClassName) {
        this.baseRequestClassName = baseRequestClassName;
    }

    public AbstractJType getRequestBodyClass() {
        return requestBodyClass;
    }

    public void setRequestBodyClass(AbstractJType requestBodyClass) {
        this.requestBodyClass = requestBodyClass;
    }

    public AbstractJType getRequestClass() {
        return requestClass;
    }

    public void setRequestClass(AbstractJType requestClass) {
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
