package com.fastjrun.codeg.generator.method;

import com.fastjrun.codeg.common.CommonController;
import com.fastjrun.codeg.common.PacketField;
import com.sun.codemodel.*;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseRPCMethodGenerator extends BaseControllerMethodGenerator {

    protected JMethod japiMethod;

    protected JMethod japiManagerMethod;

    public void processApiMethod(JDefinedClass apiClass) {
        this.japiMethod = apiClass.method(JMod.NONE, this.responseClass, this.methodName);
        String methodRemark = commonMethod.getRemark();
        this.japiMethod.javadoc().append(methodRemark);

        this.processServiceMethodVariables(this.japiMethod, this.commonMethod.getHeadVariables());
        this.processServiceMethodVariables(this.japiMethod, this.commonMethod.getPathVariables());
        this.processServiceMethodVariables(this.japiMethod, this.commonMethod.getParameters());
        this.processServiceMethodVariables(this.japiMethod, this.commonMethod.getCookieVariables());

        if (this.requestClass != null) {
            this.japiMethod.param(this.requestClass, "request");
        }
    }

    public void processApiManagerMethod(CommonController commonController, JDefinedClass apiManagerClass) {
        this.japiManagerMethod = apiManagerClass.method(JMod.PUBLIC, this.responseClass, this.methodName);
        String methodRemark = commonMethod.getRemark();
        this.japiManagerMethod.javadoc().append(methodRemark);
        String serviceName = commonController.getServiceName();
        JInvocation jInvocation = JExpr.invoke(JExpr.refthis(serviceName), this.methodName);

        List<PacketField> headVariables = this.commonMethod.getHeadVariables();
        if (headVariables != null && headVariables.size() > 0) {
            for (int index = 0; index < headVariables.size(); index++) {
                PacketField headVariable = headVariables.get(index);
                JType jType = cm.ref(headVariable.getDatatype());
                JVar headVariableJVar = this.japiManagerMethod.param(jType, headVariable.getName());
                jInvocation.arg(headVariableJVar);
            }
        }

        List<PacketField> pathVariables = this.commonMethod.getPathVariables();
        if (pathVariables != null && pathVariables.size() > 0) {
            for (int index = 0; index < pathVariables.size(); index++) {
                PacketField pathVariable = pathVariables.get(index);
                JType jType = cm.ref(pathVariable.getDatatype());
                JVar pathVariableJVar = this.japiManagerMethod.param(jType, pathVariable.getName());
                jInvocation.arg(pathVariableJVar);
            }
        }

        List<PacketField> parameters = this.commonMethod.getParameters();
        if (parameters != null && parameters.size() > 0) {
            for (int index = 0; index < parameters.size(); index++) {
                PacketField parameter = parameters.get(index);
                JClass jClass = cm.ref(parameter.getDatatype());
                JVar parameterJVar = this.japiManagerMethod.param(jClass, parameter.getName());
                jInvocation.arg(parameterJVar);
            }
        }

        List<PacketField> cookieVariables = this.commonMethod.getCookieVariables();
        if (cookieVariables != null && cookieVariables.size() > 0) {
            for (int index = 0; index < cookieVariables.size(); index++) {
                PacketField cookieVariable = cookieVariables.get(index);
                JType jType = cm.ref(cookieVariable.getDatatype());
                JVar cookieJVar = this.japiManagerMethod.param(jType, cookieVariable.getName());
                jInvocation.arg(cookieJVar);
            }
        }

        if (this.requestClass != null) {
            this.japiManagerMethod.param(this.requestClass, "request");
            jInvocation.arg(JExpr.ref("request"));
            this.japiManagerMethod.body().invoke(JExpr.ref("log"), "debug")
                    .arg(cm.ref("com.fastjrun.utils.JacksonUtils")
                            .staticInvoke("toJSon").arg(JExpr.ref("request")));
        }
        this.processResponse(this.japiManagerMethod.body(), jInvocation);
    }

    public void processClientMethod(JClass apiClass, JDefinedClass clientClass) {
        this.jClientMethod = clientClass.method(JMod.PUBLIC, this.responseBodyClass, this.methodName);
        String methodRemark = commonMethod.getRemark();
        this.jClientMethod.javadoc().append(methodRemark);
        JBlock methodBlk = this.jClientMethod.body();
        String invokeMethodName;
        if (this.commonMethod.isResponseIsArray()) {
            invokeMethodName = "processList";
        } else {
            invokeMethodName = "process";
        }
        JInvocation jInvocation = JExpr.invoke(JExpr.ref("baseClient"), invokeMethodName);
        jInvocation.arg(JExpr.dotclass(apiClass));
        jInvocation.arg(JExpr.lit(this.methodName));
        List<JType> paramterTypes = new ArrayList<>();

        List<JVar> paramterJVars = new ArrayList<>();

        // headParams
        List<PacketField> headVariables = this.commonMethod.getHeadVariables();
        if (headVariables != null && headVariables.size() > 0) {
            for (int index = 0; index < headVariables.size(); index++) {
                PacketField headVariable = headVariables.get(index);
                JClass jClass = cm.ref(headVariable.getDatatype());
                JVar headJVar = this.jClientMethod.param(jClass, headVariable.getNameAlias());
                paramterTypes.add(jClass);
                paramterJVars.add(headJVar);

                methodBlk.invoke(JExpr.ref("log"), "debug").arg(JExpr.lit("header[{}] = {}"))
                        .arg(JExpr.lit(headVariable.getNameAlias()))
                        .arg(JExpr.ref(headVariable.getNameAlias()));

            }

        }

        List<PacketField> pathVariables = this.commonMethod.getPathVariables();
        if (pathVariables != null && pathVariables.size() > 0) {
            for (int index = 0; index < pathVariables.size(); index++) {
                PacketField pathVariable = pathVariables.get(index);
                JClass jClass = cm.ref(pathVariable.getDatatype());
                JVar pathVariableVar = this.jClientMethod.param(jClass, pathVariable.getName());
                paramterTypes.add(jClass);
                paramterJVars.add(pathVariableVar);

                methodBlk.invoke(JExpr.ref("log"), "debug").arg(JExpr.lit("pathVariable[{}] = {}"))
                        .arg(JExpr.lit(pathVariable.getName())).arg(JExpr.ref(pathVariable.getName()));

            }
        }

        List<PacketField> parameters = this.commonMethod.getParameters();
        if (parameters != null && parameters.size() > 0) {
            for (int index = 0; index < parameters.size(); index++) {
                PacketField parameter = parameters.get(index);
                JClass jClass = cm.ref(parameter.getDatatype());
                JVar parameterVar = this.jClientMethod.param(jClass, parameter.getName());
                paramterTypes.add(jClass);
                paramterJVars.add(parameterVar);

                methodBlk.invoke(JExpr.ref("log"), "debug").arg(JExpr.lit("paramter[{}] = {}"))
                        .arg(JExpr.lit(parameter.getName())).arg(JExpr.ref(parameter.getName()));

            }
        }

        List<PacketField> cookies = this.commonMethod.getCookieVariables();
        if (cookies != null && cookies.size() > 0) {
            for (int index = 0; index < cookies.size(); index++) {
                PacketField cookie = cookies.get(index);
                JClass jClass = cm.ref(cookie.getDatatype());
                JVar cookieVar = this.jClientMethod.param(jClass, cookie.getName());
                paramterTypes.add(jClass);
                paramterJVars.add(cookieVar);

                JExpression jInvocationCookie = JExpr.ref(cookie.getName());
                methodBlk.invoke(JExpr.ref("log"), "debug").arg(JExpr.lit("paramter[{}] = {}"))
                        .arg(JExpr.lit(cookie.getName())).arg(JExpr.ref(cookie.getName()));

            }
        }

        if (this.requestBodyClass != null) {
            JVar jRequestBodyVar = this.jClientMethod.param(this.requestBodyClass, "requestBody");
            paramterTypes.add(this.requestClass);
            paramterJVars.add(jRequestBodyVar);
        }

        if (paramterTypes.size() > 0) {
            JVar paramterTypesJVar = methodBlk.decl(cm.ref("Class").array(), "paramterTypes", JExpr.newArray(cm.ref
                    ("Class"), paramterTypes.size()));
            JVar paramterValuesJVar = methodBlk.decl(cm.ref("Object").array(), "paramterValues", JExpr.newArray(cm
                    .ref
                            ("Object"), paramterJVars.size()));
            for (int i = 0; i < paramterJVars.size(); i++) {
                methodBlk.assign(paramterTypesJVar.component(JExpr.lit(i)), paramterJVars.get(i).invoke
                        ("getClass"));
                methodBlk.assign(paramterValuesJVar.component(JExpr.lit(i)), paramterJVars.get(i));
            }
            jInvocation.arg(paramterTypesJVar);
            jInvocation.arg(paramterValuesJVar);
        }

        if (this.responseBodyClass != cm.VOID) {
            methodBlk._return(jInvocation);
        } else {
            methodBlk.add(jInvocation);
        }
    }
}