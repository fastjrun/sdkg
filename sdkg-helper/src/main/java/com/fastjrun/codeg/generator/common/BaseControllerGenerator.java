package com.fastjrun.codeg.generator.common;

import java.util.Properties;

import com.fastjrun.codeg.common.CodeGException;
import com.fastjrun.codeg.common.CodeGMsgContants;
import com.fastjrun.codeg.common.CommonController;
import com.fastjrun.codeg.generator.ServiceGenerator;
import com.fastjrun.codeg.generator.method.BaseControllerMethodGenerator;
import com.fastjrun.codeg.generator.method.ServiceMethodGenerator;
import com.helger.jcodemodel.AbstractJClass;
import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JClassAlreadyExistsException;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JFieldVar;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.JMod;
import com.helger.jcodemodel.JVar;

public abstract class BaseControllerGenerator extends BaseCMGenerator {

    protected String webPackageName;

    protected CommonController commonController;

    protected String clientName;

    protected JDefinedClass clientClass;

    protected JDefinedClass clientTestClass;

    protected Properties clientTestParam;

    protected JDefinedClass controlllerClass;

    protected String controllerPath;

    protected ServiceGenerator serviceGenerator;

    public String getWebPackageName() {
        return webPackageName;
    }

    public void setWebPackageName(String webPackageName) {
        this.webPackageName = webPackageName;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getControllerPath() {
        return controllerPath;
    }

    public void setControllerPath(String controllerPath) {
        this.controllerPath = controllerPath;
    }

    public ServiceGenerator getServiceGenerator() {
        return serviceGenerator;
    }

    public void setServiceGenerator(ServiceGenerator serviceGenerator) {
        this.serviceGenerator = serviceGenerator;
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

    public CommonController getCommonController() {
        return commonController;
    }

    public void setCommonController(CommonController commonController) {
        this.commonController = commonController;
    }

    public JDefinedClass getControlllerClass() {
        return controlllerClass;
    }

    public void setControlllerClass(JDefinedClass controlllerClass) {
        this.controlllerClass = controlllerClass;
    }

    public abstract BaseControllerMethodGenerator prepareBaseControllerMethodGenerator(
            ServiceMethodGenerator serviceMethodGenerator);

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
            log.error(msg, e);
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
        JFieldVar fieldVar =
                this.controlllerClass.field(JMod.PRIVATE, this.serviceGenerator.getServiceClass(), serviceName);
        fieldVar.annotate(cm.ref("org.springframework.beans.factory.annotation.Autowired"));
        fieldVar.annotate(cm.ref("org.springframework.beans.factory.annotation.Qualifier")).param("value",
                commonController.getServiceRef());
    }

    protected void processClientTest() {
        try {
            this.clientTestClass = cmTest._class(this.getPackageNamePrefix() + "client." + this.clientName + "Test");
        } catch (JClassAlreadyExistsException e) {
            String msg = commonController.getName() + "Test is already exists.";
            log.error(msg, e);
            throw new CodeGException(CodeGMsgContants.CODEG_CLASS_EXISTS, msg, e);
        }
        this.clientTestClass._extends(cmTest.ref("com.fastjrun.client.BaseApplicationClientTest").narrow
                (this.clientClass));
        this.addClassDeclaration(this.clientTestClass);
        JMethod clientTestPrepareApplicationClientMethod =
                this.clientTestClass.method(JMod.PUBLIC, cmTest.VOID, "prepareApplicationClient");
        JVar jVarEnvName = clientTestPrepareApplicationClientMethod.param(cmTest.ref("String"), "envName");
        jVarEnvName.annotate(cmTest.ref("org.testng.annotations.Optional")).param("value", "unitTest");
        clientTestPrepareApplicationClientMethod.annotate(cmTest.ref("Override"));
        clientTestPrepareApplicationClientMethod.annotate(cmTest.ref("org.testng.annotations.BeforeTest"));
        clientTestPrepareApplicationClientMethod.annotate(cmTest.ref("org.testng.annotations.Parameters"))
                .paramArray("value").param("envName");
        JBlock jBlock = clientTestPrepareApplicationClientMethod.body();
        jBlock.assign(JExpr.ref("baseApplicationClient"), JExpr._new(this.clientClass));
        jBlock.add(JExpr._this().invoke("init").arg(jVarEnvName));
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
            log.error(msg, e);
            throw new CodeGException(CodeGMsgContants.CODEG_CLASS_EXISTS, msg, e);
        }

        AbstractJClass baseClientClass = cm.ref(controllerType.baseClient);

        AbstractJClass jParentClass = cm.ref("com.fastjrun.client.BaseApplicationClient").narrow(baseClientClass);

        this.addClassDeclaration(this.clientClass);

        this.clientClass._extends(jParentClass);
        this.clientClass._implements(this.serviceGenerator.getServiceClass());

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
}