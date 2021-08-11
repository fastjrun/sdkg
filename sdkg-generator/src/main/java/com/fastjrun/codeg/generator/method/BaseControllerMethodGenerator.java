/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.codeg.generator.method;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fastjrun.codeg.common.CommonController;
import com.fastjrun.codeg.common.PacketField;
import com.fastjrun.codeg.generator.common.BaseControllerGenerator;
import com.fastjrun.codeg.processer.ExchangeProcessor;
import com.fastjrun.helper.StringHelper;
import com.fastjrun.utils.JacksonUtils;
import com.helger.jcodemodel.*;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

public abstract class BaseControllerMethodGenerator extends AbstractMethodGenerator {

  protected JMethod jClientMethod;

  protected JMethod jClientTestMethod;

  protected JMethod jcontrollerMethod;

  protected ExchangeProcessor exchangeProcessor;

  protected ServiceMethodGenerator serviceMethodGenerator;

  protected BaseControllerGenerator baseControllerGenerator;

  public void setServiceMethodGenerator(ServiceMethodGenerator serviceMethodGenerator) {
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

  public JMethod getjClientTestMethod() {
    return jClientTestMethod;
  }

  public void setjClientTestMethod(JMethod jClientTestMethod) {
    this.jClientTestMethod = jClientTestMethod;
  }

  public JMethod getJcontrollerMethod() {
    return jcontrollerMethod;
  }

  public void setJcontrollerMethod(JMethod jcontrollerMethod) {
    this.jcontrollerMethod = jcontrollerMethod;
  }

  public void processClientTestMethod(JDefinedClass clientTestClass) {
    JMethod clientTestMethod =
        clientTestClass.method(
            JMod.PUBLIC,
            cmTest.VOID,
            "test" + StringHelper.toUpperCaseFirstOne(this.serviceMethodGenerator.methodName));

    JAnnotationUse methodTestAnnotationTest =
        clientTestMethod.annotate(cmTest.ref("org.testng.annotations.Test"));
    JBlock methodTestBlk = clientTestMethod.body();

    methodTestAnnotationTest.param("dataProvider", "loadParam");

    JVar reqParamsJsonStrAndAssertJVar =
        clientTestMethod.param(cmTest.ref("String"), "reqParamsJsonStrAndAssert");
    JVar jsonNodes =
        methodTestBlk.decl(
            cmTest.ref("com.fasterxml.jackson.databind.JsonNode").array(),
            "jsonNodes",
            JExpr.invoke("parseStr2JsonArray").arg(reqParamsJsonStrAndAssertJVar));
    JVar reqParamsJsonJVar =
        methodTestBlk.decl(
            cmTest.ref(JSONOBJECTCLASS_NAME), "reqParamsJson", jsonNodes.component(0));
    JVar assertJsonJVar =
        methodTestBlk.decl(cmTest.ref(JSONOBJECTCLASS_NAME), "assertJson", jsonNodes.component(1));

    JInvocation jInvocationTest =
        JExpr.invoke(JExpr.ref("baseApplicationClient"), this.serviceMethodGenerator.methodName);
    AbstractJClass exceptionClass = cmTest.ref("com.fastjrun.common.ClientException");

    // headParams
    List<PacketField> headVariables =
        this.serviceMethodGenerator.getCommonMethod().getHeadVariables();

    if (headVariables != null && headVariables.size() > 0) {
      for (int index = 0; index < headVariables.size(); index++) {
        PacketField headVariable = headVariables.get(index);
        jInvocationTest.arg(JExpr.ref(headVariable.getNameAlias()));
        MethodGeneratorHelper.processMethodCommonVariables(
            cmTest, methodTestBlk, reqParamsJsonJVar, headVariable, this.packageNamePrefix);
      }
    }

    List<PacketField> pathVariables =
        this.serviceMethodGenerator.getCommonMethod().getPathVariables();
    if (pathVariables != null && pathVariables.size() > 0) {
      for (int index = 0; index < pathVariables.size(); index++) {
        PacketField pathVariable = pathVariables.get(index);
        jInvocationTest.arg(JExpr.ref(pathVariable.getFieldName()));
        MethodGeneratorHelper.processMethodCommonVariables(
            cmTest, methodTestBlk, reqParamsJsonJVar, pathVariable, this.packageNamePrefix);
      }
    }

    List<PacketField> parameters = this.serviceMethodGenerator.getCommonMethod().getParameters();
    if (parameters != null && parameters.size() > 0) {
      for (int index = 0; index < parameters.size(); index++) {
        PacketField parameter = parameters.get(index);
        jInvocationTest.arg(JExpr.ref(parameter.getFieldName()));
        MethodGeneratorHelper.processMethodCommonVariables(
            cmTest, methodTestBlk, reqParamsJsonJVar, parameter, this.packageNamePrefix);
      }
    }

    List<PacketField> cookies = this.serviceMethodGenerator.getCommonMethod().getCookieVariables();
    if (cookies != null && cookies.size() > 0) {
      for (int index = 0; index < cookies.size(); index++) {
        PacketField cookie = cookies.get(index);
        jInvocationTest.arg(JExpr.ref(cookie.getFieldName()));
        MethodGeneratorHelper.processMethodCommonVariables(
            cmTest, methodTestBlk, reqParamsJsonJVar, cookie, this.packageNamePrefix);
      }
    }

    List<PacketField> webParameters =
        this.serviceMethodGenerator.getCommonMethod().getWebParameters();
    if (webParameters != null && webParameters.size() > 0) {
      for (int index = 0; index < webParameters.size(); index++) {
        PacketField webParameter = webParameters.get(index);
        jInvocationTest.arg(JExpr.ref(webParameter.getName()));
        MethodGeneratorHelper.processMethodCommonVariables(
            cmTest, methodTestBlk, reqParamsJsonJVar, webParameter, this.packageNamePrefix);
      }
    }

    if (this.serviceMethodGenerator.getRequestBodyClass() != null) {
      JVar requestBodyVar =
          methodTestBlk.decl(
              this.serviceMethodGenerator.getRequestBodyClass(), "requestBody", JExpr._null());
      JVar requestBodyStrVar =
          methodTestBlk.decl(
              cmTest.ref(JSONOBJECTCLASS_NAME),
              "reqJsonRequestBody",
              reqParamsJsonJVar.invoke("get").arg(JExpr.lit("requestBody")));
      JBlock jNotNullBlock = methodTestBlk._if(requestBodyStrVar.ne(JExpr._null()))._then();
      jNotNullBlock.assign(
          requestBodyVar,
          cmTest
              .ref(JacksonUtilsClassName)
              .staticInvoke("readValue")
              .arg(requestBodyStrVar.invoke("toString"))
              .arg(
                  ((AbstractJClass) this.serviceMethodGenerator.getRequestBodyClass()).dotclass()));
      jInvocationTest.arg(requestBodyVar);
    }
    if (this.serviceMethodGenerator.getResponseBodyClass() != cm.VOID) {
      JVar responseBodyVar =
          methodTestBlk.decl(
              this.serviceMethodGenerator.getResponseBodyClass(), "responseBody", JExpr._null());

      JConditional jConditional1 = methodTestBlk._if(assertJsonJVar.ne(JExpr._null()));
      JBlock jConditional1Block = jConditional1._then();
      JVar codeNodeJVar =
          jConditional1Block.decl(
              cmTest.ref(JSONOBJECTCLASS_NAME),
              "codeNode",
              assertJsonJVar.invoke("get").arg("code"));
      JConditional jConditional2 = jConditional1Block._if(codeNodeJVar.ne(JExpr._null()));
      JBlock jConditional2ThenBlock = jConditional2._then();
      JTryBlock jTry = jConditional2ThenBlock._try();
      jTry.body().assign(responseBodyVar, jInvocationTest);
      JCatchBlock jCatchBlock = jTry._catch(exceptionClass);
      JVar jExceptionVar = jCatchBlock.param("e");
      JBlock jCatchBlockBody = jCatchBlock.body();
      jCatchBlockBody.add(
          cmTest
              .ref("org.testng.Assert")
              .staticInvoke("assertEquals")
              .arg(jExceptionVar.invoke("getCode"))
              .arg(codeNodeJVar.invoke("asText"))
              .arg(JExpr.lit("返回消息码不是指定消息码：").plus(codeNodeJVar.invoke("asText"))));
      jConditional2._else().assign(responseBodyVar, jInvocationTest);
      jConditional1._else().assign(responseBodyVar, jInvocationTest);

      methodTestBlk.add(
          JExpr.refthis("log")
              .invoke("debug")
              .arg(JExpr.lit("response={}"))
              .arg(cmTest.ref(JacksonUtilsClassName).staticInvoke("toJSon").arg(responseBodyVar)));
      JBlock ifBlock1 = methodTestBlk._if(responseBodyVar.ne(JExpr._null()))._then();
      if (this.serviceMethodGenerator.getCommonMethod().isResponseIsArray()) {
        JForLoop forLoop = ifBlock1._for();
        JVar initIndexVar = forLoop.init(cmTest.INT, "index", JExpr.lit(0));
        forLoop.test(initIndexVar.lt(responseBodyVar.invoke("size")));
        forLoop.update(initIndexVar.incr());
        JBlock forBlock1 = forLoop.body();
        JVar responseBodyIndexVar =
            forBlock1.decl(
                this.serviceMethodGenerator.getElementClass(),
                "item" + this.serviceMethodGenerator.getElementClass().name(),
                responseBodyVar.invoke("get").arg(initIndexVar));

        MethodGeneratorHelper.logResponseBody(
            cmTest,
            log,
            packageNamePrefix,
            1,
            this.serviceMethodGenerator.getCommonMethod().getResponse(),
            responseBodyIndexVar,
            forBlock1);

      } else {
        MethodGeneratorHelper.logResponseBody(
            cmTest,
            log,
            packageNamePrefix,
            1,
            this.serviceMethodGenerator.getCommonMethod().getResponse(),
            responseBodyVar,
            ifBlock1);
      }
      ifBlock1.add(
          JExpr._this()
              .invoke("processAssertion")
              .arg(assertJsonJVar)
              .arg(responseBodyVar)
              .arg(JExpr.dotClass(this.serviceMethodGenerator.getResponseBodyClass())));

    } else {
      JConditional jConditional1 = methodTestBlk._if(assertJsonJVar.ne(JExpr._null()));
      JBlock jConditional1Block = jConditional1._then();
      JVar codeNodeJVar =
          jConditional1Block.decl(
              cmTest.ref(JSONOBJECTCLASS_NAME),
              "codeNode",
              assertJsonJVar.invoke("get").arg("code"));
      JConditional jConditional2 = jConditional1Block._if(codeNodeJVar.ne(JExpr._null()));
      JBlock jConditional2ThenBlock = jConditional2._then();
      JTryBlock jTry = jConditional2ThenBlock._try();
      jTry.body().add(jInvocationTest);
      JCatchBlock jCatchBlock = jTry._catch(exceptionClass);
      JVar jExceptionVar = jCatchBlock.param("e");
      JBlock jCatchBlockBody = jCatchBlock.body();
      jCatchBlockBody.add(
          cmTest
              .ref("org.testng.Assert")
              .staticInvoke("assertEquals")
              .arg(jExceptionVar.invoke("getCode"))
              .arg(codeNodeJVar.invoke("asText"))
              .arg(JExpr.lit("返回消息码不是指定消息码：").plus(codeNodeJVar.invoke("asText"))));
      jConditional2._else().add(jInvocationTest);
      jConditional1._else().add(jInvocationTest);
    }
  }

  public void processClientTestPraram() {

    this.methodParamInJsonObject = JacksonUtils.createObjectNode();

    // headParams
    List<PacketField> headVariables =
        this.serviceMethodGenerator.getCommonMethod().getHeadVariables();
    if (headVariables != null && headVariables.size() > 0) {
      for (int index = 0; index < headVariables.size(); index++) {
        PacketField headVariable = headVariables.get(index);
        methodParamInJsonObject.put(headVariable.getNameAlias(), headVariable.getDatatype());
      }
    }
    List<PacketField> pathVariables =
        this.serviceMethodGenerator.getCommonMethod().getPathVariables();
    if (pathVariables != null && pathVariables.size() > 0) {
      for (int index = 0; index < pathVariables.size(); index++) {
        PacketField pathVariable = pathVariables.get(index);
        methodParamInJsonObject.put(pathVariable.getFieldName(), pathVariable.getDatatype());
      }
    }

    List<PacketField> parameters = this.serviceMethodGenerator.getCommonMethod().getParameters();
    if (parameters != null && parameters.size() > 0) {
      for (int index = 0; index < parameters.size(); index++) {
        PacketField parameter = parameters.get(index);
        methodParamInJsonObject.put(parameter.getFieldName(), parameter.getDatatype());
      }
    }
    List<PacketField> cookies = this.serviceMethodGenerator.getCommonMethod().getCookieVariables();
    if (cookies != null && cookies.size() > 0) {
      for (int index = 0; index < cookies.size(); index++) {
        PacketField cookie = cookies.get(index);
        methodParamInJsonObject.put(cookie.getFieldName(), cookie.getDatatype());
      }
    }

    if (this.serviceMethodGenerator.getRequestBodyClass() != null) {
      ObjectNode jsonRequestParam =
          this.composeRequestBody(this.serviceMethodGenerator.getCommonMethod().getRequest());
      methodParamInJsonObject.set("requestBody", jsonRequestParam);
    }
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
    if (methodPath == null || methodPath.equals("")) {
      methodPath = "/" + this.serviceMethodGenerator.getCommonMethod().getName();
    } else if (methodPath.equals("null")) {
      methodPath = "";
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
      JVar requestParam =
          this.jcontrollerMethod.param(
              this.serviceMethodGenerator.getRequestBodyClass(), "requestBody");
      requestParam.annotate(cm.ref("org.springframework.web.bind.annotation.RequestBody"));
      requestParam.annotate(cm.ref("javax.validation.Valid"));
      jInvocation.arg(JExpr.ref("requestBody"));
    }
    this.exchangeProcessor.processResponse(controllerMethodBlk, jInvocation, this.cm);
  }
}
