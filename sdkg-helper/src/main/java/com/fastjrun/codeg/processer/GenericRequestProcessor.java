package com.fastjrun.codeg.processer;

import com.fastjrun.codeg.common.CodeGConstants;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;

public class GenericRequestProcessor extends BaseRequestProcessor {

    @Override
    public String processHTTPRequest(JMethod method, JInvocation jInvocation, MockModel mockModel,JCodeModel cm) {
        return "";
    }

    @Override
    public void processRPCRequest(JMethod method, JInvocation jInvocation,JCodeModel cm) {
        if (this.requestClass != null) {
            method.param(this.requestClass, "request");
            jInvocation.arg(JExpr.ref("request"));
            method.body().invoke(JExpr.ref("log"), "debug")
                    .arg(cm.ref(JacksonUtilsClassName)
                            .staticInvoke("toJSon").arg(JExpr.ref("request")));
        }
    }

    @Override
    public void parseRequestClass(JCodeModel cm) {
        this.requestClass = this.requestBodyClass;
    }
}
