/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.codeg.generator.method;

import com.fastjrun.codeg.common.CommonController;
import com.fastjrun.codeg.common.PacketField;
import com.fastjrun.codeg.generator.BaseRPCGenerator;
import com.fastjrun.codeg.helper.StringHelper;
import com.helger.jcodemodel.AbstractJClass;
import com.helger.jcodemodel.AbstractJType;
import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JInvocation;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.JMod;
import com.helger.jcodemodel.JVar;

import javax.lang.model.type.PrimitiveType;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseRPCMethodGenerator extends BaseControllerMethodGenerator {

    protected JMethod japiMethod;

    protected JMethod japiManagerMethod;

    public void processApiMethod(JDefinedClass apiClass) {
        this.japiMethod = apiClass.method(JMod.NONE, this.exchangeProcessor.getResponseClass(),
          this.serviceMethodGenerator.getMethodName());
        String methodRemark = this.serviceMethodGenerator.getCommonMethod().getRemark();
        this.japiMethod.javadoc().append(methodRemark);

        MethodGeneratorHelper.processServiceMethodVariables(this.japiMethod,
          this.serviceMethodGenerator.getCommonMethod().getHeadVariables(), this.cm);
        MethodGeneratorHelper.processServiceMethodVariables(this.japiMethod,
          this.serviceMethodGenerator.getCommonMethod().getPathVariables(), this.cm);
        MethodGeneratorHelper.processServiceMethodVariables(this.japiMethod,
          this.serviceMethodGenerator.getCommonMethod().getParameters(), this.cm);
        MethodGeneratorHelper.processServiceMethodVariables(this.japiMethod,
          this.serviceMethodGenerator.getCommonMethod().getCookieVariables(), this.cm);

        if (this.exchangeProcessor.getRequestClass() != null) {
            this.japiMethod.param(this.exchangeProcessor.getRequestClass(), "request");
        }
    }

    public void processApiManagerMethod(CommonController commonController,
      JDefinedClass apiManagerClass) {
        this.japiManagerMethod =
          apiManagerClass.method(JMod.PUBLIC, this.exchangeProcessor.getResponseClass(),
            this.serviceMethodGenerator.getMethodName());
        String methodRemark = this.serviceMethodGenerator.getCommonMethod().getRemark();
        this.japiManagerMethod.javadoc().append(methodRemark);
        String serviceName = commonController.getServiceName();
        JInvocation jInvocation =
          JExpr.invoke(JExpr.refthis(serviceName), this.serviceMethodGenerator.getMethodName());

        this.japiManagerMethod.annotate(cm.ref("java.lang.Override"));

        List<PacketField> headVariables =
          this.serviceMethodGenerator.getCommonMethod().getHeadVariables();
        if (headVariables != null && headVariables.size() > 0) {
            for (int index = 0; index < headVariables.size(); index++) {
                PacketField headVariable = headVariables.get(index);
                AbstractJType jType = cm.ref(headVariable.getDatatype());
                JVar headVariableJVar = this.japiManagerMethod.param(jType, headVariable.getName());
                jInvocation.arg(headVariableJVar);
            }
        }

        List<PacketField> pathVariables =
          this.serviceMethodGenerator.getCommonMethod().getPathVariables();
        if (pathVariables != null && pathVariables.size() > 0) {
            for (int index = 0; index < pathVariables.size(); index++) {
                PacketField pathVariable = pathVariables.get(index);
                AbstractJType jType = cm.ref(pathVariable.getDatatype());
                JVar pathVariableJVar = this.japiManagerMethod.param(jType, pathVariable.getName());
                jInvocation.arg(pathVariableJVar);
            }
        }

        List<PacketField> parameters =
          this.serviceMethodGenerator.getCommonMethod().getParameters();
        if (parameters != null && parameters.size() > 0) {
            for (int index = 0; index < parameters.size(); index++) {
                PacketField parameter = parameters.get(index);
                AbstractJClass jClass = cm.ref(parameter.getDatatype());
                JVar parameterJVar = this.japiManagerMethod.param(jClass, parameter.getName());
                jInvocation.arg(parameterJVar);
            }
        }

        List<PacketField> cookieVariables =
          this.serviceMethodGenerator.getCommonMethod().getCookieVariables();
        if (cookieVariables != null && cookieVariables.size() > 0) {
            for (int index = 0; index < cookieVariables.size(); index++) {
                PacketField cookieVariable = cookieVariables.get(index);
                AbstractJType jType = cm.ref(cookieVariable.getDatatype());
                JVar cookieJVar = this.japiManagerMethod.param(jType, cookieVariable.getName());
                jInvocation.arg(cookieJVar);
            }
        }

        this.exchangeProcessor.processRPCRequest(this.japiManagerMethod, jInvocation, this.cm);
        this.exchangeProcessor.processResponse(this.japiManagerMethod.body(), jInvocation, this.cm);
    }

    public void processClientMethod(AbstractJClass apiClass, JDefinedClass clientClass) {
        this.jClientMethod =
          clientClass.method(JMod.PUBLIC, this.serviceMethodGenerator.getResponseBodyClass(),
            this.serviceMethodGenerator.getMethodName());

        String methodRemark = this.serviceMethodGenerator.getCommonMethod().getRemark();
        this.jClientMethod.javadoc().append(methodRemark);

        this.jClientMethod.annotate(cm.ref("java.lang.Override"));

        JBlock methodBlk = this.jClientMethod.body();
        String invokeMethodName;
        if (this.serviceMethodGenerator.getCommonMethod().isResponseIsArray()) {
            invokeMethodName = "processList";
        } else {
            invokeMethodName = "process";
        }
        JInvocation jInvocation = JExpr.invoke(JExpr.ref("baseClient"), invokeMethodName);
        jInvocation.arg(JExpr.dotClass(apiClass));
        jInvocation.arg(JExpr.lit(this.serviceMethodGenerator.getMethodName()));
        List<AbstractJType> paramterTypes = new ArrayList<>();

        List<JVar> paramterJVars = new ArrayList<>();

        // headParams
        List<PacketField> headVariables =
          this.serviceMethodGenerator.getCommonMethod().getHeadVariables();
        if (headVariables != null && headVariables.size() > 0) {
            for (int index = 0; index < headVariables.size(); index++) {
                PacketField headVariable = headVariables.get(index);
                AbstractJClass jClass = cm.ref(headVariable.getDatatype());
                JVar headJVar = this.jClientMethod.param(jClass, headVariable.getNameAlias());
                paramterTypes.add(jClass);
                paramterJVars.add(headJVar);

                methodBlk.add(
                  JExpr.ref("log").invoke("debug").arg(JExpr.lit("header[{}] = {}")).arg(
                    JExpr.lit(headVariable.getNameAlias())).arg(
                    JExpr.ref(headVariable.getNameAlias())));

            }

        }

        List<PacketField> pathVariables =
          this.serviceMethodGenerator.getCommonMethod().getPathVariables();
        if (pathVariables != null && pathVariables.size() > 0) {
            for (int index = 0; index < pathVariables.size(); index++) {
                PacketField pathVariable = pathVariables.get(index);
                AbstractJClass jClass = cm.ref(pathVariable.getDatatype());
                JVar pathVariableVar = this.jClientMethod.param(jClass, pathVariable.getName());
                paramterTypes.add(jClass);
                paramterJVars.add(pathVariableVar);

                methodBlk.add(
                  JExpr.ref("log").invoke("debug").arg(JExpr.lit("pathVariable[{}] = {}")).arg(
                    JExpr.lit(pathVariable.getName())).arg(JExpr.ref(pathVariable.getName())));

            }
        }

        List<PacketField> parameters =
          this.serviceMethodGenerator.getCommonMethod().getParameters();
        if (parameters != null && parameters.size() > 0) {
            for (int index = 0; index < parameters.size(); index++) {
                PacketField parameter = parameters.get(index);
                AbstractJClass jClass = cm.ref(parameter.getDatatype());
                JVar parameterVar = this.jClientMethod.param(jClass, parameter.getName());
                paramterTypes.add(jClass);
                paramterJVars.add(parameterVar);

                methodBlk.add(
                  JExpr.ref("log").invoke("debug").arg(JExpr.lit("paramter[{}] = {}")).arg(
                    JExpr.lit(parameter.getName())).arg(JExpr.ref(parameter.getName())));

            }
        }

        List<PacketField> cookies =
          this.serviceMethodGenerator.getCommonMethod().getCookieVariables();
        if (cookies != null && cookies.size() > 0) {
            for (int index = 0; index < cookies.size(); index++) {
                PacketField cookie = cookies.get(index);
                AbstractJClass jClass = cm.ref(cookie.getDatatype());
                JVar cookieVar = this.jClientMethod.param(jClass, cookie.getName());
                paramterTypes.add(jClass);
                paramterJVars.add(cookieVar);
                methodBlk.add(
                  JExpr.ref("log").invoke("debug").arg(JExpr.lit("paramter[{}] = {}")).arg(
                    JExpr.lit(cookie.getName())).arg(JExpr.ref(cookie.getName())));

            }
        }

        if (this.serviceMethodGenerator.getRequestBodyClass() != null) {
            JVar jRequestBodyVar =
              this.jClientMethod.param((this.serviceMethodGenerator.getRequestBodyClass()),
                "requestBody");
            paramterTypes.add(this.exchangeProcessor.getRequestClass());
            paramterJVars.add(jRequestBodyVar);
        }

        if (paramterTypes.size() > 0) {
            JVar paramterTypesJVar = methodBlk.decl(cm.ref("Class").array(), "paramterTypes",
              JExpr.newArray(cm.ref("Class"), paramterTypes.size()));
            JVar paramterValuesJVar = methodBlk.decl(cm.ref("Object").array(), "paramterValues",
              JExpr.newArray(cm.ref("Object"), paramterJVars.size()));
            for (int i = 0; i < paramterJVars.size(); i++) {
                methodBlk.assign(paramterTypesJVar.component(JExpr.lit(i)),
                  paramterJVars.get(i).invoke("getClass"));
                methodBlk.assign(paramterValuesJVar.component(JExpr.lit(i)), paramterJVars.get(i));
            }
            jInvocation.arg(paramterTypesJVar);
            jInvocation.arg(paramterValuesJVar);
        }

        if (this.serviceMethodGenerator.getResponseBodyClass() != null && this
          .serviceMethodGenerator.getResponseBodyClass() != cm.VOID) {
            AbstractJType responseType = this.serviceMethodGenerator.getResponseBodyClass();
            AbstractJClass responseClass = null;
            if (responseType instanceof PrimitiveType) {
                responseClass = responseType.boxify();
            } else {
                responseClass = (AbstractJClass) responseType;
            }
            jInvocation.arg(responseClass.dotclass());
            methodBlk._return(jInvocation);
        } else {
            jInvocation.arg(JExpr._null());
            methodBlk.add(jInvocation);
        }
    }

    @Override
    public void generate() {
        if (this.baseControllerGenerator.getCommonController().is_new()) {
            this.processApiMethod(
              (JDefinedClass) ((BaseRPCGenerator) this.baseControllerGenerator).getApiClass());
        }
        if (!this.isApi()) {
            if (this.isClient()) {
                this.processClientMethod(
                  ((BaseRPCGenerator) this.baseControllerGenerator).getApiClass(),
                  this.baseControllerGenerator.getClientClass());
                this.processClientTestMethod(this.baseControllerGenerator.getClientTestClass());
                StringBuilder sb =
                  new StringBuilder(this.baseControllerGenerator.getClientName()).append(
                    "Test.test");
                sb.append(StringHelper.toUpperCaseFirstOne(
                  this.serviceMethodGenerator.getMethodName())).append(".n");
                this.processClientTestPraram();
                this.baseControllerGenerator.getClientTestParam().put(sb.toString(),
                  this.methodParamInJsonObject.toString().replaceAll("\n", "").replaceAll("\r",
                    "").trim());
            } else {
                if (this.getMockModel() != MockModel.MockModel_Common) {
                    this.processControllerMethod(this.baseControllerGenerator.getCommonController(),
                      this.baseControllerGenerator.getControlllerClass());
                }

                this.processApiManagerMethod(this.baseControllerGenerator.getCommonController(),
                  ((BaseRPCGenerator) this.baseControllerGenerator).getApiManagerClass());
            }
        }

    }
}