package com.fastjrun.codeg.bundle;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import org.springframework.web.bind.annotation.RequestMethod;

import com.fastjrun.codeg.CodeGException;
import com.fastjrun.codeg.bundle.common.CommonController;
import com.fastjrun.codeg.bundle.common.CommonController.ControllerType;
import com.fastjrun.codeg.bundle.common.CommonMethod;
import com.fastjrun.codeg.bundle.common.CommonService;
import com.fastjrun.codeg.bundle.common.PacketField;
import com.fastjrun.codeg.bundle.common.PacketObject;
import com.fastjrun.codeg.helper.BundleXMLParser;
import com.sun.codemodel.CodeWriter;
import com.sun.codemodel.JAnnotationArrayMember;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;
import com.sun.codemodel.writer.FileCodeWriter;

public class BundleGenerator extends ServiceGenerator {

    Map<String, CommonController> controllerMap;

    Map<String, JClass> controllerClassMap;

    /**
     * @param commonController
     */
    private JClass processController(CommonController commonController) {
        ControllerType controllerType = commonController.getControllerType();
        if (!this.mock && controllerType == ControllerType.ControllerType_RPC) {
            return null;
        }
        try {
            JDefinedClass dcController;

            if (this.mock) {
                dcController = cm._class("com.fastjrun.mock.web.controller." + commonController.getName());
                dcController.annotate(cm.ref("io.swagger.annotations.Api")).param("value", commonController.getRemark())
                        .param("tags", commonController.getTags());
            } else {
                dcController = cm._class(this.packageNamePrefix + "web.controller." + commonController.getName());
            }
            JClass baseControllerClass = cm.ref("com.fastjrun.web.controller.BaseController");

            CommonService service = commonController.getService();

            if (controllerType == ControllerType.ControllerType_APP) {
                baseControllerClass = cm.ref("com.fastjrun.web.controller.BaseAppController");
            } else if (controllerType == ControllerType.ControllerType_API) {
                baseControllerClass = cm.ref("com.fastjrun.web.controller.BaseApiController");
            }

            dcController._extends(baseControllerClass);
            dcController.annotate(cm.ref("org.springframework.web.bind.annotation.RestController"));
            dcController.annotate(cm.ref("org.springframework.web.bind.annotation.RequestMapping")).param("value",
                    commonController.getPath());

            this.addClassDeclaration(dcController);

            String serviceName = commonController.getServiceName();
            JClass dcService = serviceClassMap.get(service.getName());
            JFieldVar fieldVar = dcController.field(JMod.PRIVATE, dcService, serviceName);

            fieldVar.annotate(cm.ref("org.springframework.beans.factory.annotation.Autowired"));
            fieldVar.annotate(cm.ref("org.springframework.beans.factory.annotation.Qualifier")).param("value",
                    service.getName());

            List<CommonMethod> methods = service.getMethods();
            for (CommonMethod method : methods) {
                PacketObject response;
                JType jResponseClass;
                String methodName = method.getName();
                String methodPath = method.getPath();
                if (methodPath == null || methodPath.equals("")) {
                    methodPath = "/" + methodName;
                }
                String methodRemark = method.getRemark();
                String methodVersion = method.getVersion();

                response = method.getResponse();

                JClass responseBodyClass;
                if (response == null) {
                    responseBodyClass = cm.ref("com.fastjrun.packet.EmptyResponseBody");
                } else {
                    String _class = response.get_class();
                    responseBodyClass = poClassMap.get(_class);

                }
                jResponseClass = cm.ref("com.fastjrun.packet.BaseDefaultResponse").narrow(responseBodyClass);
                RequestMethod requestMethod = RequestMethod.POST;
                switch (method.getHttpMethod().toUpperCase()) {
                    case "GET":
                        requestMethod = RequestMethod.GET;
                        break;
                    case "PUT":
                        requestMethod = RequestMethod.PUT;
                        break;
                    case "DELETE":
                        requestMethod = RequestMethod.DELETE;
                        break;
                    case "PATCH":
                        requestMethod = RequestMethod.PATCH;
                        break;
                    case "HEAD":
                        requestMethod = RequestMethod.HEAD;
                        break;
                    case "OPTIONS":
                        requestMethod = RequestMethod.OPTIONS;
                        break;
                    default:
                        break;
                }

                String methodNameAndVersion = methodName;
                if (methodVersion != null && !methodVersion.equals("")) {
                    methodNameAndVersion = methodName + methodVersion;
                }
                JMethod controllerMethod = dcController.method(JMod.NONE, jResponseClass, methodNameAndVersion);
                if (methodVersion != null && !methodVersion.equals("")) {
                    methodPath = methodPath + "/" + methodVersion;
                }

                JBlock controllerMethodBlk = controllerMethod.body();
                JClass longClass = cm.ref("Long");
                JClass requestHeadClass = cm
                        .ref("com.fastjrun.packet.Base" + controllerType.controllerType + "RequestHead");

                if (controllerType == ControllerType.ControllerType_API) {
                    JVar requestHeadVar = controllerMethodBlk.decl(requestHeadClass, "requestHead",
                            JExpr._new(requestHeadClass));
                    methodPath = methodPath + "/{accessKey}/{txTime}/{md5Hash}";
                    JVar accessKeyJVar = controllerMethod.param(cm.ref("String"), "accessKey");
                    accessKeyJVar.annotate(cm.ref("org.springframework.web.bind.annotation.PathVariable"))
                            .param("value", "accessKey");
                    controllerMethodBlk.invoke(JExpr.ref("requestHead"), "setAccessKey").arg(JExpr.ref("accessKey"));
                    if (this.isMock()) {
                        accessKeyJVar.annotate(cm.ref("io.swagger.annotations.ApiParam")).param("name", "accessKey")
                                .param("value", "接入客户端的accessKey").param("required", true);
                    }
                    JVar txTimeJVar = controllerMethod.param(longClass, "txTime");
                    txTimeJVar.annotate(cm.ref("org.springframework.web.bind.annotation.PathVariable")).param("value",
                            "txTime");
                    controllerMethodBlk.invoke(JExpr.ref("requestHead"), "setTxTime").arg(JExpr.ref("txTime"));
                    if (this.isMock()) {
                        txTimeJVar.annotate(cm.ref("io.swagger.annotations.ApiParam")).param("name", "txTime")
                                .param("value", "接口请求时间戳").param("required", true);
                    }
                    JVar md5HashJVar = controllerMethod.param(cm.ref("String"), "md5Hash");
                    md5HashJVar.annotate(cm.ref("org.springframework.web.bind.annotation.PathVariable")).param("value",
                            "md5Hash");
                    controllerMethodBlk.invoke(JExpr.ref("requestHead"), "setMd5Hash").arg(JExpr.ref("md5Hash"));
                    if (this.isMock()) {
                        md5HashJVar.annotate(cm.ref("io.swagger.annotations.ApiParam")).param("name", "md5Hash")
                                .param("value", "md5Hash").param("required", true);
                    }
                    controllerMethodBlk.invoke(JExpr._this(), "processHead").arg(requestHeadVar);
                } else if (controllerType == ControllerType.ControllerType_APP) {
                    JVar requestHeadVar = controllerMethodBlk.decl(requestHeadClass, "requestHead",
                            JExpr._new(requestHeadClass));
                    methodPath = methodPath + "/{appKey}/{appVersion}/{appSource}/{deviceId}/{txTime}";
                    JVar appKeyJVar = controllerMethod.param(cm.ref("String"), "appKey");
                    appKeyJVar.annotate(cm.ref("org.springframework.web.bind.annotation.PathVariable")).param("value",
                            "appKey");
                    controllerMethodBlk.invoke(JExpr.ref("requestHead"), "setAppKey").arg(JExpr.ref("appKey"));
                    if (this.isMock()) {
                        appKeyJVar.annotate(cm.ref("io.swagger.annotations.ApiParam")).param("name", "appKey")
                                .param("value", "app分配的key").param("required", true);
                    }
                    JVar appVersionJVar = controllerMethod.param(cm.ref("String"), "appVersion");
                    appVersionJVar.annotate(cm.ref("org.springframework.web.bind.annotation.PathVariable"))
                            .param("value", "appVersion");
                    controllerMethodBlk.invoke(JExpr.ref("requestHead"), "setAppVersion").arg(JExpr.ref("appVersion"));
                    if (this.isMock()) {
                        appVersionJVar.annotate(cm.ref("io.swagger.annotations.ApiParam")).param("name", "appVersion")
                                .param("value", "当前app版本号").param("required", true);
                    }
                    JVar appSourceJVar = controllerMethod.param(cm.ref("String"), "appSource");
                    appSourceJVar.annotate(cm.ref("org.springframework.web.bind.annotation.PathVariable"))
                            .param("value", "appSource");
                    controllerMethodBlk.invoke(JExpr.ref("requestHead"), "setAppSource").arg(JExpr.ref("appSource"));
                    if (this.isMock()) {
                        appSourceJVar.annotate(cm.ref("io.swagger.annotations.ApiParam")).param("name", "appSource")
                                .param("value", "当前app渠道：ios,android").param("required", true);
                    }
                    JVar deviceIdJVar = controllerMethod.param(cm.ref("String"), "deviceId");
                    deviceIdJVar.annotate(cm.ref("org.springframework.web.bind.annotation.PathVariable")).param("value",
                            "deviceId");
                    controllerMethodBlk.invoke(JExpr.ref("requestHead"), "setDeviceId").arg(JExpr.ref("deviceId"));
                    if (this.isMock()) {
                        deviceIdJVar.annotate(cm.ref("io.swagger.annotations.ApiParam")).param("name", "deviceId")
                                .param("value", "设备Id").param("required", true);
                    }

                    JVar txTimeJVar = controllerMethod.param(longClass, "txTime");
                    txTimeJVar.annotate(cm.ref("org.springframework.web.bind.annotation.PathVariable")).param("value",
                            "txTime");
                    controllerMethodBlk.invoke(JExpr.ref("requestHead"), "setTxTime").arg(JExpr.ref("txTime"));
                    controllerMethodBlk.invoke(JExpr._this(), "processHead").arg(requestHeadVar);
                    if (this.isMock()) {
                        txTimeJVar.annotate(cm.ref("io.swagger.annotations.ApiParam")).param("name", "txTime")
                                .param("value", "接口请求时间戳").param("required", true);
                    }
                }

                JInvocation jInvocation = JExpr.invoke(JExpr.refthis(serviceName), controllerMethod.name());
                if (this.mock) {
                    controllerMethod.annotate(cm.ref("io.swagger.annotations.ApiOperation"))
                            .param("value", methodRemark).param("notes", methodRemark);
                }
                List<PacketField> headVariables = method.getHeadVariables();
                if (headVariables != null && headVariables.size() > 0) {
                    for (int index = 0; index < headVariables.size(); index++) {
                        PacketField headVariable = headVariables.get(index);
                        JType jType = cm.ref(headVariable.getDatatype());
                        JVar headVariableJVar = controllerMethod.param(jType, headVariable.getName());
                        headVariableJVar.annotate(cm.ref("org.springframework.web.bind.annotation.RequestHeader"))
                                .param("name", headVariable.getName()).param("required", true);
                        if (this.mock) {
                            headVariableJVar.annotate(cm.ref("io.swagger.annotations.ApiParam"))
                                    .param("name", headVariable.getName()).param("value", headVariable.getRemark())
                                    .param("required", true);
                        }
                        jInvocation.arg(headVariableJVar);
                    }
                }
                List<PacketField> pathVariables = method.getPathVariables();
                if (pathVariables != null && pathVariables.size() > 0) {
                    for (int index = 0; index < pathVariables.size(); index++) {
                        PacketField pathVariable = pathVariables.get(index);
                        JType jType = cm.ref(pathVariable.getDatatype());
                        JVar pathVariableJVar = controllerMethod.param(jType, pathVariable.getName());
                        pathVariableJVar.annotate(cm.ref("org.springframework.web.bind.annotation.PathVariable"))
                                .param("name", pathVariable.getName()).param("required", true);
                        methodPath = methodPath + "/{" + pathVariable.getName() + "}";
                        if (this.mock) {
                            pathVariableJVar.annotate(cm.ref("io.swagger.annotations.ApiParam"))
                                    .param("name", pathVariable.getName()).param("value", pathVariable.getRemark())
                                    .param("required", true);
                        }
                        jInvocation.arg(pathVariableJVar);
                    }
                }
                List<PacketField> parameters = method.getParameters();
                if (parameters != null && parameters.size() > 0) {
                    for (int index = 0; index < parameters.size(); index++) {
                        PacketField parameter = parameters.get(index);
                        JClass jClass = cm.ref(parameter.getDatatype());
                        JVar parameterJVar = controllerMethod.param(jClass, parameter.getName());
                        parameterJVar.annotate(cm.ref("org.springframework.web.bind.annotation.RequestParam"))
                                .param("name", parameter.getName()).param("required", true);
                        if (this.mock) {
                            parameterJVar.annotate(cm.ref("io.swagger.annotations.ApiParam"))
                                    .param("name", parameter.getName()).param("value", parameter.getRemark())
                                    .param("required", true);
                        }

                        jInvocation.arg(parameterJVar);
                    }
                }
                JAnnotationUse jAnnotationUse = controllerMethod
                        .annotate(cm.ref("org.springframework.web.bind.annotation.RequestMapping"));
                jAnnotationUse.param("value", methodPath).param("method", requestMethod);

                String[] resTypes = method.getResType().split(",");
                if (resTypes.length == 1) {
                    jAnnotationUse.param("produces", resTypes[0]);
                } else {
                    JAnnotationArrayMember jAnnotationArrayMember = jAnnotationUse.paramArray("produces");
                    for (int i = 0; i < resTypes.length; i++) {
                        jAnnotationArrayMember.param(resTypes[i]);
                    }
                }

                PacketObject request = method.getRequest();

                if (request != null) {
                    JClass requestClass = this.poClassMap.get(request.get_class());
                    JVar requestParam = controllerMethod.param(requestClass, "request");
                    requestParam.annotate(cm.ref("org.springframework.web.bind.annotation.RequestBody"));
                    requestParam.annotate(cm.ref("javax.validation.Valid"));
                    jInvocation.arg(JExpr.ref("request"));

                }
                if (response == null) {
                    controllerMethodBlk.decl(jResponseClass, "response",
                            cm.ref("com.fastjrun.helper.BaseResponseHelper").staticInvoke("getSuccessResult"));
                } else {

                    controllerMethodBlk.decl(jResponseClass, "response", JExpr._new(jResponseClass));
                    JClass responseHeadClass = cm.ref("com.fastjrun.packet.BaseDefaultResponseHead");
                    JVar reponseHeadVar = controllerMethodBlk.decl(responseHeadClass, "responseHead",
                            JExpr._new(responseHeadClass));
                    controllerMethodBlk.invoke(reponseHeadVar, "setCode").arg(JExpr.lit("0000"));
                    controllerMethodBlk.invoke(reponseHeadVar, "setMsg").arg(JExpr.lit("Mock."));
                    controllerMethodBlk.invoke(JExpr.ref("response"), "setHead").arg(reponseHeadVar);
                    controllerMethodBlk.decl(responseBodyClass, "responseBody", jInvocation);
                    controllerMethodBlk.invoke(JExpr.ref("response"), "setBody").arg(JExpr.ref("responseBody"));
                }

                controllerMethodBlk.invoke(JExpr.ref("log"), "debug").arg(JExpr.ref("response"));
                controllerMethodBlk._return(JExpr.ref("response"));
            }
            return dcController;
        } catch (JClassAlreadyExistsException e) {
            throw new CodeGException("CG504", commonController.getName() + " create failed:" + e.getMessage());
        }

    }

    @Override
    public boolean generate() {
        this.beforeGenerate();
        if (this.bundleFiles != null && this.bundleFiles.length > 0) {
            BundleXMLParser.checkClassNameRepeat(bundleFiles);
            for (String bundleFile : bundleFiles) {
                this.generate(bundleFile);
            }
        }
        try {
            CodeWriter src = new FileCodeWriter(this.srcDir, "UTF-8");
            // 自上而下地生成类、方法等
            cm.build(src);
        } catch (IOException e) {
            throw new CodeGException("CG502", "code generating failed:" + e.getMessage());
        }
        return true;
    }

    private boolean generate(String bundleFile) {
        this.packetMap = BundleXMLParser.processPacket(bundleFile);
        this.generatePO();
        this.serviceMap = BundleXMLParser.processService(bundleFile, this.packetMap);
        this.generateService();
        this.controllerMap = BundleXMLParser.processControllers(bundleFile, serviceMap);
        this.generateController();
        return true;
    }

    private void generateController() {
        controllerClassMap = new HashMap<>();
        for (String key : this.controllerMap.keySet()) {
            CommonController commonController = this.controllerMap.get(key);
            Callable<JClass> task = new GeneratorControllerTask(this, commonController);
            FutureTask<JClass> future = new FutureTask<>(task);
            new Thread(future).start();
            try {
                controllerClassMap.put(key, future.get());
            } catch (Exception e) {
                log.error(commonController.getName() + " controller class add to map error:" + e.getMessage());
            }
        }

        this.waitForCodeGFinished(controllerClassMap);
    }

    private class GeneratorControllerTask implements Callable<JClass> {

        private BundleGenerator codeGenerator;

        private CommonController commonController;

        public GeneratorControllerTask(BundleGenerator codeGenerator, CommonController commonController) {
            this.codeGenerator = codeGenerator;
            this.commonController = commonController;
        }

        public JClass call() {
            return codeGenerator.processController(commonController);
        }

    }

}