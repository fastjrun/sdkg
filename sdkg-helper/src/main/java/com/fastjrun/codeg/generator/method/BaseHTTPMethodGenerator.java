package com.fastjrun.codeg.generator.method;

import com.fastjrun.codeg.common.PacketField;
import com.fastjrun.codeg.common.PacketObject;
import com.sun.codemodel.*;

import java.util.List;

public class BaseHTTPMethodGenerator extends BaseControllerMethodGenerator {

    public void processClientMethod(String controllerPath, JDefinedClass clientClass) {
        this.jClientMethod = clientClass.method(JMod.PUBLIC, this.responseBodyClass, this.methodName);
        String methodRemark = this.commonMethod.getRemark();
        this.jClientMethod.javadoc().append(methodRemark);
        JBlock methodBlk = this.jClientMethod.body();
        String methodPath = this.commonMethod.getPath();
        if (methodPath == null || methodPath.equals("")) {
            methodPath = "/" + this.methodName;
        }
        String methodVersion = this.commonMethod.getVersion();
        if (methodVersion != null && !methodVersion.equals("")) {
            methodPath += "/" + methodVersion;
        }
        String invokeMethodName;
        if (this.commonMethod.isResponseIsArray()) {
            invokeMethodName = "processList";
        } else {
            invokeMethodName = "process";
        }
        JInvocation jInvocation = JExpr.invoke(JExpr.ref("baseClient"), invokeMethodName);

        // headParams
        List<PacketField> headVariables = this.commonMethod.getHeadVariables();

        JClass stringClass = cm.ref("String");

        JVar headParamsJvar = null;
        if (headVariables != null && headVariables.size() > 0) {
            headParamsJvar = methodBlk.decl(
                    cm.ref("java.util.Map").narrow(stringClass).narrow(stringClass), "headParams",
                    JExpr._new(cm.ref("java.util.HashMap").narrow(stringClass).narrow(stringClass)));
            for (int index = 0; index < headVariables.size(); index++) {
                PacketField headVariable = headVariables.get(index);
                JClass jClass = cm.ref(headVariable.getDatatype());
                this.jClientMethod.param(jClass, headVariable.getNameAlias());
                methodBlk.invoke(headParamsJvar, "put").arg(JExpr.lit(headVariable.getName()))
                        .arg(JExpr.ref(headVariable.getNameAlias()));

                methodBlk.invoke(JExpr.ref("log"), "debug").arg(JExpr.lit("header[{}] = {}"))
                        .arg(JExpr.lit(headVariable.getNameAlias()))
                        .arg(JExpr.ref(headVariable.getNameAlias()));

            }

        }
        // path
        JClass stringBuilderClass = cm.ref("java.lang.StringBuilder");
        JVar pathVar = methodBlk.decl(stringBuilderClass, "path",
                JExpr._new(stringBuilderClass).invoke("append")
                        .arg(JExpr.lit(controllerPath).plus(JExpr.lit(methodPath))));
        List<PacketField> pathVariables = this.commonMethod.getPathVariables();
        if (pathVariables != null && pathVariables.size() > 0) {
            for (int index = 0; index < pathVariables.size(); index++) {
                PacketField pathVariable = pathVariables.get(index);
                JClass jClass = cm.ref(pathVariable.getDatatype());
                this.jClientMethod.param(jClass, pathVariable.getName());

                methodBlk.invoke(pathVar, "append").arg(JExpr.lit("/"));
                methodBlk.invoke(pathVar, "append").arg(JExpr.ref(pathVariable.getName()));

                methodBlk.invoke(JExpr.ref("log"), "debug").arg(JExpr.lit("pathVariable[{}] = {}"))
                        .arg(JExpr.lit(pathVariable.getName())).arg(JExpr.ref(pathVariable.getName()));

            }
        }
        jInvocation.arg(pathVar.invoke("toString"));

        methodBlk.invoke(JExpr.ref("log"), "debug").arg(JExpr.lit("path = {}")).arg(pathVar.invoke("toString"));
        // method
        jInvocation.arg(JExpr.lit(this.commonMethod.getHttpMethod().toUpperCase()));

        methodBlk.invoke(JExpr.ref("log"), "debug")
                .arg(JExpr.lit("method = " + this.commonMethod.getHttpMethod().toUpperCase()));

        List<PacketField> parameters = this.commonMethod.getParameters();
        if (parameters != null && parameters.size() > 0) {
            // queryParams
            JVar queryParamsJvar = methodBlk.decl(
                    cm.ref("java.util.Map").narrow(stringClass).narrow(stringClass), "queryParams",
                    JExpr._new(cm.ref("java.util.HashMap").narrow(stringClass).narrow(stringClass)));
            for (int index = 0; index < parameters.size(); index++) {
                PacketField parameter = parameters.get(index);
                JClass jClass = cm.ref(parameter.getDatatype());
                this.jClientMethod.param(jClass, parameter.getName());

                JExpression jInvocationParameter = JExpr.ref(parameter.getName());

                if (jClass != stringClass) {
                    jInvocationParameter = stringClass.staticInvoke("valueOf").arg(jInvocationParameter);
                }

                methodBlk.invoke(queryParamsJvar, "put").arg(JExpr.lit(parameter.getName()))
                        .arg(jInvocationParameter);

                methodBlk.invoke(JExpr.ref("log"), "debug").arg(JExpr.lit("paramter[{}] = {}"))
                        .arg(JExpr.lit(parameter.getName())).arg(JExpr.ref(parameter.getName()));

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
        List<PacketField> cookies = this.commonMethod.getCookieVariables();
        if (cookies != null && cookies.size() > 0) {
            // cookies
            JVar cookieJvar = methodBlk.decl(
                    cm.ref("java.util.Map").narrow(stringClass).narrow(stringClass), "cookieParams",
                    JExpr._new(cm.ref("java.util.HashMap").narrow(stringClass).narrow(stringClass)));
            for (int index = 0; index < cookies.size(); index++) {
                PacketField cookie = cookies.get(index);
                JClass jClass = cm.ref(cookie.getDatatype());
                this.jClientMethod.param(jClass, cookie.getName());

                JExpression jInvocationCookie = JExpr.ref(cookie.getName());

                if (jClass != stringClass) {
                    jInvocationCookie = stringClass.staticInvoke("valueOf").arg(jInvocationCookie);
                }

                methodBlk.invoke(cookieJvar, "put").arg(JExpr.lit(cookie.getName()))
                        .arg(jInvocationCookie);

                methodBlk.invoke(JExpr.ref("log"), "debug").arg(JExpr.lit("paramter[{}] = {}"))
                        .arg(JExpr.lit(cookie.getName())).arg(JExpr.ref(cookie.getName()));

            }
            jInvocation.arg(cookieJvar);
        } else {
            jInvocation.arg(JExpr._null());
        }

        // requestBody
        PacketObject requestBody = this.commonMethod.getRequest();
        if (this.requestBodyClass != null) {
            JVar jRequestBodyVar = this.jClientMethod.param(this.requestBodyClass, "requestBody");
            jInvocation.arg(jRequestBodyVar);

        } else {
            jInvocation.arg(JExpr._null());

        }

        if (this.responseBodyClass != cm.VOID) {
            jInvocation.arg(JExpr.dotclass((JClass) this.responseBodyClass));
            methodBlk._return(jInvocation);
        } else {
            methodBlk.add(jInvocation);
        }

    }
}