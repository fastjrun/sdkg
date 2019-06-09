/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.codeg.generator.method;

import com.fastjrun.codeg.common.PacketField;
import com.fastjrun.codeg.helper.StringHelper;
import com.helger.jcodemodel.AbstractJClass;
import com.helger.jcodemodel.IJExpression;
import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JInvocation;
import com.helger.jcodemodel.JMod;
import com.helger.jcodemodel.JVar;

import java.util.List;

public abstract class BaseHTTPMethodGenerator extends BaseControllerMethodGenerator {

    public void processClientMethod(String controllerPath, JDefinedClass clientClass) {
        this.jClientMethod =
          clientClass.method(JMod.PUBLIC, this.serviceMethodGenerator.getResponseBodyClass(),
            this.serviceMethodGenerator.getMethodName());
        String methodRemark = this.serviceMethodGenerator.getCommonMethod().getRemark();
        this.jClientMethod.javadoc().append(methodRemark);
        JBlock methodBlk = this.jClientMethod.body();
        this.jClientMethod.annotate(cm.ref("java.lang.Override"));
        String methodPath = this.serviceMethodGenerator.getCommonMethod().getPath();
        if (methodPath == null || methodPath.equals("")) {
            methodPath = "/" + this.serviceMethodGenerator.getMethodName();
        }
        String methodVersion = this.serviceMethodGenerator.getCommonMethod().getVersion();
        if (methodVersion != null && !methodVersion.equals("")) {
            methodPath += "/" + methodVersion;
        }
        String invokeMethodName;
        if (this.serviceMethodGenerator.getCommonMethod().isResponseIsArray()) {
            invokeMethodName = "processList";
        } else {
            invokeMethodName = "process";
        }
        JInvocation jInvocation = JExpr.invoke(JExpr.ref("baseClient"), invokeMethodName);

        // headParams
        List<PacketField> headVariables =
          this.serviceMethodGenerator.getCommonMethod().getHeadVariables();

        AbstractJClass stringClass = cm.ref("String");

        JVar headParamsJvar = null;
        if (headVariables != null && headVariables.size() > 0) {
            headParamsJvar =
              methodBlk.decl(cm.ref("java.util.Map").narrow(stringClass).narrow(stringClass),
                "headParams",
                JExpr._new(cm.ref("java.util.HashMap").narrow(stringClass).narrow(stringClass)));
            for (int index = 0; index < headVariables.size(); index++) {
                PacketField headVariable = headVariables.get(index);
                AbstractJClass jClass = cm.ref(headVariable.getDatatype());
                this.jClientMethod.param(jClass, headVariable.getNameAlias());
                methodBlk.add(
                  headParamsJvar.invoke("put").arg(JExpr.lit(headVariable.getName())).arg(
                    JExpr.ref(headVariable.getNameAlias())));

                methodBlk.add(
                  JExpr.ref("log").invoke("debug").arg(JExpr.lit("header[{}] = {}")).arg(
                    JExpr.lit(headVariable.getNameAlias())).arg(
                    JExpr.ref(headVariable.getNameAlias())));

            }

        }
        // path
        AbstractJClass stringBuilderClass = cm.ref("java.lang.StringBuilder");
        JVar pathVar = methodBlk.decl(stringBuilderClass, "path",
          JExpr._new(stringBuilderClass).invoke("append").arg(
            JExpr.lit(controllerPath).plus(JExpr.lit(methodPath))));
        List<PacketField> pathVariables =
          this.serviceMethodGenerator.getCommonMethod().getPathVariables();
        if (pathVariables != null && pathVariables.size() > 0) {
            for (int index = 0; index < pathVariables.size(); index++) {
                PacketField pathVariable = pathVariables.get(index);
                AbstractJClass jClass = cm.ref(pathVariable.getDatatype());
                this.jClientMethod.param(jClass, pathVariable.getName());

                methodBlk.add(pathVar.invoke("append").arg(JExpr.lit("/")));
                methodBlk.add(pathVar.invoke("append").arg(JExpr.ref(pathVariable.getName())));

                methodBlk.add(
                  JExpr.ref("log").invoke("debug").arg(JExpr.lit("pathVariable[{}] = {}")).arg(
                    JExpr.lit(pathVariable.getName())).arg(JExpr.ref(pathVariable.getName())));

            }
        }
        jInvocation.arg(pathVar.invoke("toString"));

        methodBlk.add(JExpr.ref("log").invoke("debug").arg(JExpr.lit("path = {}")).arg(
          pathVar.invoke("toString")));
        // method
        jInvocation.arg(
          JExpr.lit(this.serviceMethodGenerator.getCommonMethod().getHttpMethod().toUpperCase()));

        methodBlk.add(JExpr.ref("log").invoke("debug").arg(JExpr.lit("method = {}")).arg(
          this.serviceMethodGenerator.getCommonMethod().getHttpMethod().toUpperCase()));

        List<PacketField> parameters =
          this.serviceMethodGenerator.getCommonMethod().getParameters();
        if (parameters != null && parameters.size() > 0) {
            // queryParams
            JVar queryParamsJvar =
              methodBlk.decl(cm.ref("java.util.Map").narrow(stringClass).narrow(stringClass),
                "queryParams",
                JExpr._new(cm.ref("java.util.HashMap").narrow(stringClass).narrow(stringClass)));
            for (int index = 0; index < parameters.size(); index++) {
                PacketField parameter = parameters.get(index);
                AbstractJClass jClass = cm.ref(parameter.getDatatype());
                this.jClientMethod.param(jClass, parameter.getName());

                IJExpression jInvocationParameter = JExpr.direct(parameter.getName());

                if (jClass != stringClass) {
                    jInvocationParameter =
                      stringClass.staticInvoke("valueOf").arg(jInvocationParameter);
                }

                methodBlk.add(queryParamsJvar.invoke("put").arg(JExpr.lit(parameter.getName())).arg(
                  jInvocationParameter));

                methodBlk.add(
                  JExpr.ref("log").invoke("debug").arg(JExpr.lit("paramter[{}] = {}")).arg(
                    JExpr.lit(parameter.getName())).arg(JExpr.ref(parameter.getName())));

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
        List<PacketField> cookies =
          this.serviceMethodGenerator.getCommonMethod().getCookieVariables();
        if (cookies != null && cookies.size() > 0) {
            // cookies
            JVar cookieJvar =
              methodBlk.decl(cm.ref("java.util.Map").narrow(stringClass).narrow(stringClass),
                "cookieParams",
                JExpr._new(cm.ref("java.util.HashMap").narrow(stringClass).narrow(stringClass)));
            for (int index = 0; index < cookies.size(); index++) {
                PacketField cookie = cookies.get(index);
                AbstractJClass jClass = cm.ref(cookie.getDatatype());
                this.jClientMethod.param(jClass, cookie.getName());

                IJExpression jInvocationCookie = JExpr.ref(cookie.getName());

                if (jClass != stringClass) {
                    jInvocationCookie = stringClass.staticInvoke("valueOf").arg(jInvocationCookie);
                }

                methodBlk.add(
                  cookieJvar.invoke("put").arg(JExpr.lit(cookie.getName())).arg(jInvocationCookie));

                methodBlk.add(
                  JExpr.ref("log").invoke("debug").arg(JExpr.lit("paramter[{}] = {}")).arg(
                    JExpr.lit(cookie.getName())).arg(JExpr.ref(cookie.getName())));

            }
            jInvocation.arg(cookieJvar);
        } else {
            jInvocation.arg(JExpr._null());
        }

        // requestBody
        if (this.serviceMethodGenerator.getRequestBodyClass() != null) {
            JVar jRequestBodyVar =
              this.jClientMethod.param(this.serviceMethodGenerator.getRequestBodyClass(),
                "requestBody");
            jInvocation.arg(jRequestBodyVar);

        } else {
            jInvocation.arg(JExpr._null());
        }

        if (this.serviceMethodGenerator.getResponseBodyClass() != null && this
          .serviceMethodGenerator.getResponseBodyClass() != cm.VOID) {
            if (this.serviceMethodGenerator.getElementClass() != null) {
                jInvocation.arg(JExpr.dotClass(this.serviceMethodGenerator.getElementClass()));
            } else {
                jInvocation.arg(JExpr.dotClass(this.serviceMethodGenerator.getResponseBodyClass()));
            }
            methodBlk._return(jInvocation);
        } else {
            methodBlk.add(jInvocation);
        }
    }

    @Override
    public void generate() {
        if (!this.isApi()) {
            if (this.isClient()) {
                this.processClientMethod(this.baseControllerGenerator.getControllerPath(),
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
                this.processControllerMethod(this.baseControllerGenerator.getCommonController(),
                  this.baseControllerGenerator.getControlllerClass());
            }
        }
    }
}