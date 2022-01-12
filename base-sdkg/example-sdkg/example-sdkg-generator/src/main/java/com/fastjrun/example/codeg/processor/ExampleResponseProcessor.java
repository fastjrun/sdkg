/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.example.codeg.processor;

import com.fastjrun.codeg.processor.BaseResponseProcessor;
import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JInvocation;

public class ExampleResponseProcessor extends BaseResponseProcessor {
    @Override
    public void processResponse(JBlock methodBlk, JInvocation jInvocation, JCodeModel cm) {
        if (!this.isNeedResponse() || this.elementClass == null) {
            methodBlk.add(jInvocation);
        } else {
            if (this.isResponseIsPage()) {
                methodBlk.decl(cm.ref("com.fastjrun.example.dto.PageResult").narrow(this.elementClass),
                        "responseBody", jInvocation);
            } else if (this.isResponseIsList()) {
                methodBlk.decl(cm.ref("java.util.List").narrow(this.elementClass),
                        "responseBody", jInvocation);
            } else {
                methodBlk.decl(this.elementClass, "responseBody", jInvocation);
            }
            methodBlk.add(JExpr.ref("log").invoke("debug").arg(JExpr.lit("responseBody={}")).arg(
                    JExpr.ref("responseBody")));
            methodBlk._return(JExpr.ref("responseBody"));
        }
    }

    @Override
    public void parseResponseClass(JCodeModel cm) {
        if (!this.isNeedResponse()) {
            this.responseClass = cm.VOID;
        } else if (this.elementClass != null) {
            if (this.isResponseIsPage()) {
                this.responseClass =
                        cm.ref("com.fastjrun.example.dto.PageResult").narrow(this.elementClass);
            } else if (this.isResponseIsList()) {
                this.responseClass = cm.ref("java.util.List").narrow(this.elementClass);
            } else {
                this.responseClass = this.elementClass;
            }
        } else {
            this.responseClass = cm.VOID;
        }
    }
}
