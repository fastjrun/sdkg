package com.fastjrun.codeg.generator;

import java.util.List;

import org.springframework.web.bind.annotation.RequestMethod;

import com.fastjrun.codeg.common.CommonMethod;
import com.fastjrun.codeg.common.PacketField;
import com.fastjrun.codeg.common.PacketObject;
import com.sun.codemodel.JAnnotationArrayMember;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;

public abstract class DefaultRPCGenerator extends BaseRPCGenerator {

    @Override
    protected void processApiMethod(CommonMethod method) {
        JDefinedClass apiClassTemp = (JDefinedClass) this.apiClass;
        String methodName = method.getName();
        String methodRemark = method.getRemark();
        String methodVersion = method.getVersion();
        String methodNameAndVersion = methodName;
        if (methodVersion != null && !methodVersion.equals("")) {
            methodNameAndVersion = methodName + methodVersion;
        }
        JType responseBodyClass = this.parseResponseBodyClassFromCommonMethod(method);
        JClass responseClass;
        if (method.isResponseIsArray()) {
            responseClass = cm.ref("com.fastjrun.dto.DefaultListResponse").narrow(responseBodyClass);
        } else {
            responseClass = cm.ref("com.fastjrun.dto.DefaultResponse").narrow(responseBodyClass);
        }
        JMethod apiMethod = apiClassTemp.method(JMod.NONE, responseClass, methodNameAndVersion);
        apiMethod.javadoc().append(methodRemark);
        List<PacketField> parameters = method.getParameters();
        if (parameters != null && parameters.size() > 0) {
            for (int index = 0; index < parameters.size(); index++) {
                PacketField parameter = parameters.get(index);
                JClass jClass = cm.ref(parameter.getDatatype());
                apiMethod.param(jClass, parameter.getName());
            }
        }
        PacketObject request = method.getRequest();
        if (request != null) {
            JClass requestClass;
            if (request.is_new()) {
                requestClass = cm.ref(this.packageNamePrefix + request.get_class());
            } else {
                requestClass = cm.ref(request.get_class());
            }
            apiMethod.param(requestClass, "request");
        }
    }

    @Override
    protected void processAPIManagerMethod(CommonMethod method) {
        String methodName = method.getName();
        String methodRemark = method.getRemark();
        String methodVersion = method.getVersion();
        String methodNameAndVersion = methodName;
        if (methodVersion != null && !methodVersion.equals("")) {
            methodNameAndVersion = methodName + methodVersion;
        }
        JType responseBodyClass = this.parseResponseBodyClassFromCommonMethod(method);
        JClass responseClass;
        if (method.isResponseIsArray()) {
            responseClass = cm.ref("com.fastjrun.dto.DefaultListResponse").narrow(responseBodyClass);
        } else {
            responseClass = cm.ref("com.fastjrun.dto.DefaultResponse").narrow(responseBodyClass);
        }
        JMethod apiManagerMethod = apiManagerClass.method(JMod.PUBLIC, responseClass, methodNameAndVersion);
        apiManagerMethod.javadoc().append(methodRemark);
        String serviceName = commonController.getServiceName();
        JInvocation jInvocation = JExpr.invoke(JExpr.refthis(serviceName), methodNameAndVersion);
        List<PacketField> parameters = method.getParameters();
        if (parameters != null && parameters.size() > 0) {
            for (int index = 0; index < parameters.size(); index++) {
                PacketField parameter = parameters.get(index);
                JClass jClass = cm.ref(parameter.getDatatype());
                JVar parameterJVar = apiManagerMethod.param(jClass, parameter.getName());
                jInvocation.arg(parameterJVar);
            }
        }
        PacketObject request = method.getRequest();
        if (request != null) {
            JClass requestClass;
            if (request.is_new()) {
                requestClass = cm.ref(this.packageNamePrefix + request.get_class());
            } else {
                requestClass = cm.ref(request.get_class());
            }
            apiManagerMethod.param(requestClass, "request");
            jInvocation.arg(JExpr.ref("request"));
            apiManagerMethod.body().invoke(JExpr.ref("log"), "debug").arg(cm.ref("com.fastjrun.utils.JacksonUtils")
                    .staticInvoke("toJSon").arg(JExpr.ref("request")));
        }
        PacketObject response = method.getResponse();
        this.composeProviderMethodBody(response, responseClass, responseBodyClass, method, jInvocation,
                apiManagerMethod);
    }

    @Override
    protected void processControllerMockMethod(CommonMethod method) {
        PacketObject response = method.getResponse();
        JType responseBodyClass = this.parseResponseBodyClassFromCommonMethod(method);
        JClass responseClass;
        if (method.isResponseIsArray()) {
            responseClass = cm.ref("com.fastjrun.dto.DefaultListResponse").narrow(responseBodyClass);
        } else {
            responseClass = cm.ref("com.fastjrun.dto.DefaultResponse").narrow(responseBodyClass);
        }

        String methodName = method.getName();
        String methodPath = method.getPath();
        if (methodPath == null || methodPath.equals("")) {
            methodPath = "/" + methodName;
        }
        String methodRemark = method.getRemark();
        String methodVersion = method.getVersion();

        String methodNameAndVersion = methodName;
        if (methodVersion != null && !methodVersion.equals("")) {
            methodNameAndVersion = methodName + methodVersion;
        }
        JMethod controllerMethod = this.apiControllerClass.method(JMod.PUBLIC, responseClass, methodNameAndVersion);
        controllerMethod.javadoc().append(methodRemark);
        if (methodVersion != null && !methodVersion.equals("")) {
            methodPath = methodPath + "/" + methodVersion;
        }

        JBlock controllerMethodBlk = controllerMethod.body();
        String serviceName = commonController.getServiceName();
        JInvocation jInvocation = JExpr.invoke(JExpr.refthis(serviceName), methodNameAndVersion);

        if (this.getMockModel() == MockModel.MockModel_Swagger) {
            controllerMethod.annotate(cm.ref("io.swagger.annotations.ApiOperation"))
                    .param("value", methodRemark).param("notes", methodRemark);
        }
        List<PacketField> parameters = method.getParameters();
        if (parameters != null && parameters.size() > 0) {
            for (int index = 0; index < parameters.size(); index++) {
                PacketField parameter = parameters.get(index);
                JClass jClass = cm.ref(parameter.getDatatype());
                JVar parameterJVar = controllerMethod.param(jClass, parameter.getName());

                parameterJVar.annotate(cm.ref("org.springframework.web.bind.annotation.RequestParam"))
                        .param("name", parameter.getName()).param("required", true);

                jInvocation.arg(parameterJVar);
                if (this.getMockModel() == MockModel.MockModel_Swagger) {
                    parameterJVar.annotate(cm.ref("io.swagger.annotations.ApiParam"))
                            .param("name", parameter.getName()).param("value", parameter.getRemark())
                            .param("required", true);
                }
            }
        }
        RequestMethod requestMethod = RequestMethod.POST;

        JAnnotationUse jAnnotationUse = controllerMethod
                .annotate(cm.ref("org.springframework.web.bind.annotation.RequestMapping"));
        jAnnotationUse.param("value", methodPath).param("method", requestMethod);

        String[] resTypes = method.getResType().split(",");
        if (resTypes.length == 1) {
            jAnnotationUse.param("produces", resTypes[0]);
        } else {
            JAnnotationArrayMember jAnnotationArrayMember = jAnnotationUse.paramArray("produces");
            for (int i = 0; i < resTypes.length; i++) {
                jAnnotationArrayMember.param(resTypes[i]);
            }
        }

        PacketObject request = method.getRequest();

        if (request != null) {
            JClass requestClass;
            if (request.is_new()) {
                requestClass = cm.ref(this.packageNamePrefix + request.get_class());
            } else {
                requestClass = cm.ref(request.get_class());
            }
            JVar requestParam = controllerMethod.param(requestClass, "request");
            requestParam.annotate(cm.ref("org.springframework.web.bind.annotation.RequestBody"));
            requestParam.annotate(cm.ref("javax.validation.Valid"));
            jInvocation.arg(JExpr.ref("request"));

        }
        this.composeProviderMethodBody(response, responseClass, responseBodyClass, method, jInvocation,
                controllerMethod);
    }

    private void composeProviderMethodBody(PacketObject response, JClass responseClass, JType responseBodyClass,
                                           CommonMethod method, JInvocation jInvocation,
                                           JMethod
                                                   controllerMethod) {
        if (response == null) {
            controllerMethod.body().decl(responseClass, "response",
                    cm.ref("com.fastjrun.helper.BaseResponseHelper")
                            .staticInvoke("getSuccessResult"));
        } else {
            String responseHelperMethodName = "getResult";
            if (method.isResponseIsArray()) {
                responseHelperMethodName = "getResultList";
            }
            controllerMethod.body().decl(responseClass, "response",
                    cm.ref("com.fastjrun.helper.BaseResponseHelper")
                            .staticInvoke(responseHelperMethodName));
            controllerMethod.body().decl(responseBodyClass, "responseBody", jInvocation);
            controllerMethod.body().invoke(JExpr.ref("response"), "setBody").arg(JExpr.ref("responseBody"));
        }
        controllerMethod.body().invoke(JExpr.ref("log"), "debug").arg(JExpr.ref("response"));
        controllerMethod.body()._return(JExpr.ref("response"));
    }

}