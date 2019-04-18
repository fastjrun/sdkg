package com.fastjrun.codeg.generator;

import java.util.Properties;

import com.fastjrun.codeg.common.CodeGException;
import com.fastjrun.codeg.common.CodeGMsgContants;
import com.fastjrun.codeg.generator.common.BaseControllerGenerator;
import com.fastjrun.helper.StringHelper;
import com.helger.jcodemodel.AbstractJClass;
import com.helger.jcodemodel.EClassType;
import com.helger.jcodemodel.JClassAlreadyExistsException;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JFieldVar;
import com.helger.jcodemodel.JMod;

public abstract class BaseRPCGenerator extends BaseControllerGenerator {

    static String rpcApi = "api.";

    static String rpcBiz = "biz.";

    protected AbstractJClass apiClass;

    protected JDefinedClass apiManagerClass;

    public AbstractJClass getApiClass() {
        return apiClass;
    }

    public void setApiClass(AbstractJClass apiClass) {
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
                                EClassType.INTERFACE);
                if (controllerType.apiParentName != null && !controllerType.apiParentName.equals("")) {
                    apiClassTemp._implements(cm.ref(controllerType.apiParentName));
                }

            } catch (JClassAlreadyExistsException e) {
                String msg = commonController.getClientName() + " is already exists.";
                log.error(msg, e);
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
            log.error(msg, e);
            throw new CodeGException(CodeGMsgContants.CODEG_CLASS_EXISTS, msg, e);
        }
        this.apiManagerClass._implements(this.apiClass);
        this.apiManagerClass.annotate(cm.ref("org.springframework.stereotype.Service"))
                .param("value", StringHelper.toLowerCaseFirstOne(commonController.getClientName())
                );
        this.addClassDeclaration(this.apiManagerClass);

        String serviceName = commonController.getServiceName();
        JFieldVar fieldVar =
                this.apiManagerClass.field(JMod.PRIVATE, this.serviceGenerator.getServiceClass(), serviceName);
        fieldVar.annotate(cm.ref("org.springframework.beans.factory.annotation.Autowired"));
        fieldVar.annotate(cm.ref("org.springframework.beans.factory.annotation.Qualifier")).param("value",
                commonController.getServiceRef());
    }

    @Override
    public void generate() {
        this.processAPI();
        if (!this.isApi()) {
            if (this.isClient()) {
                this.processClient();
                this.processClientTest();
                this.clientTestParam = new Properties();
            } else {
                this.processAPIManager();
                if (this.getMockModel() != MockModel.MockModel_Common) {
                    this.genreateControllerPath();
                    this.processController();
                }
            }
        }
    }

}