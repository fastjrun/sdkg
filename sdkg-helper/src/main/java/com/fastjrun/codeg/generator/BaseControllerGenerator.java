package com.fastjrun.codeg.generator;

import java.util.Properties;

import com.fastjrun.codeg.common.CodeGException;
import com.fastjrun.codeg.common.CodeGMsgContants;
import com.fastjrun.codeg.common.CommonController;
import com.fastjrun.codeg.common.CommonService;
import com.fastjrun.codeg.generator.method.BaseControllerMethodGenerator;
import com.sun.codemodel.ClassType;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JVar;

public abstract class BaseControllerGenerator extends BaseCMGenerator {

    static String servicePackageName = "service.";

    static String webPackageName = "web.controller.";

    static String mockPackageName = "com.fastjrun.mock.";

    protected CommonController commonController;

    protected JDefinedClass serviceClass;
    protected JDefinedClass serviceMockClass;

    protected String clientName;

    protected JDefinedClass clientClass;

    protected JDefinedClass clientTestClass;

    protected Properties clientTestParam;

    protected JDefinedClass controlllerClass;

    protected String controllerPath;

    protected BaseControllerMethodGenerator baseControllerMethodGenerator;

    public BaseControllerMethodGenerator getBaseControllerMethodGenerator() {
        return baseControllerMethodGenerator;
    }

    public void setBaseControllerMethodGenerator(
            BaseControllerMethodGenerator baseControllerMethodGenerator) {
        this.baseControllerMethodGenerator = baseControllerMethodGenerator;
    }

    public JDefinedClass getClientClass() {
        return clientClass;
    }

    public void setClientClass(JDefinedClass clientClass) {
        this.clientClass = clientClass;
    }

    public JDefinedClass getClientTestClass() {
        return clientTestClass;
    }

    public void setClientTestClass(JDefinedClass clientTestClass) {
        this.clientTestClass = clientTestClass;
    }

    public Properties getClientTestParam() {
        return clientTestParam;
    }

    public void setClientTestParam(Properties clientTestParam) {
        this.clientTestParam = clientTestParam;
    }

    public JDefinedClass getServiceMockClass() {
        return serviceMockClass;
    }

    public void setServiceMockClass(JDefinedClass serviceMockClass) {
        this.serviceMockClass = serviceMockClass;
    }

    public CommonController getCommonController() {
        return commonController;
    }

    public void setCommonController(CommonController commonController) {
        this.commonController = commonController;
    }

    public JDefinedClass getServiceClass() {
        return serviceClass;
    }

    public void setServiceClass(JDefinedClass serviceClass) {
        this.serviceClass = serviceClass;
    }

    public JDefinedClass getControlllerClass() {
        return controlllerClass;
    }

    public void setControlllerClass(JDefinedClass controlllerClass) {
        this.controlllerClass = controlllerClass;
    }

    protected void processController() {
        ControllerType controllerType = commonController.getControllerType();

        String controllerPackageName = this.packageNamePrefix + this.webPackageName;

        if (this.getMockModel() != MockModel.MockModel_Common) {
            controllerPackageName = this.mockPackageName + this.webPackageName;
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
        this.controlllerClass.annotate(cm.ref("org.springframework.web.bind.annotation.RestController"));
        this.controlllerClass.annotate(cm.ref("org.springframework.web.bind.annotation.RequestMapping"))
                .param("value", this.controllerPath);
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

    protected void processService() {
        CommonService commonService = commonController.getService();
        try {
            this.serviceClass = cm._class(this.packageNamePrefix + this.servicePackageName + commonService.get_class(),
                    ClassType.INTERFACE);
        } catch (JClassAlreadyExistsException e) {
            String msg = commonService.get_class() + " is already exists.";
            this.commonLog.getLog().error(msg, e);
            throw new CodeGException(CodeGMsgContants.CODEG_CLASS_EXISTS, msg, e);
        }
        this.addClassDeclaration(this.serviceClass);
    }

    protected void processServiceMock() {
        CommonService commonService = this.commonController.getService();
        try {
            this.serviceMockClass = cm._class(mockPackageName + commonService.get_class() + "Mock");
            serviceMockClass._implements(this.serviceClass);
            serviceMockClass.annotate(cm.ref("org.springframework.stereotype.Service")).param("value",
                    commonService.getName());
        } catch (JClassAlreadyExistsException e) {
            String msg = commonService.get_class() + " is already exists.";
            this.commonLog.getLog().error(msg, e);
            throw new CodeGException(CodeGMsgContants.CODEG_CLASS_EXISTS, msg, e);
        }
        this.addClassDeclaration(this.serviceMockClass);
    }

    protected void processClientTest() {
        try {
            this.clientTestClass = cmTest._class(this.getPackageNamePrefix() + "client." + this.clientName + "Test");
        } catch (JClassAlreadyExistsException e) {
            String msg = commonController.getName() + "Test is already exists.";
            this.commonLog.getLog().error(msg, e);
            throw new CodeGException(CodeGMsgContants.CODEG_CLASS_EXISTS, msg, e);
        }
        this.clientTestClass._extends(cmTest.ref("com.fastjrun.client.BaseApplicationClientTest").narrow
                (this.clientClass));
        this.addClassDeclaration(this.clientTestClass);
        JMethod clientTestPrepareApplicationClientMethod =
                this.clientTestClass.method(JMod.PUBLIC, cmTest.VOID, "prepareApplicationClient");
        JVar jVarEnvName = clientTestPrepareApplicationClientMethod.param(cmTest.ref("String"), "envName");
        clientTestPrepareApplicationClientMethod.annotate(cmTest.ref("Override"));
        clientTestPrepareApplicationClientMethod.annotate(cmTest.ref("org.testng.annotations.BeforeTest"));
        clientTestPrepareApplicationClientMethod.annotate(cmTest.ref("org.testng.annotations.Parameters"))
                .paramArray("value").param("envName");
        JBlock jBlock = clientTestPrepareApplicationClientMethod.body();
        jBlock.assign(JExpr.ref("baseApplicationClient"), JExpr._new(this.clientClass));
        jBlock.invoke(JExpr._this(), "init").arg(jVarEnvName);
    }

    protected void processClient() {
        ControllerType controllerType = commonController.getControllerType();
        this.clientName = commonController.getClientName();
        int lastDotIndex = clientName.lastIndexOf(".");
        if (lastDotIndex > 0) {
            this.clientName = clientName.substring(lastDotIndex + 1, clientName.length());
        }

        this.clientName = this.clientName + controllerType.clientSuffix;
        try {
            this.clientClass = cm
                    ._class(this.getPackageNamePrefix() + "client." + this.clientName);
        } catch (JClassAlreadyExistsException e) {
            String msg = commonController.getName() + " is already exists.";
            this.commonLog.getLog().error(msg, e);
            throw new CodeGException(CodeGMsgContants.CODEG_CLASS_EXISTS, msg, e);
        }

        JClass baseClientClass = cm.ref(controllerType.baseClient);

        JClass jParentClass = cm.ref("com.fastjrun.client.BaseApplicationClient").narrow(baseClientClass);

        this.addClassDeclaration(this.clientClass);

        this.clientClass._extends(jParentClass);
        this.clientClass._implements(this.serviceClass);

        JMethod clientInitMethod = clientClass.method(JMod.PUBLIC, cm.VOID, "initSDKConfig");
        clientInitMethod.annotate(cm.ref("Override"));
        JBlock jInitBlock = clientInitMethod.body();
        jInitBlock.assign(JExpr.ref("baseClient"), JExpr._new(baseClientClass));
        jInitBlock.invoke(JExpr.ref("baseClient"), "initSDKConfig");
    }

    protected void genreateControllerPath() {
        this.controllerPath = commonController.getPath();
        String version = commonController.getVersion();
        if (version != null && !version.equals("")) {
            this.controllerPath = this.controllerPath + "/" + version;
        }
    }

    public abstract void processProviderModule();

    public abstract void processApiModule();
}