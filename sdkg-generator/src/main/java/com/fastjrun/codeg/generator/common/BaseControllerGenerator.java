/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.codeg.generator.common;

import com.fastjrun.codeg.common.*;
import com.fastjrun.codeg.generator.BaseServiceGenerator;
import com.fastjrun.codeg.generator.method.BaseControllerMethodGenerator;
import com.fastjrun.codeg.generator.method.BaseServiceMethodGenerator;
import com.helger.jcodemodel.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class BaseControllerGenerator extends BaseCMGenerator {

    static final String WEB_PACKAGE_NAME = "web.controller.";

    protected CommonController commonController;

    protected String clientName;

    protected JDefinedClass clientClass;

    protected JDefinedClass controlllerClass;

    protected String controllerPath;

    protected BaseServiceGenerator serviceGenerator;

    public abstract BaseControllerMethodGenerator prepareBaseControllerMethodGenerator(
      BaseServiceMethodGenerator baseServiceMethodGenerator);

    protected void processController() {
        ControllerType controllerType = commonController.getControllerType();

        String controllerPackageName = this.packageNamePrefix + WEB_PACKAGE_NAME;

        if (this.isMock()) {
            controllerPackageName = MOCK_PACKAGE_NAME + WEB_PACKAGE_NAME;
        }

        String controllerName = commonController.getName();

        try {
            this.controlllerClass = cm._class(controllerPackageName + controllerName);
            if (controllerType.providerParentName != null && !controllerType.providerParentName.equals(
              "")) {
                this.controlllerClass._extends(cm.ref(controllerType.providerParentName));
            }

        } catch (JCodeModelException e) {
            String msg = commonController.getName() + " is already exists.";
            log.error(msg, e);
            throw new CodeGException(CodeGMsgContants.CODEG_CLASS_EXISTS, msg, e);
        }
        this.controlllerClass.annotate(
          cm.ref(controllerType.baseControllerName));
        this.controlllerClass.annotate(
          cm.ref("org.springframework.web.bind.annotation.RequestMapping")).param("value",
          this.controllerPath);
        if(this.swaggerVersion==SwaggerVersion.Swagger2){
            this.controlllerClass.annotate(cm.ref("io.swagger.annotations.Api")).param("value",
                    commonController.getRemark()).paramArray("tags", commonController.getTags());
        }else if(this.swaggerVersion==SwaggerVersion.Swagger3){
            this.controlllerClass.annotate(cm.ref("io.swagger.v3.oas.annotations.tags.Tag"))
                    .param("name", commonController.getRemark());
        }

        this.addClassDeclaration(this.controlllerClass);

        String serviceName = commonController.getServiceName();
        JFieldVar fieldVar =
          this.controlllerClass.field(JMod.PRIVATE, this.serviceGenerator.getServiceClass(),
            serviceName);
        fieldVar.annotate(cm.ref("org.springframework.beans.factory.annotation.Autowired"));
        fieldVar.annotate(cm.ref("org.springframework.beans.factory.annotation.Qualifier")).param(
          "value", commonController.getServiceRef());
    }

    protected void genreateControllerPath() {
        this.controllerPath = commonController.getPath();
        String version = commonController.getVersion();
        if (version != null && !version.equals("")) {
            this.controllerPath = this.controllerPath + "/" + version;
        }
    }

    protected void generatorControllerMethod() {
        for (CommonMethod commonMethod : this.commonController.getService().getMethods()) {
            BaseServiceMethodGenerator serviceMethodGenerator =
              this.serviceGenerator.getServiceMethodGeneratorMap().get(commonMethod);
            BaseControllerMethodGenerator baseControllerMethodGenerator =
              this.prepareBaseControllerMethodGenerator(serviceMethodGenerator);
            baseControllerMethodGenerator.setApi(this.isApi());
            baseControllerMethodGenerator.setCm(cm);
            baseControllerMethodGenerator.generate();
        }
    }
}
