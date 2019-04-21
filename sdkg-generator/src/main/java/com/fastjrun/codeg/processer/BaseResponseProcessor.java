/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.codeg.processer;

import com.helger.jcodemodel.AbstractJClass;

public abstract class BaseResponseProcessor implements ResponseProcessor {

    protected String baseResponseClassName;

    protected AbstractJClass responseClass;

    protected AbstractJClass elementClass;

    private boolean responseIsArray;

    public String getBaseResponseClassName() {
        return baseResponseClassName;
    }

    public void setBaseResponseClassName(String baseResponseClassName) {
        this.baseResponseClassName = baseResponseClassName;
    }

    public AbstractJClass getResponseClass() {
        return responseClass;
    }

    public void setResponseClass(AbstractJClass responseClass) {
        this.responseClass = responseClass;
    }

    public AbstractJClass getElementClass() {
        return elementClass;
    }

    public void setElementClass(AbstractJClass elementClass) {
        this.elementClass = elementClass;
    }

    public boolean isResponseIsArray() {
        return responseIsArray;
    }

    public void setResponseIsArray(boolean responseIsArray) {
        this.responseIsArray = responseIsArray;
    }
}
