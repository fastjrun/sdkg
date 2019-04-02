package com.fastjrun.codeg.processer;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JInvocation;

public class DefaultResponseWithHeadProcessor extends BaseResponseProcessor {


    static String DEFAULT_RESPONSE_CLASS_NAME = "com.fastjrun.dto.DefaultResponse";

    static String DEFAULT_LISTRESPONSE_CLASS_NAME = "com.fastjrun.dto.DefaultListResponse";

    static String DEFAULT_RESPONSE_HELPER_CLASS_NAME = "com.fastjrun.helper.BaseResponseHelper";

    @Override
    public void parseResponseClass(JCodeModel cm) {
        if (this.elementClass != null) {
            if (!this.isResponseIsArray()) {
                this.responseClass = cm.ref(DEFAULT_RESPONSE_CLASS_NAME).narrow(this.elementClass);
            } else {
                this.responseClass = cm.ref(DEFAULT_LISTRESPONSE_CLASS_NAME).narrow(this.elementClass);
            }
        } else {
            if (!this.isResponseIsArray()) {
                this.responseClass = cm.ref(DEFAULT_RESPONSE_CLASS_NAME);
            } else {
                this.responseClass = cm.ref(DEFAULT_LISTRESPONSE_CLASS_NAME);
            }
        }
    }

    @Override
    public void processResponse(JBlock methodBlk,
                                JInvocation jInvocation,JCodeModel cm) {

        String responseHelperMethodName = "getResult";
        if (this.isResponseIsArray()) {
            responseHelperMethodName = "getResultList";
        }

        methodBlk.decl(this.responseClass, "response",
                cm.ref(DEFAULT_RESPONSE_HELPER_CLASS_NAME).staticInvoke(responseHelperMethodName));
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
