/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.codeg.processor;

import com.fastjrun.codeg.generator.method.BaseServiceMethodGenerator;
import com.fastjrun.codeg.common.CodeGConstants;
import com.helger.jcodemodel.AbstractJType;
import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JInvocation;
import com.helger.jcodemodel.JMethod;

public class DefaultExchangeProcessor<T extends BaseRequestProcessor, V extends BaseResponseProcessor>
        implements ExchangeProcessor {

    private T requestProcessor;

    private V responseProcessor;

    public DefaultExchangeProcessor(T requestProcessor, V responseProcessor) {
        this.requestProcessor = requestProcessor;
        this.responseProcessor = responseProcessor;
    }

    @Override
    public String processHTTPRequest(JMethod jMethod, JInvocation jInvocation, CodeGConstants.SwaggerVersion swaggerVersion, JCodeModel cm) {
        return this.requestProcessor.processHTTPRequest(jMethod, jInvocation, swaggerVersion, cm);
    }

    @Override
    public void processResponse(JBlock methodBlk, JInvocation jInvocation, JCodeModel cm) {
        this.responseProcessor.processResponse(methodBlk, jInvocation, cm);
    }

    @Override
    public AbstractJType getRequestClass() {
        return this.requestProcessor.getRequestClass();
    }

    @Override
    public AbstractJType getResponseClass() {
        return this.responseProcessor.getResponseClass();
    }

    @Override
    public void doParse(BaseServiceMethodGenerator serviceMethodGenerator, String packagePrefix) {
        this.requestProcessor.setRequestBodyClass(serviceMethodGenerator.getRequestBodyClass());
        this.requestProcessor.parseRequestClass(serviceMethodGenerator.getCm());
        this.responseProcessor.setElementClass(serviceMethodGenerator.getElementClass());
        this.responseProcessor.setResponseIsList(serviceMethodGenerator.getCommonMethod().isResponseIsArray());
        this.responseProcessor.setResponseIsPage(serviceMethodGenerator.getCommonMethod().isResponseIsPage());
        this.responseProcessor.setNeedResponse(serviceMethodGenerator.getCommonMethod().isNeedResponse());
        this.responseProcessor.setHttpStatus(serviceMethodGenerator.getCommonMethod().getHttpStatus());
        this.responseProcessor.parseResponseClass(serviceMethodGenerator.getCm());
    }
}
