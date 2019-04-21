/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.codeg.processer;

import com.fastjrun.codeg.common.CodeGConstants;
import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JInvocation;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.JVar;

public class ApiRequestProcessor extends BaseRequestProcessor {

    @Override
    public String processHTTPRequest(JMethod jcontrollerMethod, JInvocation jInvocation,
                                     CodeGConstants.MockModel mockModel, JCodeModel cm) {
        JBlock controllerMethodBlk = jcontrollerMethod.body();
        JVar requestHeadVar = controllerMethodBlk.decl(this.requestHeadClass,
                "requestHead",
                JExpr._new(this.requestHeadClass));
        JVar accessKeyJVar = jcontrollerMethod.param(cm.ref("String"), "accessKey");
        accessKeyJVar.annotate(cm.ref("org.springframework.web.bind.annotation.PathVariable"))
                .param("value", "accessKey");
        controllerMethodBlk.add(JExpr.ref("requestHead").invoke("setAccessKey").arg(JExpr.ref("accessKey")));
        if (mockModel == MockModel.MockModel_Swagger) {
            accessKeyJVar.annotate(cm.ref("io.swagger.annotations.ApiParam")).param("name", "accessKey")
                    .param("value", "接入客户端的accessKey").param("required", true);
        }
        JVar txTimeJVar = jcontrollerMethod.param(cm.ref("Long"), "txTime");
        txTimeJVar.annotate(cm.ref("org.springframework.web.bind.annotation.PathVariable")).param("value",
                "txTime");
        controllerMethodBlk.add(JExpr.ref("requestHead").invoke("setTxTime").arg(JExpr.ref("txTime")));
        if (mockModel == MockModel.MockModel_Swagger) {
            txTimeJVar.annotate(cm.ref("io.swagger.annotations.ApiParam")).param("name", "txTime")
                    .param("value", "接口请求时间戳").param("required", true);
        }
        JVar md5HashJVar = jcontrollerMethod.param(cm.ref("String"), "md5Hash");
        md5HashJVar.annotate(cm.ref("org.springframework.web.bind.annotation.PathVariable")).param("value",
                "md5Hash");
        controllerMethodBlk.add(JExpr.ref("requestHead").invoke("setMd5Hash").arg(JExpr.ref("md5Hash")));
        if (mockModel == MockModel.MockModel_Swagger) {
            md5HashJVar.annotate(cm.ref("io.swagger.annotations.ApiParam")).param("name", "md5Hash")
                    .param("value", "md5Hash").param("required", true);
        }
        controllerMethodBlk.add(JExpr._this().invoke("processHead").arg(requestHeadVar));
        return "/{accessKey}/{txTime}/{md5Hash}";
    }

    @Override
    public void processRPCRequest(JMethod method, JInvocation jInvocation, JCodeModel cm) {

    }
}
