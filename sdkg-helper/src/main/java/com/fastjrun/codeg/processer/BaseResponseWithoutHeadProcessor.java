package com.fastjrun.codeg.processer;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JInvocation;

public abstract class BaseResponseWithoutHeadProcessor extends BaseResponseProcessor {

    @Override
    public void processResponse(JBlock methodBlk,
                                JInvocation jInvocation) {
        if (this.elementClass == null) {
            methodBlk.add(jInvocation);
        } else {
            if (this.isResponseIsArray()) {
                methodBlk.decl(cm.ref("java.util.List").narrow(this.elementClass), "responseBody", jInvocation);
            } else {
                methodBlk.decl(this.elementClass, "responseBody", jInvocation);
            }
            methodBlk.invoke(JExpr.ref("log"), "debug").arg(JExpr.ref("responseBody"));
            methodBlk._return(JExpr.ref("responseBody"));
        }
    }

    @Override
    public void parseResponseClass() {
        if (this.elementClass != null) {
            if (!this.isResponseIsArray()) {
                this.responseClass = cm.ref("java.util.List").narrow(this.elementClass);
            } else {
                this.responseClass = this.elementClass;
            }
        }
    }
}
