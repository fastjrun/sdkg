package com.fastjrun.eladmin.codeg.processer;

import com.fastjrun.codeg.processor.BaseResponseProcessor;
import com.fastjrun.eladmin.codeg.Constants;
import com.helger.jcodemodel.*;
import org.apache.commons.lang.StringUtils;

public class EladminResponseProcessor extends BaseResponseProcessor {
    @Override
    public void processResponse(JBlock methodBlk, JInvocation jInvocation, JCodeModel cm) {
        AbstractJClass entityClass = cm.ref("org.springframework.http.ResponseEntity");

        AbstractJClass httpStatusClass = cm.ref("org.springframework.http.HttpStatus");


        if (!this.isNeedResponse()) {
            methodBlk.add(jInvocation);
            if (StringUtils.isNotBlank(this.getHttpStatus())) {
                methodBlk._return(JExpr._new(entityClass).arg(httpStatusClass.staticRef(this.getHttpStatus().toUpperCase())));
            }
        } else {
            if (this.responseClass == entityClass) {
                methodBlk.add(jInvocation);
                methodBlk._return(JExpr._new(entityClass).arg(httpStatusClass.staticRef(this.getHttpStatus().toUpperCase())));
            } else {
                JVar response;
                if (this.isResponseIsPage()) {
                    response = methodBlk.decl(cm.ref(Constants.PAGE_RESULT_CLASS_NAME).narrow(this.elementClass), "response", jInvocation);
                } else if (this.isResponseIsList()) {
                    response = methodBlk.decl(cm.ref("java.util.List").narrow(this.elementClass), "response", jInvocation);
                } else {
                    response = methodBlk.decl(this.elementClass, "response", jInvocation);
                }

                methodBlk._return(JExpr._new(this.responseClass).arg(response).arg(httpStatusClass.staticRef(this.getHttpStatus().toUpperCase())));
            }

        }

    }

    @Override
    public void parseResponseClass(JCodeModel cm) {

        AbstractJClass entityClass = cm.ref("org.springframework.http.ResponseEntity");
        if (!this.isNeedResponse()) {
            this.responseClass = cm.VOID;
        } else if (this.elementClass != null) {
            if (this.isResponseIsPage()) {
                this.responseClass = entityClass.narrow(
                        cm.ref("com.fastjrun.eladmin.vo.PageResult").narrow(this.elementClass));
            } else if (this.isResponseIsList()) {
                this.responseClass = entityClass.narrow(cm.ref("java.util.List").narrow(this.elementClass));
            } else {
                this.responseClass = entityClass.narrow(this.elementClass);
            }
        } else {
            this.responseClass = entityClass;
        }
    }
}
