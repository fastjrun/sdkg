package com.fastjrun.codeg.generator;

import java.util.List;

import com.fastjrun.codeg.common.CodeGException;
import com.fastjrun.codeg.common.CodeGMsgContants;
import com.fastjrun.codeg.common.CommonMethod;
import com.fastjrun.codeg.common.CommonService;
import com.sun.codemodel.ClassType;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMod;

public abstract class BaseRPCGenerator extends BaseControllerGenerator {

    protected String rpcApi = "api";

    protected String rpcBiz = "biz";

    protected JClass apiClass;

    protected JDefinedClass apiManagerClass;

    protected JDefinedClass apiControllerClass;

    public JDefinedClass getApiControllerClass() {
        return apiControllerClass;
    }

    public void setApiControllerClass(JDefinedClass apiControllerClass) {
        this.apiControllerClass = apiControllerClass;
    }

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
        if (controllerType.controllerProtocol == ControllerProtocol.ControllerProtocol_RPC) {
            JDefinedClass apiClassTemp;
            if (!commonController.is_new()) {
                this.apiClass = cm.ref(commonController.getName());
            } else {
                try {
                    apiClassTemp =
                            cm._class(this.packageNamePrefix + this.rpcApi + "." + commonController.getClientName(),
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
    }

    protected abstract void processApiMethod(CommonMethod method);

    protected void processAPIManager() {
        ControllerType controllerType = commonController.getControllerType();
        try {
            this.apiManagerClass = cm._class(this.packageNamePrefix + this.rpcBiz + "." + commonController.getName());
            if (controllerType.providerParentName != null && !controllerType.providerParentName.equals("")) {
                this.apiManagerClass._extends(cm.ref(controllerType.providerParentName));
            }
        } catch (JClassAlreadyExistsException e) {
            String msg = commonController.getName() + " is already exists.";
            this.commonLog.getLog().error(msg, e);
            throw new CodeGException(CodeGMsgContants.CODEG_CLASS_EXISTS, msg, e);
        }
        this.addClassDeclaration(this.apiManagerClass);
    }

    protected abstract void processAPIManagerMethod(CommonMethod method);

    protected void processControllerMock() {
        ControllerType controllerType = commonController.getControllerType();
        JClass baseControllerClass = cm.ref(controllerType.providerParentName);
        String controllerName = commonController.getName() + controllerType.providerSuffix;
        String controllerPackageName = this.mockPackageName + "." + this.webPackageName;
        try {
            this.apiControllerClass = cm._class(controllerPackageName + controllerName);
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

        this.apiControllerClass._extends(baseControllerClass);
        this.apiControllerClass.annotate(cm.ref("org.springframework.web.bind.annotation.RestController"));
        this.apiControllerClass.annotate(cm.ref("org.springframework.web.bind.annotation.RequestMapping"))
                .param("value", path);
        this.apiControllerClass.annotate(cm.ref("io.swagger.annotations.Api"))
                .param("value", commonController.getRemark())
                .param("tags", commonController.getTags());

        this.addClassDeclaration(this.apiControllerClass);
        CommonService service = commonController.getService();

        String serviceName = commonController.getServiceName();
        JFieldVar fieldVar = this.apiControllerClass.field(JMod.PRIVATE, this.serviceClass, serviceName);
        fieldVar.annotate(cm.ref("org.springframework.beans.factory.annotation.Autowired"));
        fieldVar.annotate(cm.ref("org.springframework.beans.factory.annotation.Qualifier")).param("value",
                service.getName());
    }

    protected abstract void processControllerMockMethod(CommonMethod method);

    @Override
    public void processProviderModule() {
        CommonService commonService = commonController.getService();
        this.processService(commonService);
        if (this.getMockModel() != MockModel.MockModel_Common) {
            this.processServiceMock(commonService);
            this.processControllerMock();
        }
        this.processAPI();
        this.processAPIManager();

        List<CommonMethod> methods = commonService.getMethods();
        for (CommonMethod method : methods) {
            this.processServiceMethod(method);
            this.processApiMethod(method);
            this.processAPIManagerMethod(method);
            if (this.getMockModel() != MockModel.MockModel_Common) {
                this.processServiceMockMethod(method);
                this.processControllerMockMethod(method);
            }
        }
    }

    @Override
    public void processApiModule() {
        CommonService commonService = commonController.getService();
        this.processService(commonService);
        this.processAPI();

        List<CommonMethod> methods = commonService.getMethods();
        for (CommonMethod method : methods) {
            this.processServiceMethod(method);
            this.processApiMethod(method);
        }
    }

}