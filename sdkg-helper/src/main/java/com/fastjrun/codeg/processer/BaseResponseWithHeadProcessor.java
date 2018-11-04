package com.fastjrun.codeg.processer;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JInvocation;

public abstract class BaseResponseWithHeadProcessor extends BaseResponseProcessor {

    static JClass DEFAULT_RESPONSE_CLASS = cm.ref("com.fastjrun.dto.DefaultResponse");

    static JClass DEFAULT_LISTRESPONSE_CLASS = cm.ref("com.fastjrun.dto.DefaultListResponse");

    static JClass DEFAULT_RESPONSE_HELPER_CLASS = cm.ref("com.fastjrun.helper.BaseResponseHelper");

    @Override
    public void parseResponseClass() {
        if (this.elementClass != null) {
            if (!this.isResponseIsArray()) {
                this.responseClass = DEFAULT_RESPONSE_CLASS.narrow(this.elementClass);
            } else {
                this.responseClass = DEFAULT_LISTRESPONSE_CLASS.narrow(this.elementClass);
            }
        } else {
            if (!this.isResponseIsArray()) {
                this.responseClass = DEFAULT_RESPONSE_CLASS;
            } else {
                this.responseClass = DEFAULT_LISTRESPONSE_CLASS;
            }
        }
    }

    @Override
    public void processResponse(JBlock methodBlk,
                                JInvocation jInvocation) {

        String responseHelperMethodName = "getResult";
        if (this.isResponseIsArray()) {
            responseHelperMethodName = "getResultList";
        }

        methodBlk.decl(this.responseClass, "response",
                DEFAULT_RESPONSE_HELPER_CLASS.staticInvoke(responseHelperMethodName));
        if (this.elementClass == null) {
            methodBlk.add(jInvocation);
        } else {
            if (this.isResponseIsArray()) {
                methodBlk.decl(cm.ref("java.util.List").narrow(this.elementClass), "responseBody", jInvocation);
            } else {
                methodBlk.decl(this.elementClass, "responseBody", jInvocation);
            }

            methodBlk.invoke(JExpr.ref("response"), "setBody").arg(JExpr.ref("responseBody"));
        }
        methodBlk.invoke(JExpr.ref("log"), "debug").arg(JExpr.ref("response"));
        methodBlk._return(JExpr.ref("response"));
    }
}
