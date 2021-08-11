/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.codeg.processer;

import com.helger.jcodemodel.AbstractJClass;
import com.helger.jcodemodel.AbstractJType;

public abstract class BaseResponseProcessor implements ResponseProcessor {

    protected String baseResponseClassName;

    protected AbstractJType responseClass;

    protected AbstractJType elementClass;

    private boolean responseIsList;

    private boolean responseIsPage;

    public boolean isResponseIsPage() {
        return responseIsPage;
    }

    public void setResponseIsPage(boolean responseIsPage) {
        this.responseIsPage = responseIsPage;
    }

    public String getBaseResponseClassName() {
        return baseResponseClassName;
    }

    public void setBaseResponseClassName(String baseResponseClassName) {
        this.baseResponseClassName = baseResponseClassName;
    }

    public AbstractJType getResponseClass() {
        return responseClass;
    }

    public void setResponseClass(AbstractJType responseClass) {
        this.responseClass = responseClass;
    }

    public AbstractJType getElementClass() {
        return elementClass;
    }

    public void setElementClass(AbstractJClass elementClass) {
        this.elementClass = elementClass;
    }

    public boolean isResponseIsList() {
        return responseIsList;
    }

    public void setResponseIsList(boolean responseIsList) {
        this.responseIsList = responseIsList;
    }
}
