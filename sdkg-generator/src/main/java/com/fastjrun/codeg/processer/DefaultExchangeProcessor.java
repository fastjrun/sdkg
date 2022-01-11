/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.codeg.processer;

import com.fastjrun.codeg.generator.method.ServiceMethodGenerator;
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
    public void processRPCRequest(JMethod jMethod, JInvocation jInvocation, JCodeModel cm) {
        this.requestProcessor.processRPCRequest(jMethod, jInvocation, cm);
    }

    @Override
    public String processHTTPRequest(JMethod jMethod, JInvocation jInvocation, CodeGConstants.MockModel mockModel, JCodeModel cm) {
        return this.requestProcessor.processHTTPRequest(jMethod, jInvocation, mockModel, cm);
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
    public void doParse(ServiceMethodGenerator serviceMethodGenerator, String packagePrefix) {
        this.requestProcessor.setRequestBodyClass(serviceMethodGenerator.getRequestBodyClass());
        this.requestProcessor.parseRequestClass(serviceMethodGenerator.getCm());
        this.responseProcessor.setElementClass(serviceMethodGenerator.getElementClass());
        this.responseProcessor.setResponseIsList(serviceMethodGenerator.getCommonMethod().isResponseIsArray());
        this.responseProcessor.setResponseIsPage(serviceMethodGenerator.getCommonMethod().isResponseIsPage());

        this.responseProcessor.parseResponseClass(serviceMethodGenerator.getCm());
    }
}
