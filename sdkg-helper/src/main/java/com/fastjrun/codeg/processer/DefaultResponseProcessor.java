package com.fastjrun.codeg.processer;

import com.fastjrun.codeg.generator.method.BaseControllerMethodGenerator;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JInvocation;

public class DefaultResponseProcessor implements ResponseProcessor {

    static String responseClassName = "com.fastjrun.dto.DefaultResponse";

    static String listResponseClassName = "com.fastjrun.dto.DefaultListResponse";

    @Override
    public void processResponse(BaseControllerMethodGenerator baseControllerMethodGenerator, JBlock methodBlk,
                                JInvocation jInvocation) {
        if (baseControllerMethodGenerator.getResponseBodyClass() == cm.VOID) {
            methodBlk.decl(baseControllerMethodGenerator.getResponseClass(), "response",
                    cm.ref("com.fastjrun.helper.BaseResponseHelper")
                            .staticInvoke("getSuccessResult"));
        } else {
            String responseHelperMethodName = "getResult";
            if (baseControllerMethodGenerator.getCommonMethod().isResponseIsArray()) {
                responseHelperMethodName = "getResultList";
            }
            methodBlk.decl(baseControllerMethodGenerator.getResponseClass(), "response",
                    cm.ref("com.fastjrun.helper.BaseResponseHelper")
                            .staticInvoke(responseHelperMethodName));
            methodBlk.decl(baseControllerMethodGenerator.getResponseBodyClass(), "responseBody", jInvocation);
            methodBlk.invoke(JExpr.ref("response"), "setBody").arg(JExpr.ref("responseBody"));
        }

        methodBlk.invoke(JExpr.ref("log"), "debug").arg(JExpr.ref("response"));
        methodBlk._return(JExpr.ref("response"));
    }

    @Override
    public void parseResponseClass(BaseControllerMethodGenerator baseControllerMethodGenerator) {
        if (baseControllerMethodGenerator.getResponseBodyClass() != cm.VOID) {
            if (baseControllerMethodGenerator.getCommonMethod().isResponseIsArray()) {
                baseControllerMethodGenerator.setResponseClass(
                        cm.ref(listResponseClassName).narrow(baseControllerMethodGenerator.getResponseBodyClass()));
            } else {
                baseControllerMethodGenerator
                        .setResponseClass(
                                cm.ref(responseClassName).narrow(baseControllerMethodGenerator.getResponseBodyClass()));
            }
        } else {
            baseControllerMethodGenerator.setResponseClass(
                    cm.ref(responseClassName));
        }
    }

}
