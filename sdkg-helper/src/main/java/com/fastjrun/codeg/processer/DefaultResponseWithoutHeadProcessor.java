package com.fastjrun.codeg.processer;

import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JInvocation;

public class DefaultResponseWithoutHeadProcessor extends BaseResponseProcessor {
    @Override
    public void processResponse(JBlock methodBlk,
                                JInvocation jInvocation, JCodeModel cm) {
        if (this.elementClass == null) {
            methodBlk.add(jInvocation);
        } else {
            if (this.isResponseIsArray()) {
                methodBlk.decl(cm.ref("java.util.List").narrow(this.elementClass), "responseBody", jInvocation);
            } else {
                methodBlk.decl(this.elementClass, "responseBody", jInvocation);
            }
            methodBlk.add(JExpr.ref("log").invoke("debug").arg(JExpr.ref("responseBody")));
            methodBlk._return(JExpr.ref("responseBody"));
        }
    }

    @Override
    public void parseResponseClass(JCodeModel cm) {
        if (this.elementClass != null) {
            if (!this.isResponseIsArray()) {
                this.responseClass = cm.ref("java.util.List").narrow(this.elementClass);
            } else {
                this.responseClass = this.elementClass;
            }
        }
    }
}
