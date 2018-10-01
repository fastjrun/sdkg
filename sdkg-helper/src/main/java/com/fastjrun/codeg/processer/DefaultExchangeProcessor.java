package com.fastjrun.codeg.processer;

import com.fastjrun.codeg.generator.method.BaseControllerMethodGenerator;
import com.sun.codemodel.JMethod;

public class DefaultExchangeProcessor extends BaseExchangeProcessor {

    @Override
    public String processRequest(BaseControllerMethodGenerator baseControllerMethodGenerator, JMethod
            jcontrollerMethod, MockModel mockModel) {
        return "";
    }

    @Override
    public void parseRequestClass(BaseControllerMethodGenerator baseControllerMethodGenerator) {
        baseControllerMethodGenerator.setRequestClass(
                baseControllerMethodGenerator.getRequestBodyClass());
    }
}
