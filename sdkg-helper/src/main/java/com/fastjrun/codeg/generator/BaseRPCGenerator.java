package com.fastjrun.codeg.generator;

import java.util.List;
import java.util.Properties;

import org.dom4j.Document;

import com.fastjrun.codeg.common.CodeGException;
import com.fastjrun.codeg.common.CodeGMsgContants;
import com.fastjrun.codeg.common.CommonMethod;
import com.fastjrun.codeg.generator.method.BaseRPCMethodGenerator;
import com.fastjrun.codeg.helper.StringHelper;
import com.sun.codemodel.ClassType;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMod;

public abstract class BaseRPCGenerator extends BaseControllerGenerator {

    static String rpcApi = "api.";

    static String rpcBiz = "biz.";

    protected JClass apiClass;

    protected JDefinedClass apiManagerClass;

    protected Document clientXml;

    protected Document serverXml;

    public JClass getApiClass() {
        return apiClass;
    }

    public void setApiClass(JClass apiClass) {
        this.apiClass = apiClass;
    }

    public JDefinedClass getApiManagerClass() {
        return apiManagerClass;
    }

    public void setApiManagerClass(JDefinedClass apiManagerClass) {
        this.apiManagerClass = apiManagerClass;
    }

    protected void processAPI() {
        ControllerType controllerType = this.commonController.getControllerType();
        JDefinedClass apiClassTemp;
        if (!commonController.is_new()) {
            this.apiClass = cm.ref(commonController.getName());
        } else {
            try {
                apiClassTemp =
                        cm._class(this.packageNamePrefix + rpcApi + commonController.getClientName(),
                                ClassType.INTERFACE);
                if (controllerType.apiParentName != null && !controllerType.apiParentName.equals("")) {
                    apiClassTemp._implements(cm.ref(controllerType.apiParentName));
                }

            } catch (JClassAlreadyExistsException e) {
                String msg = commonController.getClientName() + " is already exists.";
                this.commonLog.getLog().error(msg, e);
                throw new CodeGException(CodeGMsgContants.CODEG_CLASS_EXISTS, msg, e);
            }
            this.addClassDeclaration(apiClassTemp);
            this.apiClass = apiClassTemp;
        }
    }

    protected void processAPIManager() {
        ControllerType controllerType = commonController.getControllerType();
        try {
            this.apiManagerClass = cm._class(this.packageNamePrefix + rpcBiz + commonController.getName());
            if (controllerType.providerParentName != null && !controllerType.providerParentName.equals("")) {
                this.apiManagerClass._extends(cm.ref(controllerType.providerParentName));
            }
        } catch (JClassAlreadyExistsException e) {
            String msg = commonController.getName() + " is already exists.";
            this.commonLog.getLog().error(msg, e);
            throw new CodeGException(CodeGMsgContants.CODEG_CLASS_EXISTS, msg, e);
        }
        this.apiManagerClass._implements(this.apiClass);
        this.apiManagerClass.annotate(cm.ref("org.springframework.stereotype.Service"))
                .param("value", StringHelper.toLowerCaseFirstOne(commonController.getClientName())
                );
        this.addClassDeclaration(this.apiManagerClass);

        String serviceName = commonController.getServiceName();
        JFieldVar fieldVar = this.apiManagerClass.field(JMod.PRIVATE, this.serviceClass, serviceName);
        fieldVar.annotate(cm.ref("org.springframework.beans.factory.annotation.Autowired"));
        fieldVar.annotate(cm.ref("org.springframework.beans.factory.annotation.Qualifier")).param("value",
                commonController.getServiceRef());
    }

    @Override
    public void processProviderModule() {
        this.processService();
        if (this.getMockModel() != MockModel.MockModel_Common) {
            this.processServiceMock();
            this.genreateControllerPath();
            this.processController();
        }
        this.processAPI();
        this.processAPIManager();

        List<CommonMethod> commonMethods = this.commonController.getService().getMethods();
        for (CommonMethod commonMethod : commonMethods) {
            BaseRPCMethodGenerator baseRPCMethodGenerator = null;
            try {
                baseRPCMethodGenerator = (BaseRPCMethodGenerator) this.baseControllerMethodGenerator.clone();
                baseRPCMethodGenerator.setCommonMethod(commonMethod);
            } catch (CloneNotSupportedException e) {
                throw new CodeGException(CodeGMsgContants.CODEG_NOT_SUPPORT, "不支持这个生成器", e);
            }
            baseRPCMethodGenerator.processServiceMethod(this.serviceClass);
            if (this.commonController.is_new()) {
                baseRPCMethodGenerator.processApiMethod((JDefinedClass) this.apiClass);
            }
            baseRPCMethodGenerator.processApiManagerMethod(commonController, this.apiManagerClass);
            if (this.getMockModel() != MockModel.MockModel_Common) {
                baseRPCMethodGenerator.processServiceMockMethod(this.serviceMockClass);
                baseRPCMethodGenerator.processControllerMethod(commonController, this.controlllerClass);
            }
        }
    }

    @Override
    public void processApiModule() {
        this.processService();
        this.processAPI();
        this.processClient();
        this.processClientTest();
        this.clientTestParam = new Properties();

        List<CommonMethod> commonMethods = this.commonController.getService().getMethods();
        for (CommonMethod commonMethod : commonMethods) {
            BaseRPCMethodGenerator baseRPCMethodGenerator = null;
            try {
                baseRPCMethodGenerator = (BaseRPCMethodGenerator) this.baseControllerMethodGenerator.clone();
                baseRPCMethodGenerator.setCommonMethod(commonMethod);
            } catch (CloneNotSupportedException e) {
                throw new CodeGException(CodeGMsgContants.CODEG_NOT_SUPPORT, "不支持这个生成器", e);
            }
            baseRPCMethodGenerator.processServiceMethod(this.serviceClass);
            if (this.commonController.is_new()) {
                baseRPCMethodGenerator.processApiMethod((JDefinedClass) this.apiClass);
            }
            baseRPCMethodGenerator.processClientMethod(this.apiClass, this.clientClass);
            baseRPCMethodGenerator.processClientTestMethod(this.clientTestClass);
            StringBuilder sb = new StringBuilder(this.clientName).append(".test");
            sb.append(baseRPCMethodGenerator.getMethodName()).append(".n");
            baseRPCMethodGenerator.processClientTestPraram();
            this.clientTestParam.put(sb.toString(), baseRPCMethodGenerator.getMethodParamInJsonObject()
                    .toString().replaceAll("\n", "").replaceAll("\r", "")
                    .trim());
        }
    }

}