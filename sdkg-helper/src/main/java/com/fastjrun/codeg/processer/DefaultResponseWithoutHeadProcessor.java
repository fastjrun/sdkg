package com.fastjrun.codeg.processer;

import com.fastjrun.codeg.generator.method.BaseControllerMethodGenerator;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JInvocation;

public class DefaultResponseWithoutHeadProcessor implements ResponseProcessor {

    static String emptyDTOClassName = "Boolean";

    @Override
    public void processResponse(BaseControllerMethodGenerator baseControllerMethodGenerator, JBlock methodBlk,
                                JInvocation jInvocation) {
        if (baseControllerMethodGenerator.getResponseBodyClass() != cm.VOID) {
            methodBlk.decl(baseControllerMethodGenerator.getResponseBodyClass(), "response", jInvocation);
            methodBlk.invoke(JExpr.ref("log"), "debug").arg(JExpr.ref("response"));
            methodBlk._return(JExpr.ref("response"));
        } else {
            methodBlk.add(jInvocation);
            methodBlk._return(JExpr._new(cm.ref(emptyDTOClassName)).arg(JExpr.lit(true)));
        }

    }

    @Override
    public void parseResponseClass(BaseControllerMethodGenerator baseControllerMethodGenerator) {
        if (baseControllerMethodGenerator.getResponseBodyClass() != cm.VOID) {
            if (baseControllerMethodGenerator.getCommonMethod().isResponseIsArray()) {
                baseControllerMethodGenerator.setResponseClass(
                        cm.ref("java.util.List").narrow(baseControllerMethodGenerator.getResponseBodyClass()));
            } else {
                baseControllerMethodGenerator
                        .setResponseClass((JClass) baseControllerMethodGenerator.getResponseBodyClass());
            }
        } else {
            baseControllerMethodGenerator
                    .setResponseClass(cm.ref(emptyDTOClassName));
        }
    }

}
