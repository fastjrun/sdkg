package com.fastjrun.codeg.processer;

import com.fastjrun.codeg.generator.method.ServiceMethodGenerator;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JType;

public class DefaultExchangeProcessor implements ExchangeProcessor {

    private BaseRequestProcessor requestProcessor;

    private BaseResponseProcessor responseProcessor;

    public void setRequestProcessor(BaseRequestProcessor requestProcessor) {
        this.requestProcessor = requestProcessor;
    }

    public void setResponseProcessor(BaseResponseProcessor responseProcessor) {
        this.responseProcessor = responseProcessor;
    }

    @Override
    public void processRPCRequest(JMethod jMethod, JInvocation jInvocation) {
        this.requestProcessor.processRPCRequest(jMethod, jInvocation);
    }

    @Override
    public String processHTTPRequest(JMethod jMethod, JInvocation jInvocation, MockModel mockModel) {
        return this.requestProcessor.processHTTPRequest(jMethod, jInvocation, mockModel);
    }

    @Override
    public void processResponse(JBlock methodBlk, JInvocation jInvocation) {
        this.responseProcessor.processResponse(methodBlk, jInvocation);
    }

    @Override
    public JType getRequestClass() {
        return this.requestProcessor.getRequestClass();
    }

    @Override
    public JClass getResponseClass() {
        return this.responseProcessor.getResponseClass();
    }

    @Override
    public void doParse(ServiceMethodGenerator serviceMethodGenerator, String packagePrefix) {
        this.requestProcessor.setRequestBodyClass(serviceMethodGenerator.getRequestBodyClass());
        this.requestProcessor.parseRequestClass();
        this.responseProcessor.setElementClass(serviceMethodGenerator.getElementClass());
        this.responseProcessor.setResponseIsArray(serviceMethodGenerator.getCommonMethod().isResponseIsArray());
        this.responseProcessor.parseResponseClass();
    }
}
