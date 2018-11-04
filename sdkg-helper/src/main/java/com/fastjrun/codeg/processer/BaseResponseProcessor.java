package com.fastjrun.codeg.processer;

import com.fastjrun.codeg.common.CodeModelConstants;
import com.sun.codemodel.JClass;

public abstract class BaseResponseProcessor implements ResponseProcessor, CodeModelConstants {

    protected String baseResponseClassName;

    protected JClass responseClass;

    protected JClass elementClass;

    private boolean responseIsArray;

    public String getBaseResponseClassName() {
        return baseResponseClassName;
    }

    public void setBaseResponseClassName(String baseResponseClassName) {
        this.baseResponseClassName = baseResponseClassName;
    }

    public JClass getResponseClass() {
        return responseClass;
    }

    public void setResponseClass(JClass responseClass) {
        this.responseClass = responseClass;
    }

    public JClass getElementClass() {
        return elementClass;
    }

    public void setElementClass(JClass elementClass) {
        this.elementClass = elementClass;
    }

    public boolean isResponseIsArray() {
        return responseIsArray;
    }

    public void setResponseIsArray(boolean responseIsArray) {
        this.responseIsArray = responseIsArray;
    }
}
