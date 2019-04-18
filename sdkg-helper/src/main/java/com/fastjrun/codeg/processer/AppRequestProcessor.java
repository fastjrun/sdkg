package com.fastjrun.codeg.processer;

import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JInvocation;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.JVar;

public class AppRequestProcessor extends BaseRequestProcessor {

    @Override
    public String processHTTPRequest(JMethod jcontrollerMethod, JInvocation jInvocation,
                                     MockModel mockModel, JCodeModel cm) {
        JBlock controllerMethodBlk = jcontrollerMethod.body();
        JVar requestHeadVar = controllerMethodBlk.decl(this.requestHeadClass, "requestHead",
                JExpr._new(this.requestHeadClass));
        JVar appKeyJVar = jcontrollerMethod.param(cm.ref("String"), "appKey");
        appKeyJVar.annotate(cm.ref("org.springframework.web.bind.annotation.PathVariable")).param("value",
                "appKey");
        controllerMethodBlk.add(JExpr.ref("requestHead").invoke("setAppKey").arg(JExpr.ref("appKey")));
        if (mockModel == MockModel.MockModel_Swagger) {
            appKeyJVar.annotate(cm.ref("io.swagger.annotations.ApiParam")).param("name", "appKey")
                    .param("value", "app分配的key").param("required", true);
        }
        JVar appVersionJVar = jcontrollerMethod.param(cm.ref("String"), "appVersion");
        appVersionJVar.annotate(cm.ref("org.springframework.web.bind.annotation.PathVariable"))
                .param("value", "appVersion");
        controllerMethodBlk.add(JExpr.ref("requestHead").invoke("setAppVersion").arg(JExpr.ref("appVersion")));
        if (mockModel == MockModel.MockModel_Swagger) {
            appVersionJVar.annotate(cm.ref("io.swagger.annotations.ApiParam")).param("name", "appVersion")
                    .param("value", "当前app版本号").param("required", true);
        }
        JVar appSourceJVar = jcontrollerMethod.param(cm.ref("String"), "appSource");
        appSourceJVar.annotate(cm.ref("org.springframework.web.bind.annotation.PathVariable"))
                .param("value", "appSource");
        controllerMethodBlk.add(JExpr.ref("requestHead").invoke("setAppSource").arg(JExpr.ref("appSource")));
        if (mockModel == MockModel.MockModel_Swagger) {
            appSourceJVar.annotate(cm.ref("io.swagger.annotations.ApiParam")).param("name", "appSource")
                    .param("value", "当前app渠道：ios,android").param("required", true);
        }
        JVar deviceIdJVar = jcontrollerMethod.param(cm.ref("String"), "deviceId");
        deviceIdJVar.annotate(cm.ref("org.springframework.web.bind.annotation.PathVariable")).param("value",
                "deviceId");
        controllerMethodBlk.add(JExpr.ref("requestHead").invoke("setDeviceId").arg(JExpr.ref("deviceId")));
        if (mockModel == MockModel.MockModel_Swagger) {
            deviceIdJVar.annotate(cm.ref("io.swagger.annotations.ApiParam")).param("name", "deviceId")
                    .param("value", "设备Id").param("required", true);
        }

        JVar txTimeJVar = jcontrollerMethod.param(cm.ref("Long"), "txTime");
        txTimeJVar.annotate(cm.ref("org.springframework.web.bind.annotation.PathVariable")).param("value",
                "txTime");
        controllerMethodBlk.add(JExpr.ref("requestHead").invoke("setTxTime").arg(JExpr.ref("txTime")));
        if (mockModel == MockModel.MockModel_Swagger) {
            txTimeJVar.annotate(cm.ref("io.swagger.annotations.ApiParam")).param("name", "txTime")
                    .param("value", "接口请求时间戳").param("required", true);
        }
        controllerMethodBlk.add(JExpr._this().invoke("processHead").arg(requestHeadVar));
        return "/{appKey}/{appVersion}/{appSource}/{deviceId}/{txTime}";
    }

    @Override
    public void processRPCRequest(JMethod method, JInvocation jInvocation, JCodeModel cm) {

    }
}
