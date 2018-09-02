package com.fastjrun.codeg.generator;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JVar;

public class AppControllerGenerator extends BaseHTTPGenerator {

    @Override
    protected String processRequestHead(ControllerType controllerType, JMethod controllerMethod, String
            methodPath) {
        JClass requestHeadClass = cm
                .ref("com.fastjrun.packet." + controllerType.controllerType + "RequestHead");

        JBlock controllerMethodBlk = controllerMethod.body();
        JVar requestHeadVar = controllerMethodBlk.decl(requestHeadClass, "requestHead",
                JExpr._new(requestHeadClass));
        methodPath = methodPath + "/{appKey}/{appVersion}/{appSource}/{deviceId}/{txTime}";
        JVar appKeyJVar = controllerMethod.param(cm.ref("String"), "appKey");
        appKeyJVar.annotate(cm.ref("org.springframework.web.bind.annotation.PathVariable")).param("value",
                "appKey");
        controllerMethodBlk.invoke(JExpr.ref("requestHead"), "setAppKey").arg(JExpr.ref("appKey"));
        if (this.mockModel == MockModel.MockModel_Swagger) {
            appKeyJVar.annotate(cm.ref("io.swagger.annotations.ApiParam")).param("name", "appKey")
                    .param("value", "app分配的key").param("required", true);
        }
        JVar appVersionJVar = controllerMethod.param(cm.ref("String"), "appVersion");
        appVersionJVar.annotate(cm.ref("org.springframework.web.bind.annotation.PathVariable"))
                .param("value", "appVersion");
        controllerMethodBlk.invoke(JExpr.ref("requestHead"), "setAppVersion").arg(JExpr.ref("appVersion"));
        if (this.mockModel == MockModel.MockModel_Swagger) {
            appVersionJVar.annotate(cm.ref("io.swagger.annotations.ApiParam")).param("name", "appVersion")
                    .param("value", "当前app版本号").param("required", true);
        }
        JVar appSourceJVar = controllerMethod.param(cm.ref("String"), "appSource");
        appSourceJVar.annotate(cm.ref("org.springframework.web.bind.annotation.PathVariable"))
                .param("value", "appSource");
        controllerMethodBlk.invoke(JExpr.ref("requestHead"), "setAppSource").arg(JExpr.ref("appSource"));
        if (this.mockModel == MockModel.MockModel_Swagger) {
            appSourceJVar.annotate(cm.ref("io.swagger.annotations.ApiParam")).param("name", "appSource")
                    .param("value", "当前app渠道：ios,android").param("required", true);
        }
        JVar deviceIdJVar = controllerMethod.param(cm.ref("String"), "deviceId");
        deviceIdJVar.annotate(cm.ref("org.springframework.web.bind.annotation.PathVariable")).param("value",
                "deviceId");
        controllerMethodBlk.invoke(JExpr.ref("requestHead"), "setDeviceId").arg(JExpr.ref("deviceId"));
        if (this.mockModel == MockModel.MockModel_Swagger) {
            deviceIdJVar.annotate(cm.ref("io.swagger.annotations.ApiParam")).param("name", "deviceId")
                    .param("value", "设备Id").param("required", true);
        }

        JVar txTimeJVar = controllerMethod.param(cm.ref("Long"), "txTime");
        txTimeJVar.annotate(cm.ref("org.springframework.web.bind.annotation.PathVariable")).param("value",
                "txTime");
        controllerMethodBlk.invoke(JExpr.ref("requestHead"), "setTxTime").arg(JExpr.ref("txTime"));
        if (this.mockModel == MockModel.MockModel_Swagger) {
            txTimeJVar.annotate(cm.ref("io.swagger.annotations.ApiParam")).param("name", "txTime")
                    .param("value", "接口请求时间戳").param("required", true);
        }
        controllerMethodBlk.invoke(JExpr._this(), "processHead").arg(requestHeadVar);
        return methodPath;
    }

}
