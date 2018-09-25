package com.fastjrun.codeg.generator;

import java.util.List;
import java.util.Properties;

import com.fastjrun.codeg.common.CodeGException;
import com.fastjrun.codeg.common.CodeGMsgContants;
import com.fastjrun.codeg.common.CommonMethod;
import com.fastjrun.codeg.common.CommonService;
import com.fastjrun.codeg.common.PacketField;
import com.fastjrun.codeg.common.PacketObject;
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

public abstract class BaseHTTPGenerator extends BaseControllerGenerator {

    protected JDefinedClass controlllerClass;
    String controllerPackageName = "com.fastjrun.mock.web.controller.";

    public JDefinedClass getControlllerClass() {
        return controlllerClass;
    }

    public void setControlllerClass(JDefinedClass controlllerClass) {
        this.controlllerClass = controlllerClass;
    }

    protected abstract String processRequestHead(ControllerType controllerType, JMethod controllerMethod, String
            methodPath);

    @Override
    public void processProviderModule() {
        CommonService commonService = commonController.getService();
        this.processService(commonService);
        if (this.getMockModel() != MockModel.MockModel_Common) {
            this.processServiceMock(commonService);
        }
        this.processController();

        List<CommonMethod> methods = commonService.getMethods();
        for (CommonMethod method : methods) {
            this.processServiceMethod(method);
            if (this.getMockModel() != MockModel.MockModel_Common) {
                this.processServiceMockMethod(method);
            }
            this.processControllerMethod(method);
        }
    }

    protected void processController() {
        ControllerType controllerType = commonController.getControllerType();

        String controllerPackageName = this.packageNamePrefix + this.webPackageName;

        if (this.getMockModel() != MockModel.MockModel_Common) {
            controllerPackageName = this.mockPackageName + "." + this.webPackageName;
        }

        String controllerName = commonController.getName() + controllerType.providerSuffix;

        try {
            this.controlllerClass = cm._class(controllerPackageName + controllerName);
            if (controllerType.providerParentName != null && !controllerType.providerParentName.equals("")) {
                this.controlllerClass._extends(cm.ref(controllerType.providerParentName));
            }

        } catch (JClassAlreadyExistsException e) {
            String msg = commonController.getName() + " is already exists.";
            this.commonLog.getLog().error(msg, e);
            throw new CodeGException(CodeGMsgContants.CODEG_CLASS_EXISTS, msg, e);
        }

        String path = commonController.getPath();
        String version = commonController.getVersion();
        if (version != null && !version.equals("")) {
            path = path + "/" + version;
        }
        this.controlllerClass.annotate(cm.ref("org.springframework.web.bind.annotation.RestController"));
        this.controlllerClass.annotate(cm.ref("org.springframework.web.bind.annotation.RequestMapping"))
                .param("value", path);
        if (this.getMockModel() == MockModel.MockModel_Swagger) {
            this.controlllerClass.annotate(cm.ref("io.swagger.annotations.Api"))
                    .param("value", commonController.getRemark())
                    .param("tags", commonController.getTags());
        }
        this.addClassDeclaration(this.controlllerClass);

        String serviceName = commonController.getServiceName();
        JFieldVar fieldVar = this.controlllerClass.field(JMod.PRIVATE, this.serviceClass, serviceName);
        fieldVar.annotate(cm.ref("org.springframework.beans.factory.annotation.Autowired"));
        fieldVar.annotate(cm.ref("org.springframework.beans.factory.annotation.Qualifier")).param("value",
                commonController.getServiceRef());
    }

    protected void processControllerMethod(CommonMethod method) {

        //TODO

    }

    @Override
    public void processApiModule() {
        this.processClient();
        this.processClientTest();
        this.clientTestParam = new Properties();
        CommonService commonService = commonController.getService();
        List<CommonMethod> methods = commonService.getMethods();
        for (CommonMethod method : methods) {
            this.processServiceMethod(method);
            this.processClientMethod(method);
            this.processClientTestMethod(method);
            this.processClientTestPraram(method);
        }

    }

    @Override
    protected void processClient() {
        ControllerType controllerType = commonController.getControllerType();
        String clientName = commonController.getClientName();
        int lastDotIndex = clientName.lastIndexOf(".");
        if (lastDotIndex > 0) {
            clientName = clientName.substring(lastDotIndex + 1, clientName.length());
        }

        this.clientName = clientName + controllerType.clientSuffix;
        try {
            this.clientClass = cm
                    ._class(this.getPackageNamePrefix() + "client." + this.clientName);
        } catch (JClassAlreadyExistsException e) {
            String msg = commonController.getName() + " is already exists.";
            this.commonLog.getLog().error(msg, e);
            throw new CodeGException(CodeGMsgContants.CODEG_CLASS_EXISTS, msg, e);
        }

        CommonService service = commonController.getService();
        JClass jParentClass = cm.ref("com.fastjrun.client.BaseApplicationClient")
                .narrow(cm.ref("com.fastjrun.client.BaseHttpResponseHandleClient"));

        this.addClassDeclaration(this.clientClass);
        this.clientClass._extends(jParentClass);
        JClass dcService = cm.ref(this.packageNamePrefix + "service." + service.get_class());
        this.clientClass._implements(dcService);

        JClass baseClientClass = cm.ref("com.fastjrun.client.DefaultAppClient");

        JMethod clientInitMethod = this.clientClass.method(JMod.PUBLIC, cm.VOID, "initSDKConfig");
        clientInitMethod.annotate(cm.ref("Override"));
        JBlock jInitBlock = clientInitMethod.body();
        jInitBlock.assign(JExpr.ref("baseClient"), JExpr._new(baseClientClass));
        jInitBlock.invoke(JExpr.ref("baseClient"), "initSDKConfig");
    }

    protected void processClientMethod(CommonMethod method) {
        String methodName = method.getName();
        String methodRemark = method.getRemark();

        String methodVersion = method.getVersion();
        if (methodVersion != null && !methodVersion.equals("")) {
            methodName = methodName + methodVersion;
        }

        JType responseBodyClass = this.parseResponseBodyClassFromCommonMethod(method);

        JMethod clientMethod = this.clientClass.method(JMod.PUBLIC, responseBodyClass, methodName);

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

        JClass stringClass = cm.ref("String");

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
                    cm.ref("com.fastjrun.dto.EmptyBody");
            JVar requestBodyVar = methodBlk.decl(jRequestBodyClass, "requestBody",
                    JExpr._new(jRequestBodyClass));
            jInvocation.arg(requestBodyVar);

        }

        if (responseBodyClass != cm.VOID) {
            jInvocation.arg(JExpr.dotclass((JClass) responseBodyClass));
            methodBlk._return(jInvocation);
        } else {
            methodBlk.add(jInvocation);
        }
    }

}