package com.fastjrun.codeg.processer;

import com.fastjrun.codeg.common.CodeModelConstants;
import com.fastjrun.codeg.generator.BaseCMGenerator;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;

public abstract class BaseRequestWithoutHeadProcessor extends BaseRequestProcessor implements CodeModelConstants {
    @Override
    public String processHTTPRequest(JMethod method, JInvocation jInvocation, MockModel mockModel) {
        return "";
    }

    @Override
    public void processRPCRequest(JMethod method, JInvocation jInvocation) {
        if (this.requestClass != null) {
            method.param(this.requestClass, "request");
            jInvocation.arg(JExpr.ref("request"));
            method.body().invoke(JExpr.ref("log"), "debug")
                    .arg(BaseCMGenerator.JacksonUtilsClass
                            .staticInvoke("toJSon").arg(JExpr.ref("request")));
        }
    }

    @Override
    public void parseRequestClass() {
        this.requestClass = this.requestBodyClass;
    }
}
