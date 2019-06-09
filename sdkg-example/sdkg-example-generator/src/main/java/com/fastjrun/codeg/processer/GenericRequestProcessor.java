/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.codeg.processer;

import com.fastjrun.codeg.common.CodeGConstants;
import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JInvocation;
import com.helger.jcodemodel.JMethod;

public class GenericRequestProcessor extends BaseRequestProcessor {

    @Override
    public String processHTTPRequest(JMethod method, JInvocation jInvocation, MockModel mockModel,
      JCodeModel cm) {
        return "";
    }

    @Override
    public void processRPCRequest(JMethod method, JInvocation jInvocation, JCodeModel cm) {
        if (this.requestClass != null) {
            method.param(this.requestClass, "request");
            jInvocation.arg(JExpr.ref("request"));
            method.body().add(JExpr.ref("log").invoke("debug").arg(JExpr.lit("request={}")).arg(
              cm.ref(CodeGConstants.JacksonUtilsClassName).staticInvoke("toJSon").arg(
                JExpr.ref("request"))));
        }
    }

    @Override
    public void parseRequestClass(JCodeModel cm) {
        this.requestClass = this.requestBodyClass;
    }
}
