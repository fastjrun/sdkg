/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.codeg.processer;

import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JInvocation;

public class DefaultResponseWithHeadProcessor extends BaseResponseProcessor {

    static String DEFAULT_RESPONSE_CLASS_NAME = "com.fastjrun.example.dto.DefaultResponse";

    static String DEFAULT_RESPONSE_HELPER_CLASS_NAME =
      "com.fastjrun.example.helper.BaseResponseHelper";

    @Override
    public void parseResponseClass(JCodeModel cm) {
        if (this.elementClass == null) {
            this.responseClass = cm.ref(DEFAULT_RESPONSE_CLASS_NAME);
        } else {
            if (this.isResponseIsPage()) {
                this.responseClass = cm.ref(DEFAULT_RESPONSE_CLASS_NAME).narrow(
                        cm.ref("com.fastjrun.dto.PageResult").narrow(elementClass));
            } else if (this.isResponseIsList()) {
                this.responseClass = cm.ref("java.util.List").narrow(this.elementClass);
            } else {
                this.responseClass = cm.ref(DEFAULT_RESPONSE_CLASS_NAME).narrow(this.elementClass);
            }
        }
    }

    @Override
    public void processResponse(JBlock methodBlk, JInvocation jInvocation, JCodeModel cm) {

        String responseHelperMethodName = "getResult";

        methodBlk.decl(this.responseClass, "response",
                cm.ref(DEFAULT_RESPONSE_HELPER_CLASS_NAME).staticInvoke(responseHelperMethodName));
        if (this.elementClass == null) {
            methodBlk.add(jInvocation);
        } else {
            if (this.isResponseIsPage()) {
                methodBlk.decl(cm.ref("com.fastjrun.dto.PageResult").narrow(this.elementClass),
                        "responseBody", jInvocation);
            } else if (this.isResponseIsList()) {
                methodBlk.decl(cm.ref("java.util.List").narrow(this.elementClass), "responseBody",
                        jInvocation);
            } else {
                methodBlk.decl(this.elementClass, "responseBody", jInvocation);
            }

            methodBlk.add(JExpr.ref("response").invoke("setBody").arg(JExpr.ref("responseBody")));
        }
        methodBlk.add(JExpr.ref("log").invoke("debug").arg(JExpr.lit("response={}")).arg(
                JExpr.ref("response").invoke("toString")));
        methodBlk._return(JExpr.ref("response"));
    }
}
