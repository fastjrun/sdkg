package com.fastjrun.codeg.processer;

import com.fastjrun.codeg.generator.method.BaseControllerMethodGenerator;
import com.fastjrun.exchange.DefaultExchange;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JInvocation;

public abstract class BaseExchangeProcessor implements ExchangeProcessor, DefaultExchange {

    @Override
    public void processResponse(BaseControllerMethodGenerator baseControllerMethodGenerator, JBlock methodBlk,
                                JInvocation jInvocation) {
        if (baseControllerMethodGenerator.getResponseBodyClass() == cm.VOID) {
            methodBlk.decl(baseControllerMethodGenerator.getResponseClass(), "response",
                    BaseControllerMethodGenerator.cm.ref("com.fastjrun.helper.BaseResponseHelper")
                            .staticInvoke("getSuccessResult"));
        } else {
            String responseHelperMethodName = "getResult";
            if (baseControllerMethodGenerator.getCommonMethod().isResponseIsArray()) {
                responseHelperMethodName = "getResultList";
            }
            methodBlk.decl(baseControllerMethodGenerator.getResponseClass(), "response",
                    BaseControllerMethodGenerator.cm.ref("com.fastjrun.helper.BaseResponseHelper")
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
                        BaseControllerMethodGenerator.cm.ref(LISTRESPONSE_CLASS_NAME)
                                .narrow(baseControllerMethodGenerator.getResponseBodyClass()));
            } else {
                baseControllerMethodGenerator.setResponseClass(
                        BaseControllerMethodGenerator.cm.ref(RESPONSE_CLASS_NAME)
                                .narrow(baseControllerMethodGenerator.getResponseBodyClass()));
            }
        } else {
            baseControllerMethodGenerator.setResponseClass(
                    BaseControllerMethodGenerator.cm.ref(RESPONSE_CLASS_NAME));
        }
    }
}
