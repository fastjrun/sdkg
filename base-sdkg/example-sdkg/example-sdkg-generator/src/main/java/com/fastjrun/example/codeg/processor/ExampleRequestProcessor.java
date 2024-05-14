package com.fastjrun.example.codeg.processor;

import com.fastjrun.codeg.processor.BaseRequestProcessor;
import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JInvocation;
import com.helger.jcodemodel.JMethod;

public class ExampleRequestProcessor extends BaseRequestProcessor {

    @Override
    public String processHTTPRequest(JMethod method, JInvocation jInvocation, SwaggerVersion swaggerVersion,
                                     JCodeModel cm) {
        return "";
    }

    @Override
    public void parseRequestClass(JCodeModel cm) {
        this.requestClass = this.requestBodyClass;
    }
}
