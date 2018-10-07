package com.fastjrun.codeg.processer;

import com.fastjrun.codeg.generator.method.BaseControllerMethodGenerator;

public abstract class BaseRequestProcessor implements RequestProcessor {

    protected String requestHeadClassName;

    protected String requestClassName;

    @Override
    public void parseRequestClass(BaseControllerMethodGenerator baseControllerMethodGenerator) {
        if (baseControllerMethodGenerator.getRequestBodyClass() != null) {
            baseControllerMethodGenerator.setRequestClass(
                    cm.ref(this.requestClassName).narrow(baseControllerMethodGenerator.getRequestBodyClass()));
        } else {
            baseControllerMethodGenerator.setRequestClass(cm.ref(this.requestClassName));
        }
    }
}
