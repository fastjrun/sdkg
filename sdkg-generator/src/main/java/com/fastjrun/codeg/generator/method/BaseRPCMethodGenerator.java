/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.codeg.generator.method;

import com.fastjrun.codeg.common.CommonController;
import com.fastjrun.codeg.generator.BaseRPCGenerator;
import com.fastjrun.codeg.helper.StringHelper;
import com.helger.jcodemodel.*;

import javax.lang.model.type.PrimitiveType;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseRPCMethodGenerator extends BaseControllerMethodGenerator {

  protected JMethod japiMethod;

  protected JMethod japiManagerMethod;

  public void processApiMethod(JDefinedClass apiClass) {
    this.japiMethod =
        apiClass.method(
            JMod.NONE,
            this.exchangeProcessor.getResponseClass(),
            this.serviceMethodGenerator.getMethodName());
    String methodRemark = this.serviceMethodGenerator.getCommonMethod().getRemark();
    this.japiMethod.javadoc().append(methodRemark);

    if (this.exchangeProcessor.getRequestClass() != null) {
      this.japiMethod.param(this.exchangeProcessor.getRequestClass(), "request");
    }
  }

  public void processApiManagerMethod(
      CommonController commonController, JDefinedClass apiManagerClass) {
    this.japiManagerMethod =
        apiManagerClass.method(
            JMod.PUBLIC,
            this.exchangeProcessor.getResponseClass(),
            this.serviceMethodGenerator.getMethodName());
    String methodRemark = this.serviceMethodGenerator.getCommonMethod().getRemark();
    this.japiManagerMethod.javadoc().append(methodRemark);
    String serviceName = commonController.getServiceName();
    JInvocation jInvocation =
        JExpr.invoke(JExpr.refthis(serviceName), this.serviceMethodGenerator.getMethodName());

    this.japiManagerMethod.annotate(cm.ref("java.lang.Override"));

    this.exchangeProcessor.processRPCRequest(this.japiManagerMethod, jInvocation, this.cm);
    this.exchangeProcessor.processResponse(this.japiManagerMethod.body(), jInvocation, this.cm);
  }

  public void processClientMethod(AbstractJClass apiClass, JDefinedClass clientClass) {
    this.jClientMethod =
        clientClass.method(
            JMod.PUBLIC,
            this.serviceMethodGenerator.getResponseBodyClass(),
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



    if (this.serviceMethodGenerator.getRequestBodyClass() != null) {
      JVar jRequestBodyVar =
          this.jClientMethod.param(
              (this.serviceMethodGenerator.getRequestBodyClass()), "requestBody");
      paramterTypes.add(this.exchangeProcessor.getRequestClass());
      paramterJVars.add(jRequestBodyVar);
    }

    if (paramterTypes.size() > 0) {
      JVar paramterTypesJVar =
          methodBlk.decl(
              cm.ref("Class").array(),
              "paramterTypes",
              JExpr.newArray(cm.ref("Class"), paramterTypes.size()));
      JVar paramterValuesJVar =
          methodBlk.decl(
              cm.ref("Object").array(),
              "paramterValues",
              JExpr.newArray(cm.ref("Object"), paramterJVars.size()));
      for (int i = 0; i < paramterJVars.size(); i++) {
        methodBlk.assign(
            paramterTypesJVar.component(JExpr.lit(i)), paramterJVars.get(i).invoke("getClass"));
        methodBlk.assign(paramterValuesJVar.component(JExpr.lit(i)), paramterJVars.get(i));
      }
      jInvocation.arg(paramterTypesJVar);
      jInvocation.arg(paramterValuesJVar);
    }

    if (this.serviceMethodGenerator.getResponseBodyClass() != null
        && this.serviceMethodGenerator.getResponseBodyClass() != cm.VOID) {
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
            new StringBuilder(this.baseControllerGenerator.getClientName()).append("Test.test");
        sb.append(StringHelper.toUpperCaseFirstOne(this.serviceMethodGenerator.getMethodName()))
            .append(".n");
        this.processClientTestPraram();
        this.baseControllerGenerator
            .getClientTestParam()
            .put(
                sb.toString(),
                this.methodParamInJsonObject
                    .toString()
                    .replaceAll("\n", "")
                    .replaceAll("\r", "")
                    .trim());
      } else {
        if (this.getMockModel() != MockModel.MockModel_Common) {
          this.processControllerMethod(
              this.baseControllerGenerator.getCommonController(),
              this.baseControllerGenerator.getControlllerClass());
        }

        this.processApiManagerMethod(
            this.baseControllerGenerator.getCommonController(),
            ((BaseRPCGenerator) this.baseControllerGenerator).getApiManagerClass());
      }
    }
  }
}
