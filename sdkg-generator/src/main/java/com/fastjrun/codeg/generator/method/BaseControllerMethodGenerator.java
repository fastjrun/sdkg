/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.codeg.generator.method;

import com.fastjrun.codeg.common.CommonController;
import com.fastjrun.codeg.common.PacketField;
import com.fastjrun.codeg.generator.common.BaseControllerGenerator;
import com.fastjrun.codeg.processor.ExchangeProcessor;
import com.fastjrun.codeg.helper.StringHelper;
import com.helger.jcodemodel.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

public abstract class BaseControllerMethodGenerator extends AbstractMethodGenerator {

    protected JMethod jClientMethod;

    protected JMethod jcontrollerMethod;

    protected ExchangeProcessor exchangeProcessor;

    protected BaseServiceMethodGenerator serviceMethodGenerator;

    protected BaseControllerGenerator baseControllerGenerator;

    public void setServiceMethodGenerator(BaseServiceMethodGenerator serviceMethodGenerator) {
        this.serviceMethodGenerator = serviceMethodGenerator;
    }

    public void setBaseControllerGenerator(BaseControllerGenerator baseControllerGenerator) {
        this.baseControllerGenerator = baseControllerGenerator;
    }

    public void setExchangeProcessor(ExchangeProcessor exchangeProcessor) {
        this.exchangeProcessor = exchangeProcessor;
    }

    public JMethod getjClientMethod() {
        return jClientMethod;
    }

    public void setjClientMethod(JMethod jClientMethod) {
        this.jClientMethod = jClientMethod;
    }

    public JMethod getJcontrollerMethod() {
        return jcontrollerMethod;
    }

    public void setJcontrollerMethod(JMethod jcontrollerMethod) {
        this.jcontrollerMethod = jcontrollerMethod;
    }

    public void processControllerMethod(
            CommonController commonController, JDefinedClass controllerClass) {

        RequestMethod requestMethod = RequestMethod.POST;
        switch (this.serviceMethodGenerator.getCommonMethod().getHttpMethod().toUpperCase()) {
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
        this.jcontrollerMethod =
                controllerClass.method(
                        JMod.PUBLIC,
                        this.exchangeProcessor.getResponseClass(),
                        this.serviceMethodGenerator.getMethodName());
        String methodRemark = this.serviceMethodGenerator.getCommonMethod().getRemark();
        this.jcontrollerMethod.javadoc().append(methodRemark);
        String methodPath = this.serviceMethodGenerator.getCommonMethod().getPath();
        if (methodPath != null && methodPath.equals("null")) {
            methodPath = "/" + this.serviceMethodGenerator.getCommonMethod().getName();
        }
        String methodVersion = this.serviceMethodGenerator.getCommonMethod().getVersion();
        if (methodVersion != null && !methodVersion.equals("")) {
            methodPath = methodPath + "/" + methodVersion;
        }

        JBlock controllerMethodBlk = this.jcontrollerMethod.body();

        JInvocation jInvocation =
                JExpr.invoke(
                        JExpr.refthis(commonController.getServiceName()),
                        this.serviceMethodGenerator.getMethodName());

        methodPath =
                methodPath
                        + this.exchangeProcessor.processHTTPRequest(
                        jcontrollerMethod, jInvocation, mockModel, this.cm);

        if (this.getMockModel() == MockModel.MockModel_Swagger) {
            this.jcontrollerMethod
                    .annotate(cm.ref("io.swagger.annotations.ApiOperation"))
                    .param("value", methodRemark)
                    .param("notes", methodRemark);
        }

        // headParams
        List<PacketField> headVariables =
                this.serviceMethodGenerator.getCommonMethod().getHeadVariables();
        if (headVariables != null && headVariables.size() > 0) {
            for (int index = 0; index < headVariables.size(); index++) {
                PacketField headVariable = headVariables.get(index);
                AbstractJType jType = cm.ref(headVariable.getDatatype());
                JVar headVariableJVar = this.jcontrollerMethod.param(jType, headVariable.getFieldName());
                headVariableJVar
                        .annotate(cm.ref("org.springframework.web.bind.annotation.RequestHeader"))
                        .param("name", headVariable.getFieldName())
                        .param("required", true);
                jInvocation.arg(headVariableJVar);
                if (this.getMockModel() == MockModel.MockModel_Swagger) {
                    headVariableJVar
                            .annotate(cm.ref("io.swagger.annotations.ApiParam"))
                            .param("name", headVariable.getFieldName())
                            .param("value", headVariable.getRemark())
                            .param("required", true);
                }
            }
        }
        List<PacketField> pathVariables =
                this.serviceMethodGenerator.getCommonMethod().getPathVariables();
        if (pathVariables != null && pathVariables.size() > 0) {
            for (int index = 0; index < pathVariables.size(); index++) {
                PacketField pathVariable = pathVariables.get(index);
                AbstractJType jType = cm.ref(pathVariable.getDatatype());
                JVar pathVariableJVar = this.jcontrollerMethod.param(jType, pathVariable.getFieldName());
                pathVariableJVar
                        .annotate(cm.ref("org.springframework.web.bind.annotation.PathVariable"))
                        .param(pathVariable.getFieldName());

                jInvocation.arg(pathVariableJVar);
                if (this.getMockModel() == MockModel.MockModel_Swagger) {
                    pathVariableJVar
                            .annotate(cm.ref("io.swagger.annotations.ApiParam"))
                            .param("name", pathVariable.getFieldName())
                            .param("value", pathVariable.getRemark())
                            .param("required", true);
                }
                methodPath = methodPath.replaceFirst("\\{\\}", "{" + pathVariable.getFieldName() + "}");
            }
        }
        List<PacketField> parameters = this.serviceMethodGenerator.getCommonMethod().getParameters();
        if (parameters != null && parameters.size() > 0) {
            for (int index = 0; index < parameters.size(); index++) {
                PacketField parameter = parameters.get(index);
                AbstractJClass jClass = cm.ref(parameter.getDatatype());
                JVar parameterJVar = this.jcontrollerMethod.param(jClass, parameter.getFieldName());

                parameterJVar
                        .annotate(cm.ref("org.springframework.web.bind.annotation.RequestParam"))
                        .param("name", parameter.getFieldName())
                        .param("required", true);

                jInvocation.arg(parameterJVar);
                if (this.getMockModel() == MockModel.MockModel_Swagger) {
                    parameterJVar
                            .annotate(cm.ref("io.swagger.annotations.ApiParam"))
                            .param("name", parameter.getFieldName())
                            .param("value", parameter.getRemark())
                            .param("required", true);
                }
            }
        }
        JAnnotationUse jAnnotationUse =
                this.jcontrollerMethod.annotate(
                        cm.ref("org.springframework.web.bind.annotation.RequestMapping"));
        jAnnotationUse.param("value", methodPath).param("method", requestMethod);

        String[] resTypes = this.serviceMethodGenerator.getCommonMethod().getResType().split(",");
        if (resTypes.length == 1) {
            if (!resTypes[0].equals("")) {
                jAnnotationUse.param("produces", resTypes[0]);
            }
        } else {
            JAnnotationArrayMember jAnnotationArrayMember = jAnnotationUse.paramArray("produces");
            for (int i = 0; i < resTypes.length; i++) {
                jAnnotationArrayMember.param(resTypes[i]);
            }
        }

        List<PacketField> cookieVariables =
                this.serviceMethodGenerator.getCommonMethod().getCookieVariables();
        if (cookieVariables != null && cookieVariables.size() > 0) {
            for (int index = 0; index < cookieVariables.size(); index++) {
                PacketField cookieVariable = cookieVariables.get(index);
                AbstractJType jType = cm.ref(cookieVariable.getDatatype());
                JVar cookieJVar = this.jcontrollerMethod.param(jType, cookieVariable.getFieldName());
                cookieJVar
                        .annotate(cm.ref("org.springframework.web.bind.annotation.CookieValue"))
                        .param("name", cookieVariable.getFieldName())
                        .param("required", true);
                jInvocation.arg(cookieJVar);
                if (this.getMockModel() == MockModel.MockModel_Swagger) {
                    cookieJVar
                            .annotate(cm.ref("io.swagger.annotations.ApiParam"))
                            .param("name", cookieVariable.getFieldName())
                            .param("value", "cookie:" + cookieVariable.getRemark())
                            .param("required", true);
                }
                controllerMethodBlk.add(
                        JExpr.ref("log")
                                .invoke("debug")
                                .arg(JExpr.lit(cookieJVar.name() + "{}"))
                                .arg(cookieJVar));
            }
        }

        List<PacketField> webParameters =
                this.serviceMethodGenerator.getCommonMethod().getWebParameters();
        if (webParameters != null && webParameters.size() > 0) {
            for (int index = 0; index < webParameters.size(); index++) {
                PacketField parameter = webParameters.get(index);
                String dataType = parameter.getDatatype();
                if (parameter.is_new()) {
                    dataType = this.packageNamePrefix + dataType;
                }

                AbstractJType jType;
                if (dataType.endsWith(":List")) {
                    String primitiveType = dataType.split(":")[0];
                    jType = cm.ref("java.util.List").narrow(cm.ref(primitiveType));
                } else if (dataType.endsWith(":Array")) {
                    String primitiveType = dataType.split(":")[0];
                    jType = cm.ref(primitiveType).array();
                } else {
                    jType = cm.ref(dataType);
                }
                JVar parameterJVar = this.jcontrollerMethod.param(jType, parameter.getName());

                jInvocation.arg(parameterJVar);
            }
        }

        if (this.serviceMethodGenerator.getRequestBodyClass() != null) {
            String varName=this.serviceMethodGenerator.getCommonMethod().getRequestName();
            if(StringUtils.isBlank(varName)){
                if(this.serviceMethodGenerator.getRequestBodyClass().isArray()){
                    varName = StringHelper.toLowerCaseFirstOne(this.serviceMethodGenerator.getRequestBodyClass().elementType().name())+"s";
                }else{
                    varName = StringHelper.toLowerCaseFirstOne(this.serviceMethodGenerator.getRequestBodyClass().name());
                }
            }
            JVar requestParam =
                    this.jcontrollerMethod.param(
                            this.serviceMethodGenerator.getRequestBodyClass(), varName);
            requestParam.annotate(cm.ref("org.springframework.web.bind.annotation.RequestBody"));
            requestParam.annotate(cm.ref("javax.validation.Valid"));
            jInvocation.arg(requestParam);
        }
        this.exchangeProcessor.processResponse(controllerMethodBlk, jInvocation, this.cm);
    }
}
