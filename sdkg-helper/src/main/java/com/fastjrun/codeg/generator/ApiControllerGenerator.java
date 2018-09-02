package com.fastjrun.codeg.generator;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JVar;

public class ApiControllerGenerator extends BaseHTTPGenerator {

    @Override
    protected String processRequestHead(ControllerType controllerType, JMethod controllerMethod, String
            methodPath) {
        JClass requestHeadClass = cm
                .ref("com.fastjrun.packet." + controllerType.controllerType + "RequestHead");

        JBlock controllerMethodBlk = controllerMethod.body();
        JVar requestHeadVar = controllerMethodBlk.decl(requestHeadClass, "requestHead",
                JExpr._new(requestHeadClass));
        methodPath = methodPath + "/{accessKey}/{txTime}/{md5Hash}";
        JVar accessKeyJVar = controllerMethod.param(cm.ref("String"), "accessKey");
        accessKeyJVar.annotate(cm.ref("org.springframework.web.bind.annotation.PathVariable"))
                .param("value", "accessKey");
        controllerMethodBlk.invoke(JExpr.ref("requestHead"), "setAccessKey").arg(JExpr.ref("accessKey"));
        if (this.mockModel == MockModel.MockModel_Swagger) {
            accessKeyJVar.annotate(cm.ref("io.swagger.annotations.ApiParam")).param("name", "accessKey")
                    .param("value", "接入客户端的accessKey").param("required", true);
        }
        JVar txTimeJVar = controllerMethod.param(cm.ref("Long"), "txTime");
        txTimeJVar.annotate(cm.ref("org.springframework.web.bind.annotation.PathVariable")).param("value",
                "txTime");
        controllerMethodBlk.invoke(JExpr.ref("requestHead"), "setTxTime").arg(JExpr.ref("txTime"));
        if (this.mockModel == MockModel.MockModel_Swagger) {
            txTimeJVar.annotate(cm.ref("io.swagger.annotations.ApiParam")).param("name", "txTime")
                    .param("value", "接口请求时间戳").param("required", true);
        }
        JVar md5HashJVar = controllerMethod.param(cm.ref("String"), "md5Hash");
        md5HashJVar.annotate(cm.ref("org.springframework.web.bind.annotation.PathVariable")).param("value",
                "md5Hash");
        controllerMethodBlk.invoke(JExpr.ref("requestHead"), "setMd5Hash").arg(JExpr.ref("md5Hash"));
        if (this.mockModel == MockModel.MockModel_Swagger) {
            md5HashJVar.annotate(cm.ref("io.swagger.annotations.ApiParam")).param("name", "md5Hash")
                    .param("value", "md5Hash").param("required", true);
        }
        return methodPath;
    }

}
