package com.fastjrun.codeg.processer;

import com.fastjrun.codeg.generator.method.BaseControllerMethodGenerator;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;

public class DefaultExchangeProcessor implements ExchangeProcessor {

    private RequestProcessor requestProcessor;

    private ResponseProcessor responseProcessor;

    public void setRequestProcessor(RequestProcessor requestProcessor) {
        this.requestProcessor = requestProcessor;
    }

    public void setResponseProcessor(ResponseProcessor responseProcessor) {
        this.responseProcessor = responseProcessor;
    }

    @Override
    public String processRequest(BaseControllerMethodGenerator baseControllerMethodGenerator, JMethod jcontrollerMethod,
                                 MockModel mockModel) {
        return this.requestProcessor.processRequest(baseControllerMethodGenerator, jcontrollerMethod, mockModel);
    }

    @Override
    public void processResponse(BaseControllerMethodGenerator baseControllerMethodGenerator, JBlock methodBlk,
                                JInvocation jInvocation) {
        this.responseProcessor.processResponse(baseControllerMethodGenerator, methodBlk, jInvocation);
    }

    @Override
    public void parseResponseClass(BaseControllerMethodGenerator baseControllerMethodGenerator) {
        this.responseProcessor.parseResponseClass(baseControllerMethodGenerator);
    }

    @Override
    public void parseRequestClass(BaseControllerMethodGenerator baseControllerMethodGenerator) {
        this.requestProcessor.parseRequestClass(baseControllerMethodGenerator);

    }
}
