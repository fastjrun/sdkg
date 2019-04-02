package com.fastjrun.codeg.processer;

import com.fastjrun.codeg.generator.method.ServiceMethodGenerator;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JType;

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
    public String processHTTPRequest(JMethod jMethod, JInvocation jInvocation, MockModel mockModel, JCodeModel cm) {
        return this.requestProcessor.processHTTPRequest(jMethod, jInvocation, mockModel, cm);
    }

    @Override
    public void processResponse(JBlock methodBlk, JInvocation jInvocation, JCodeModel cm) {
        this.responseProcessor.processResponse(methodBlk, jInvocation, cm);
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
        this.requestProcessor.parseRequestClass(serviceMethodGenerator.getCm());
        this.responseProcessor.setElementClass(serviceMethodGenerator.getElementClass());
        this.responseProcessor.setResponseIsArray(serviceMethodGenerator.getCommonMethod().isResponseIsArray());
        this.responseProcessor.parseResponseClass(serviceMethodGenerator.getCm());
    }
}
