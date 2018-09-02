package com.fastjrun.codeg.generator;

import java.util.List;

import org.springframework.web.bind.annotation.RequestMethod;

import com.fastjrun.codeg.common.CodeGException;
import com.fastjrun.codeg.common.CodeGMsgContants;
import com.fastjrun.codeg.common.CommonController;
import com.fastjrun.codeg.common.CommonMethod;
import com.fastjrun.codeg.common.CommonService;
import com.fastjrun.codeg.common.PacketField;
import com.fastjrun.codeg.common.PacketObject;
import com.sun.codemodel.JAnnotationArrayMember;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;

public abstract class BaseHTTPGenerator extends BaseClientGenerator {

    protected boolean igorePOService = false;

    public boolean isIgorePOService() {
        return igorePOService;
    }

    public void setIgorePOService(boolean igorePOService) {
        this.igorePOService = igorePOService;
    }

    protected abstract String processRequestHead(ControllerType controllerType, JMethod controllerMethod, String
            methodPath);

    @Override
    public JDefinedClass processController(CommonController commonController) {
        String controllerPackageName = "com.fastjrun.mock.web.controller.";
        ControllerType controllerType = commonController.getControllerType();
        JClass baseControllerClass = cm.ref(controllerType.parentName);

        String path = commonController.getPath();
        String version = commonController.getVersion();
        if (version != null && !version.equals("")) {
            path = path + "/" + version;
        }
        String controllerName = commonController.getName() + controllerType.controllerSuffix;
        if (this.getMockModel() == MockModel.MockModel_Common) {
            controllerPackageName = this.packageNamePrefix + "web.controller.";
        }
        JDefinedClass dcController;
        try {
            dcController = cm._class(controllerPackageName + controllerName);
        } catch (JClassAlreadyExistsException e) {
            String msg = commonController.getName() + " is already exists.";
            this.commonLog.getLog().error(msg, e);
            throw new CodeGException(CodeGMsgContants.CODEG_CLASS_EXISTS, msg, e);
        }

        dcController._extends(baseControllerClass);
        dcController.annotate(cm.ref("org.springframework.web.bind.annotation.RestController"));
        dcController.annotate(cm.ref("org.springframework.web.bind.annotation.RequestMapping"))
                .param("value", path);
        if (this.getMockModel() == MockModel.MockModel_Swagger) {
            dcController.annotate(cm.ref("io.swagger.annotations.Api")).param("value", commonController.getRemark())
                    .param("tags", commonController.getTags());
        }

        this.addClassDeclaration(dcController);

        CommonService service = commonController.getService();

        String serviceName = commonController.getServiceName();
        JClass dcService = cm.ref(this.packageNamePrefix + "service." + service.get_class());
        JFieldVar fieldVar = dcController.field(JMod.PRIVATE, dcService, serviceName);
        fieldVar.annotate(cm.ref("org.springframework.beans.factory.annotation.Autowired"));
        fieldVar.annotate(cm.ref("org.springframework.beans.factory.annotation.Qualifier")).param("value",
                service.getName());

        List<CommonMethod> methods = service.getMethods();
        for (CommonMethod method : methods) {
            PacketObject response;
            JClass jResponseClass;
            String methodName = method.getName();
            String methodPath = method.getPath();
            if (methodPath == null || methodPath.equals("")) {
                methodPath = "/" + methodName;
            }
            String methodRemark = method.getRemark();
            String methodVersion = method.getVersion();

            response = method.getResponse();
            String responseClassP;

            JClass responseBodyClass;
            JClass baseResponseClass = cm.ref("com.fastjrun.packet.DefaultResponse");
            if (method.isResponseIsArray()) {
                baseResponseClass = cm.ref("com.fastjrun.packet.DefaultListResponse");
            }
            if (response == null) {
                responseBodyClass = cm.ref("com.fastjrun.packet.EmptyBody");
                jResponseClass = baseResponseClass.narrow(responseBodyClass);
            } else {
                responseClassP = response.get_class();
                if (method.isResponseIsArray()) {
                    responseBodyClass =
                            cm.ref("java.util.List").narrow(cm.ref(this.packageNamePrefix + responseClassP));
                } else {
                    responseBodyClass = cm.ref(this.packageNamePrefix + responseClassP);
                }

                jResponseClass = baseResponseClass.narrow(cm.ref(this.packageNamePrefix + responseClassP));
            }

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
            JMethod controllerMethod = dcController.method(JMod.PUBLIC, jResponseClass, methodNameAndVersion);
            controllerMethod.javadoc().append(methodRemark);
            if (methodVersion != null && !methodVersion.equals("")) {
                methodPath = methodPath + "/" + methodVersion;
            }

            JBlock controllerMethodBlk = controllerMethod.body();

            methodPath = this.processRequestHead(controllerType, controllerMethod, methodPath);

            JInvocation jInvocation = JExpr.invoke(JExpr.refthis(serviceName), methodNameAndVersion);

            if (this.getMockModel() == MockModel.MockModel_Swagger) {
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
                    jInvocation.arg(headVariableJVar);
                    if (this.getMockModel() == MockModel.MockModel_Swagger) {
                        headVariableJVar.annotate(cm.ref("io.swagger.annotations.ApiParam"))
                                .param("name", headVariable.getName()).param("value", headVariable.getRemark())
                                .param("required", true);
                    }
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

                    jInvocation.arg(pathVariableJVar);
                    if (this.getMockModel() == MockModel.MockModel_Swagger) {
                        pathVariableJVar.annotate(cm.ref("io.swagger.annotations.ApiParam"))
                                .param("name", pathVariable.getName()).param("value", pathVariable.getRemark())
                                .param("required", true);
                    }
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

                    jInvocation.arg(parameterJVar);
                    if (this.getMockModel() == MockModel.MockModel_Swagger) {
                        parameterJVar.annotate(cm.ref("io.swagger.annotations.ApiParam"))
                                .param("name", parameter.getName()).param("value", parameter.getRemark())
                                .param("required", true);
                    }
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
                JClass requestClass = cm.ref(this.packageNamePrefix + request.get_class());
                JVar requestParam = controllerMethod.param(requestClass, "request");
                requestParam.annotate(cm.ref("org.springframework.web.bind.annotation.RequestBody"));
                requestParam.annotate(cm.ref("javax.validation.Valid"));
                jInvocation.arg(JExpr.ref("request"));

            }
            if (response == null) {
                String responseHelperMethodName = "getSuccessResult";
                if (method.isResponseIsArray()) {
                    responseHelperMethodName = "getSuccessResultList";
                }
                controllerMethodBlk.decl(jResponseClass, "response",
                        cm.ref("com.fastjrun.helper.BaseResponseHelper")
                                .staticInvoke("getSuccessResult"));
            } else {
                String responseHelperMethodName = "getResult";
                if (method.isResponseIsArray()) {
                    responseHelperMethodName = "getResultList";
                }

                controllerMethodBlk.decl(jResponseClass, "response",
                        cm.ref("com.fastjrun.helper.BaseResponseHelper")
                                .staticInvoke(responseHelperMethodName));
                controllerMethodBlk.decl(responseBodyClass, "responseBody", jInvocation);
                controllerMethodBlk.invoke(JExpr.ref("response"), "setBody").arg(JExpr.ref("responseBody"));
            }

            controllerMethodBlk.invoke(JExpr.ref("log"), "debug").arg(JExpr.ref("response"));
            controllerMethodBlk._return(JExpr.ref("response"));
        }
        return dcController;
    }

    @Override
    public JDefinedClass processClient(CommonController commonController) {
        JDefinedClass clientClass = null;
        try {
            clientClass = cm
                    ._class(this.getPackageNamePrefix() + "client." + commonController.getClientName());
        } catch (JClassAlreadyExistsException e) {
            String msg = commonController.getName() + " is already exists.";
            this.commonLog.getLog().error(msg, e);
            throw new CodeGException(CodeGMsgContants.CODEG_CLASS_EXISTS, msg, e);
        }

        JClass stringClass = cm.ref("String");

        CommonService service = commonController.getService();
        JClass jParentClass = cm.ref("com.fastjrun.client.BaseApplicationClient")
                .narrow(cm.ref("com.fastjrun.client.BaseHttpResponseHandleClient"));

        this.addClassDeclaration(clientClass);
        clientClass._extends(jParentClass);
        JClass dcService = cm.ref(this.packageNamePrefix + "service." + service.get_class());
        clientClass._implements(dcService);

        JClass baseClientClass = cm.ref("com.fastjrun.client.DefaultAppClient");

        JMethod clientInitMethod = clientClass.method(JMod.PUBLIC, cm.VOID, "initSDKConfig");
        clientInitMethod.annotate(cm.ref("Override"));
        JBlock jInitBlock = clientInitMethod.body();
        jInitBlock.assign(JExpr.ref("baseClient"), JExpr._new(baseClientClass));
        jInitBlock.invoke(JExpr.ref("baseClient"), "initSDKConfig");

        List<CommonMethod> methods = service.getMethods();
        for (CommonMethod method : methods) {
            PacketObject response;
            String methodName = method.getName();
            String methodRemark = method.getRemark();

            String methodVersion = method.getVersion();
            if (methodVersion != null && !methodVersion.equals("")) {
                methodName = methodName + methodVersion;
            }
            response = method.getResponse();
            String responseClassP;

            JType responseBodyClass;
            if (response == null) {
                responseBodyClass = cm.VOID;
                responseClassP = "";
            } else {
                responseClassP = response.get_class();
                if (method.isResponseIsArray()) {
                    responseBodyClass =
                            cm.ref("java.util.List").narrow(cm.ref(this.packageNamePrefix + responseClassP));
                } else {
                    responseBodyClass = cm.ref(this.packageNamePrefix + responseClassP);
                }
            }

            JMethod clientMethod = clientClass.method(JMod.PUBLIC, responseBodyClass, methodName);

            clientMethod.javadoc().append(methodRemark);

            JBlock methodBlk = clientMethod.body();

            String methodPath = method.getPath();
            if (methodPath == null || methodPath.equals("")) {
                methodPath = "/" + methodName;
            }
            if (methodVersion != null && !methodVersion.equals("")) {
                methodPath += "/" + methodVersion;
            }
            String invokeMethodName;

            if (method.isResponseIsArray()) {
                invokeMethodName = "processList";
            } else {
                invokeMethodName = "process";
            }
            JInvocation jInvocation = JExpr.invoke(JExpr.ref("baseClient"), invokeMethodName);

            // headParams
            List<PacketField> headVariables = method.getHeadVariables();

            JVar headParamsJvar = null;
            if (headVariables != null && headVariables.size() > 0) {
                headParamsJvar = methodBlk.decl(
                        cm.ref("java.util.Map").narrow(stringClass).narrow(stringClass), "headParams",
                        JExpr._new(cm.ref("java.util.HashMap").narrow(stringClass).narrow(stringClass)));
                for (int index = 0; index < headVariables.size(); index++) {
                    PacketField headVariable = headVariables.get(index);
                    JClass jClass = cm.ref(headVariable.getDatatype());
                    clientMethod.param(jClass, headVariable.getNameAlias());
                    methodBlk.invoke(headParamsJvar, "put").arg(JExpr.lit(headVariable.getName()))
                            .arg(JExpr.ref(headVariable.getNameAlias()));

                    methodBlk.invoke(JExpr.ref("log"), "debug").arg(JExpr.lit("header[{}] = {}"))
                            .arg(JExpr.lit(headVariable.getNameAlias()))
                            .arg(JExpr.ref(headVariable.getNameAlias()));

                }

            }
            // path
            JClass stringBuilderClass = cm.ref("StringBuilder");
            JVar pathVar = methodBlk.decl(stringBuilderClass, "path",
                    JExpr._new(stringBuilderClass).arg(JExpr.lit(commonController.getPath() + methodPath)));
            List<PacketField> pathVariables = method.getPathVariables();
            if (pathVariables != null && pathVariables.size() > 0) {
                for (int index = 0; index < pathVariables.size(); index++) {
                    PacketField pathVariable = pathVariables.get(index);
                    JClass jClass = cm.ref(pathVariable.getDatatype());
                    clientMethod.param(jClass, pathVariable.getName());

                    methodBlk.invoke(pathVar, "append").arg(JExpr.lit("/"));
                    methodBlk.invoke(pathVar, "append").arg(JExpr.ref(pathVariable.getName()));

                    methodBlk.invoke(JExpr.ref("log"), "debug").arg(JExpr.lit("pathVariable[{}] = {}"))
                            .arg(JExpr.lit(pathVariable.getName())).arg(JExpr.ref(pathVariable.getName()));

                }
            }
            jInvocation.arg(pathVar.invoke("toString"));

            methodBlk.invoke(JExpr.ref("log"), "debug").arg(JExpr.lit("path = {}")).arg(pathVar.invoke("toString"));
            // method
            jInvocation.arg(JExpr.lit(method.getHttpMethod().toUpperCase()));

            methodBlk.invoke(JExpr.ref("log"), "debug")
                    .arg(JExpr.lit("method = " + method.getHttpMethod().toUpperCase()));

            List<PacketField> parameters = method.getParameters();
            if (parameters != null && parameters.size() > 0) {
                // queryParams
                JVar queryParamsJvar = methodBlk.decl(
                        cm.ref("java.util.Map").narrow(stringClass).narrow(stringClass), "queryParams",
                        JExpr._new(cm.ref("java.util.HashMap").narrow(stringClass).narrow(stringClass)));
                for (int index = 0; index < parameters.size(); index++) {
                    PacketField parameter = parameters.get(index);
                    JClass jClass = cm.ref(parameter.getDatatype());
                    clientMethod.param(jClass, parameter.getName());

                    JExpression jInvocationParameter = JExpr.ref(parameter.getName());

                    if (jClass != stringClass) {
                        jInvocationParameter = stringClass.staticInvoke("valueOf").arg(jInvocationParameter);
                    }

                    methodBlk.invoke(queryParamsJvar, "put").arg(JExpr.lit(parameter.getName()))
                            .arg(jInvocationParameter);

                    methodBlk.invoke(JExpr.ref("log"), "debug").arg(JExpr.lit("paramter[{}] = {}"))
                            .arg(JExpr.lit(parameter.getName())).arg(JExpr.ref(parameter.getName()));

                }

                jInvocation.arg(queryParamsJvar);
            } else {
                jInvocation.arg(JExpr._null());
            }

            if (headParamsJvar != null) {
                jInvocation.arg(headParamsJvar);
            } else {
                jInvocation.arg(JExpr._null());
            }
            List<PacketField> cookies = method.getCookieVariables();
            if (cookies != null && cookies.size() > 0) {
                // cookies
                JVar cookieJvar = methodBlk.decl(
                        cm.ref("java.util.Map").narrow(stringClass).narrow(stringClass), "cookieParams",
                        JExpr._new(cm.ref("java.util.HashMap").narrow(stringClass).narrow(stringClass)));
                for (int index = 0; index < cookies.size(); index++) {
                    PacketField cookie = cookies.get(index);
                    JClass jClass = cm.ref(cookie.getDatatype());
                    clientMethod.param(jClass, cookie.getName());

                    JExpression jInvocationCookie = JExpr.ref(cookie.getName());

                    if (jClass != stringClass) {
                        jInvocationCookie = stringClass.staticInvoke("valueOf").arg(jInvocationCookie);
                    }

                    methodBlk.invoke(cookieJvar, "put").arg(JExpr.lit(cookie.getName()))
                            .arg(jInvocationCookie);

                    methodBlk.invoke(JExpr.ref("log"), "debug").arg(JExpr.lit("paramter[{}] = {}"))
                            .arg(JExpr.lit(cookie.getName())).arg(JExpr.ref(cookie.getName()));

                    JClass jType = cmTest.ref(cookie.getDatatype());

                }
                jInvocation.arg(cookieJvar);
            } else {
                jInvocation.arg(JExpr._null());
            }

            // requestBody
            PacketObject requestBody = method.getRequest();
            if (requestBody != null) {
                JClass requestBodyClass = cm.ref(this.packageNamePrefix + requestBody.get_class());

                JVar jRequestBodyVar = clientMethod.param(requestBodyClass, "requestBody");
                jInvocation.arg(jRequestBodyVar);

            } else {

                JClass jRequestBodyClass =
                        cm.ref("com.fastjrun.packet.EmptyBody");
                JVar requestBodyVar = methodBlk.decl(jRequestBodyClass, "requestBody",
                        JExpr._new(jRequestBodyClass));
                jInvocation.arg(requestBodyVar);

            }

            if (responseBodyClass != cm.VOID) {
                jInvocation.arg(JExpr.dotclass((JClass) cm.ref(this.packageNamePrefix + responseClassP)));
                methodBlk._return(jInvocation);

            } else {
                methodBlk.add(jInvocation);
            }

        }

        return clientClass;
    }

    @Override
    public void generate() {
        if (!igorePOService) {
            if (this.packetMap != null && this.packetMap.size() > 0) {
                this.generatePO();
            }
            if (this.serviceMap != null && this.serviceMap.size() > 0) {
                this.generateService();
            }
        }

        if (this.controllerMap != null) {
            if (!this.isClient()) {
                this.generateController();
            } else {
                this.generateClient();
            }
        }
    }

}