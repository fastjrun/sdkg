
package com.fastjrun.codeg.generator;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.springframework.web.bind.annotation.RequestMethod;

import com.fastjrun.codeg.common.CodeGException;
import com.fastjrun.codeg.common.CodeGMsgContants;
import com.fastjrun.codeg.common.CommonController;
import com.fastjrun.codeg.common.CommonMethod;
import com.fastjrun.codeg.common.CommonService;
import com.fastjrun.codeg.common.PacketField;
import com.fastjrun.codeg.common.PacketObject;
import com.fastjrun.codeg.helper.StringHelper;
import com.sun.codemodel.ClassType;
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

public abstract class BaseRPCGenerator extends BaseClientGenerator {

    protected String rpcApi = "api";

    protected String rpcBiz = "biz";

    protected Document clientXml;
    protected Document serverXml;

    public Document getClientXml() {
        return clientXml;
    }

    public void setClientXml(Document clientXml) {
        this.clientXml = clientXml;
    }

    public Document getServerXml() {
        return serverXml;
    }

    public void setServerXml(Document serverXml) {
        this.serverXml = serverXml;
    }

    @Override
    public JDefinedClass processController(CommonController commonController) {
        if (this.getMockModel() == MockModel.MockModel_Common) {
            return null;
        }
        String controllerPackageName = "com.fastjrun.mock.web.controller.";
        CommonController.ControllerType controllerType = commonController.getControllerType();
        JClass baseControllerClass = cm.ref(controllerType.parentName);

        String path = commonController.getPath();
        String version = commonController.getVersion();
        if (version != null && !version.equals("")) {
            path = path + "/" + version;
        }
        String controllerName = commonController.getName() + controllerType.controllerSuffix;

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
            JClass baseResponseClass = cm.ref("com.com.fastjrun.packet.DefaultResponse");
            if (response == null) {
                responseBodyClass = cm.ref("com.com.fastjrun.packet.EmptyBody");
                responseClassP = "";

                jResponseClass = baseResponseClass.narrow(responseBodyClass);
            } else {
                responseClassP = response.get_class();
                if (method.isResponseIsArray()) {
                    responseBodyClass =
                            cm.ref("java.util.List").narrow(cm.ref(this.packageNamePrefix + responseClassP));
                    baseResponseClass = cm.ref("com.fastjrun.packet.DefaultListResponse");
                } else {
                    responseBodyClass = cm.ref(this.packageNamePrefix + responseClassP);
                }

                jResponseClass = baseResponseClass.narrow(cm.ref(this.packageNamePrefix + responseClassP));
            }

            RequestMethod requestMethod = RequestMethod.POST;

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
                controllerMethodBlk.decl(responseBodyClass, "responseData", jInvocation);
                controllerMethodBlk.invoke(JExpr.ref("response"), "setData").arg(JExpr.ref("responseData"));
            }

            controllerMethodBlk.invoke(JExpr.ref("log"), "debug").arg(JExpr.ref("response"));
            controllerMethodBlk._return(JExpr.ref("response"));
        }
        return dcController;
    }

    public JDefinedClass processAPIManager(CommonController commonController) {
        JDefinedClass apiManagerClass;
        try {

            apiManagerClass = cm._class(this.packageNamePrefix + this.rpcBiz + "." + commonController.getName())
                    ._extends(cm.ref(" com.fastjrun.biz.BaseDefaultApiManager"));
        } catch (JClassAlreadyExistsException e) {
            String msg = commonController.getName() + " is already exists.";
            this.commonLog.getLog().error(msg, e);
            throw new CodeGException(CodeGMsgContants.CODEG_CLASS_EXISTS, msg, e);
        }

        String version = commonController.getVersion();
        JClass apiJClass = cm.ref(this.packageNamePrefix + this.rpcApi + "." + commonController.getClientName());
        apiManagerClass._implements(apiJClass);
        apiManagerClass.annotate(cm.ref("org.springframework.stereotype.Service"))
                .param("value", StringHelper.toLowerCaseFirstOne(commonController.getClientName())
                );

        this.addClassDeclaration(apiManagerClass);

        CommonService service = commonController.getService();

        String serviceName = commonController.getServiceName();
        JClass dcService = cm.ref(this.packageNamePrefix + "service." + service.get_class());
        if (apiManagerClass != null) {
            JFieldVar fieldApiVar = apiManagerClass.field(JMod.PRIVATE, dcService, serviceName);
            fieldApiVar.annotate(cm.ref("org.springframework.beans.factory.annotation.Autowired"));
            fieldApiVar.annotate(cm.ref("org.springframework.beans.factory.annotation.Qualifier")).param("value",
                    service.getName());
        }

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
            if (response == null) {
                responseBodyClass = cm.ref("com.fastjrun.packet.EmptyBody");
                responseClassP = "";

                jResponseClass = baseResponseClass.narrow(responseBodyClass);
            } else {
                responseClassP = response.get_class();
                if (method.isResponseIsArray()) {
                    responseBodyClass =
                            cm.ref("java.util.List").narrow(cm.ref(this.packageNamePrefix + responseClassP));
                    baseResponseClass = cm.ref("com.fastjrun.packet.DefaultListResponse");
                } else {
                    responseBodyClass = cm.ref(this.packageNamePrefix + responseClassP);
                }

                jResponseClass = baseResponseClass.narrow(cm.ref(this.packageNamePrefix + responseClassP));
            }

            String methodNameAndVersion = methodName;
            if (methodVersion != null && !methodVersion.equals("")) {
                methodNameAndVersion = methodName + methodVersion;
            }
            JMethod apiManagerMethod = apiManagerClass.method(JMod.PUBLIC, jResponseClass, methodNameAndVersion);
            apiManagerMethod.javadoc().append(methodRemark);

            if (methodVersion != null && !methodVersion.equals("")) {
                methodPath = methodPath + "/" + methodVersion;
            }

            JInvocation jInvocation = JExpr.invoke(JExpr.refthis(serviceName), methodNameAndVersion);

            List<PacketField> headVariables = method.getHeadVariables();
            if (headVariables != null && headVariables.size() > 0) {
                for (int index = 0; index < headVariables.size(); index++) {
                    PacketField headVariable = headVariables.get(index);
                    JType jType = cm.ref(headVariable.getDatatype());
                    JVar headVariableJVar = apiManagerMethod.param(jType, headVariable.getName());
                    jInvocation.arg(headVariableJVar);
                }
            }
            List<PacketField> pathVariables = method.getPathVariables();
            if (pathVariables != null && pathVariables.size() > 0) {
                for (int index = 0; index < pathVariables.size(); index++) {
                    PacketField pathVariable = pathVariables.get(index);
                    JType jType = cm.ref(pathVariable.getDatatype());
                    JVar pathVariableJVar = apiManagerMethod.param(jType, pathVariable.getName());
                    jInvocation.arg(pathVariableJVar);
                }
            }
            List<PacketField> parameters = method.getParameters();
            if (parameters != null && parameters.size() > 0) {
                for (int index = 0; index < parameters.size(); index++) {
                    PacketField parameter = parameters.get(index);
                    JClass jClass = cm.ref(parameter.getDatatype());
                    JVar parameterJVar = apiManagerMethod.param(jClass, parameter.getName());
                    jInvocation.arg(parameterJVar);
                }
            }

            String[] resTypes = method.getResType().split(",");

            PacketObject request = method.getRequest();

            if (request != null) {
                JClass requestClass = cm.ref(this.packageNamePrefix + request.get_class());
                if (apiManagerMethod != null) {
                    apiManagerMethod.param(requestClass, "request");
                }
                jInvocation.arg(JExpr.ref("request"));

            }
            if (response == null) {

                if (apiManagerMethod != null) {
                    apiManagerMethod.body().decl(jResponseClass, "response",
                            cm.ref("com.fastjrun.helper.BaseResponseHelper")
                                    .staticInvoke("getSuccessResult"));
                }
            } else {
                String responseHelperMethodName = "getResult";
                if (method.isResponseIsArray()) {
                    responseHelperMethodName = "getResultList";
                }

                if (apiManagerMethod != null) {
                    apiManagerMethod.body().decl(jResponseClass, "response",
                            cm.ref("com.fastjrun.helper.BaseResponseHelper")
                                    .staticInvoke(responseHelperMethodName));
                    apiManagerMethod.body().decl(responseBodyClass, "responseData", jInvocation);
                    apiManagerMethod.body().invoke(JExpr.ref("response"), "setData").arg(JExpr.ref("responseData"));
                }
            }
            if (apiManagerMethod != null) {
                apiManagerMethod.body().invoke(JExpr.ref("log"), "debug").arg(JExpr.ref("response"));
                apiManagerMethod.body()._return(JExpr.ref("response"));
            }
        }
        return apiManagerClass;

    }

    @Override
    public JDefinedClass processClient(CommonController commonController) {
        JDefinedClass clientClass = null;
        CommonController.ControllerType controllerType = commonController.getControllerType();
        String clientName = commonController.getClientName() + controllerType.clientSuffix;

        try {
            clientClass = cm
                    ._class(this.getPackageNamePrefix() + "client." + clientName);
        } catch (JClassAlreadyExistsException e) {
            String msg = commonController.getName() + " is already exists.";
            this.commonLog.getLog().error(msg, e);
            throw new CodeGException(CodeGMsgContants.CODEG_CLASS_EXISTS, msg, e);
        }

        JClass stringClass = cm.ref("String");

        CommonService service = commonController.getService();
        JClass jParentClass = cm.ref("com.fastjrun.client.BaseApplicationClient")
                .narrow(cm.ref("com.fastjrun.client.DefaultPRCClient"));

        this.addClassDeclaration(clientClass);
        clientClass._extends(jParentClass);
        JClass dcService = cm.ref(this.packageNamePrefix + "service." + service.get_class());
        clientClass._implements(dcService);

        JClass baseClientClass = cm.ref("com.fastjrun.client.DefaultPRCClient");

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

            jInvocation.arg(JExpr.dotclass(cm.ref(this.packageNamePrefix + this.rpcApi + "." + commonController
                    .getClientName()
            )));

            jInvocation.arg(JExpr.lit(methodName));

            List<JClass> paramterTypes = new ArrayList<>();

            List<JVar> paramterJVars = new ArrayList<>();

            // headParams
            List<PacketField> headVariables = method.getHeadVariables();

            JVar headParamsJvar = null;
            if (headVariables != null && headVariables.size() > 0) {
                for (int index = 0; index < headVariables.size(); index++) {
                    PacketField headVariable = headVariables.get(index);
                    JClass jClass = cm.ref(headVariable.getDatatype());
                    JVar headJVar = clientMethod.param(jClass, headVariable.getNameAlias());
                    paramterTypes.add(jClass);
                    paramterJVars.add(headJVar);

                    methodBlk.invoke(JExpr.ref("log"), "debug").arg(JExpr.lit("header[{}] = {}"))
                            .arg(JExpr.lit(headVariable.getNameAlias()))
                            .arg(JExpr.ref(headVariable.getNameAlias()));

                }

            }
            // path
            JClass stringBuilderClass = cm.ref("StringBuilder");
            List<PacketField> pathVariables = method.getPathVariables();
            if (pathVariables != null && pathVariables.size() > 0) {
                for (int index = 0; index < pathVariables.size(); index++) {
                    PacketField pathVariable = pathVariables.get(index);
                    JClass jClass = cm.ref(pathVariable.getDatatype());
                    JVar pathVariableVar = clientMethod.param(jClass, pathVariable.getName());
                    paramterTypes.add(jClass);
                    paramterJVars.add(pathVariableVar);

                    methodBlk.invoke(JExpr.ref("log"), "debug").arg(JExpr.lit("pathVariable[{}] = {}"))
                            .arg(JExpr.lit(pathVariable.getName())).arg(JExpr.ref(pathVariable.getName()));

                }
            }

            List<PacketField> parameters = method.getParameters();
            if (parameters != null && parameters.size() > 0) {
                for (int index = 0; index < parameters.size(); index++) {
                    PacketField parameter = parameters.get(index);
                    JClass jClass = cm.ref(parameter.getDatatype());
                    JVar parameterVar = clientMethod.param(jClass, parameter.getName());
                    paramterTypes.add(jClass);
                    paramterJVars.add(parameterVar);

                    methodBlk.invoke(JExpr.ref("log"), "debug").arg(JExpr.lit("paramter[{}] = {}"))
                            .arg(JExpr.lit(parameter.getName())).arg(JExpr.ref(parameter.getName()));

                }
            }

            List<PacketField> cookies = method.getCookieVariables();
            if (cookies != null && cookies.size() > 0) {
                for (int index = 0; index < cookies.size(); index++) {
                    PacketField cookie = cookies.get(index);
                    JClass jClass = cm.ref(cookie.getDatatype());
                    JVar cookieVar = clientMethod.param(jClass, cookie.getName());
                    paramterTypes.add(jClass);
                    paramterJVars.add(cookieVar);

                    JExpression jInvocationCookie = JExpr.ref(cookie.getName());
                    methodBlk.invoke(JExpr.ref("log"), "debug").arg(JExpr.lit("paramter[{}] = {}"))
                            .arg(JExpr.lit(cookie.getName())).arg(JExpr.ref(cookie.getName()));

                }
            }

            // requestBody
            PacketObject requestBody = method.getRequest();
            if (requestBody != null) {
                JClass requestBodyClass = cm.ref(this.packageNamePrefix + requestBody.get_class());

                JVar jRequestBodyVar = clientMethod.param(requestBodyClass, "requestBody");
                paramterTypes.add(requestBodyClass);
                paramterJVars.add(jRequestBodyVar);

            }

            if (paramterTypes.size() > 0) {
                JVar paramterTypesJVar = methodBlk.decl(cm.ref("Class").array(), "paramterTypes", JExpr.newArray(cm.ref
                        ("Class"), paramterTypes.size()));
                JVar paramterValuesJVar = methodBlk.decl(cm.ref("Object").array(), "paramterValues", JExpr.newArray(cm
                        .ref
                                ("Object"), paramterJVars.size()));
                for (int i = 0; i < paramterJVars.size(); i++) {
                    methodBlk.assign(paramterTypesJVar.component(JExpr.lit(i)), paramterJVars.get(i).invoke
                            ("getClass"));
                    methodBlk.assign(paramterValuesJVar.component(JExpr.lit(i)), paramterJVars.get(i));
                }
                jInvocation.arg(paramterTypesJVar);
                jInvocation.arg(paramterValuesJVar);
            }

            if (responseBodyClass != cm.VOID) {
                methodBlk._return(jInvocation);

            } else {
                methodBlk.add(jInvocation);
            }

        }

        return clientClass;

    }

    public JDefinedClass processAPI(CommonController commonController) {
        JDefinedClass apiClass;
        try {
            apiClass = cm._class(this.packageNamePrefix + this.rpcApi + "." + commonController.getClientName(),
                    ClassType.INTERFACE)._implements(cm
                    .ref("com.fasjtrun.api.BaseDefaultApi"));
        } catch (JClassAlreadyExistsException e) {
            String msg = commonController.getClientName() + " is already exists.";
            this.commonLog.getLog().error(msg, e);
            throw new CodeGException(CodeGMsgContants.CODEG_CLASS_EXISTS, msg, e);
        }
        this.addClassDeclaration(apiClass);

        CommonService service = commonController.getService();

        List<CommonMethod> methods = service.getMethods();
        for (CommonMethod method : methods) {
            PacketObject response;
            JClass jResponseClass;
            String methodName = method.getName();
            String methodRemark = method.getRemark();
            String methodVersion = method.getVersion();

            response = method.getResponse();
            String responseClassP;

            JClass responseBodyClass;
            JClass baseResponseClass = cm.ref("com.fastjrun.packet.DefaultResponse");
            if (response == null) {
                responseBodyClass = cm.ref("com.fastjrun.packet.EmptyBody");

                jResponseClass = baseResponseClass.narrow(responseBodyClass);
            } else {
                responseClassP = response.get_class();
                if (method.isResponseIsArray()) {
                    baseResponseClass = cm.ref("com.fastjrun.packet.DefaultListResponse");
                }

                jResponseClass = baseResponseClass.narrow(cm.ref(this.packageNamePrefix + responseClassP));
            }

            String methodNameAndVersion = methodName;
            if (methodVersion != null && !methodVersion.equals("")) {
                methodNameAndVersion = methodName + methodVersion;
            }
            JMethod apiMethod = null;
            if (apiClass != null) {
                apiMethod = apiClass.method(JMod.NONE, jResponseClass, methodNameAndVersion);
                apiMethod.javadoc().append(methodRemark);
            }
            List<PacketField> headVariables = method.getHeadVariables();
            if (headVariables != null && headVariables.size() > 0) {
                for (int index = 0; index < headVariables.size(); index++) {
                    PacketField headVariable = headVariables.get(index);
                    JType jType = cm.ref(headVariable.getDatatype());
                    if (apiMethod != null) {
                        apiMethod.param(jType, headVariable.getName());
                    }
                }
            }
            List<PacketField> pathVariables = method.getPathVariables();
            if (pathVariables != null && pathVariables.size() > 0) {
                for (int index = 0; index < pathVariables.size(); index++) {
                    PacketField pathVariable = pathVariables.get(index);
                    JType jType = cm.ref(pathVariable.getDatatype());
                    if (apiMethod != null) {
                        apiMethod.param(jType, pathVariable.getName());
                    }
                }
            }
            List<PacketField> parameters = method.getParameters();
            if (parameters != null && parameters.size() > 0) {
                for (int index = 0; index < parameters.size(); index++) {
                    PacketField parameter = parameters.get(index);
                    JClass jClass = cm.ref(parameter.getDatatype());
                    if (apiMethod != null) {
                        apiMethod.param(jClass, parameter.getName());
                    }
                }
            }
            PacketObject request = method.getRequest();
            if (request != null) {
                JClass requestClass = cm.ref(this.packageNamePrefix + request.get_class());
                if (apiMethod != null) {
                    apiMethod.param(requestClass, "request");
                }
            }

        }
        return apiClass;

    }

    protected abstract void generateClientXml();

    protected abstract void generateServerXml();

    @Override
    public void generate() {
        if (this.packetMap != null && this.packetMap.size() > 0) {
            this.generatePO();
        }
        if (this.serviceMap != null && this.serviceMap.size() > 0) {
            this.generateService();
        }
        if (this.controllerMap != null) {
            if (!this.isClient()) {
                this.generateAPI();
                this.generateAPIManager();
                if (this.getMockModel() != MockModel.MockModel_Common) {
                    this.generateController();
                }
            } else {
                this.generateAPI();
                this.generateClient();
            }

        }
    }

    protected boolean generateAPIManager() {
        for (String key : this.controllerMap.keySet()) {
            CommonController commonController = this.controllerMap.get(key);
            this.processAPIManager(commonController);
        }
        if (!this.isClient()) {
            this.generateServerXml();
        }
        return true;
    }

    protected boolean generateAPI() {
        for (String key : this.controllerMap.keySet()) {
            CommonController commonController = this.controllerMap.get(key);
            JDefinedClass jDc = this.processAPI(commonController);
        }
        if (this.isClient()) {
            this.generateClientXml();
        }
        return true;
    }
}