/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.codeg.generator.method;

import com.fastjrun.codeg.common.CodeGException;
import com.fastjrun.codeg.common.CommonMethod;
import com.fastjrun.codeg.common.PacketField;
import com.fastjrun.codeg.common.PacketObject;
import com.fastjrun.codeg.generator.ServiceGenerator;
import com.fastjrun.helper.StringHelper;
import com.helger.jcodemodel.*;

import java.util.List;
import java.util.Map;

public class ServiceMethodGenerator extends AbstractMethodGenerator {

  static String mockHelperClassName = "com.fastjrun.helper.MockHelper";

  protected CommonMethod commonMethod;

  protected String methodName;

  protected JMethod jServiceMethod;

  protected JMethod jServiceMockMethod;

  protected AbstractJType requestBodyClass;

  protected AbstractJType responseBodyClass;

  protected AbstractJClass elementClass;

  protected ServiceGenerator serviceGenerator;

  protected JFieldVar fieldVar;

  public JFieldVar getFieldVar() {
    return fieldVar;
  }

  public void setFieldVar(JFieldVar fieldVar) {
    this.fieldVar = fieldVar;
  }

  public ServiceGenerator getServiceGenerator() {
    return serviceGenerator;
  }

  public void setServiceGenerator(ServiceGenerator serviceGenerator) {
    this.serviceGenerator = serviceGenerator;
  }

  public AbstractJType getResponseBodyClass() {
    return responseBodyClass;
  }

  public void setResponseBodyClass(AbstractJType responseBodyClass) {
    this.responseBodyClass = responseBodyClass;
  }

  public AbstractJClass getElementClass() {
    return elementClass;
  }

  public void setElementClass(AbstractJClass elementClass) {
    this.elementClass = elementClass;
  }

  public AbstractJType getRequestBodyClass() {
    return requestBodyClass;
  }

  public void setRequestBodyClass(AbstractJType requestBodyClass) {
    this.requestBodyClass = requestBodyClass;
  }

  public String getMethodName() {
    return methodName;
  }

  public void setMethodName(String methodName) {
    this.methodName = methodName;
  }

  public JMethod getjServiceMethod() {
    return jServiceMethod;
  }

  public void setjServiceMethod(JMethod jServiceMethod) {
    this.jServiceMethod = jServiceMethod;
  }

  public JMethod getjServiceMockMethod() {
    return jServiceMockMethod;
  }

  public void setjServiceMockMethod(JMethod jServiceMockMethod) {
    this.jServiceMockMethod = jServiceMockMethod;
  }

  public CommonMethod getCommonMethod() {
    return commonMethod;
  }

  public void setCommonMethod(CommonMethod commonMethod) {
    this.commonMethod = commonMethod;
  }

  public void doParse() {
    PacketObject request = this.commonMethod.getRequest();
    if (request != null) {
      if (request.is_new()) {
        this.requestBodyClass = cm.ref(this.packageNamePrefix + request.get_class());
      } else {
        this.requestBodyClass = cm.ref(request.get_class());
      }
    }
    PacketObject response = this.commonMethod.getResponse();
    if (response == null) {
      this.responseBodyClass = cm.VOID;
    } else {
      String responseClassP = response.get_class();
      if (response.is_new()) {
        this.elementClass = cm.ref(this.packageNamePrefix + responseClassP);
      } else {
        this.elementClass = cm.ref(responseClassP);
      }
      if (this.commonMethod.isResponseIsArray()) {
        this.responseBodyClass = cm.ref("java.util.List").narrow(elementClass);
      } else {
        this.responseBodyClass = elementClass;
      }
    }
  }

  public void processServiceMethod() {
    this.jServiceMethod =
        this.serviceGenerator
            .getServiceClass()
            .method(JMod.NONE, this.responseBodyClass, this.methodName);
    String methodRemark = commonMethod.getRemark();
    this.jServiceMethod.javadoc().append(methodRemark);

    MethodGeneratorHelper.processServiceMethodVariables(
        this.jServiceMethod, this.commonMethod.getHeadVariables(), this.cm);
    MethodGeneratorHelper.processServiceMethodVariables(
        this.jServiceMethod, this.commonMethod.getPathVariables(), this.cm);
    MethodGeneratorHelper.processServiceMethodVariables(
        this.jServiceMethod, this.commonMethod.getParameters(), this.cm);
    MethodGeneratorHelper.processServiceMethodVariables(
        this.jServiceMethod, this.commonMethod.getCookieVariables(), this.cm);

    if (this.requestBodyClass != null) {
      this.jServiceMethod.param(this.requestBodyClass, "requestBody");
    }
  }

  public void processServiceTestMethod() {
    JDefinedClass serviceTestClass = this.serviceGenerator.getServiceTestClass();
    JMethod jServiceTestMethod =
        serviceTestClass.method(
            JMod.PUBLIC, cmTest.VOID, "test" + StringHelper.toUpperCaseFirstOne(this.methodName));

    JAnnotationUse methodTestAnnotationTest =
        jServiceTestMethod.annotate(cmTest.ref("org.testng.annotations.Test"));
    JBlock methodTestBlk = jServiceTestMethod.body();

    methodTestAnnotationTest.param("dataProvider", "loadParam");

    JVar reqParamsJsonStrAndAssertJVar =
        jServiceTestMethod.param(cmTest.ref("String"), "reqParamsJsonStrAndAssert");
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

    JInvocation jInvocationTest = JExpr.invoke(fieldVar, this.methodName);
    AbstractJClass exceptionClass = cmTest.ref("com.fastjrun.common.ServiceException");
    // headParams
    List<PacketField> headVariables = this.getCommonMethod().getHeadVariables();

    if (headVariables != null && headVariables.size() > 0) {
      for (int index = 0; index < headVariables.size(); index++) {
        PacketField headVariable = headVariables.get(index);
        jInvocationTest.arg(JExpr.ref(headVariable.getNameAlias()));
        MethodGeneratorHelper.processMethodCommonVariables(
            cmTest, methodTestBlk, reqParamsJsonJVar, headVariable);
      }
    }

    List<PacketField> pathVariables = this.getCommonMethod().getPathVariables();
    if (pathVariables != null && pathVariables.size() > 0) {
      for (int index = 0; index < pathVariables.size(); index++) {
        PacketField pathVariable = pathVariables.get(index);
        jInvocationTest.arg(JExpr.ref(pathVariable.getFieldName()));
        MethodGeneratorHelper.processMethodCommonVariables(
            cmTest, methodTestBlk, reqParamsJsonJVar, pathVariable);
      }
    }

    List<PacketField> parameters = this.getCommonMethod().getParameters();
    if (parameters != null && parameters.size() > 0) {
      for (int index = 0; index < parameters.size(); index++) {
        PacketField parameter = parameters.get(index);
        jInvocationTest.arg(JExpr.ref(parameter.getFieldName()));
        MethodGeneratorHelper.processMethodCommonVariables(
            cmTest, methodTestBlk, reqParamsJsonJVar, parameter);
      }
    }

    List<PacketField> cookies = this.getCommonMethod().getCookieVariables();
    if (cookies != null && cookies.size() > 0) {
      for (int index = 0; index < cookies.size(); index++) {
        PacketField cookie = cookies.get(index);
        jInvocationTest.arg(JExpr.ref(cookie.getFieldName()));
        MethodGeneratorHelper.processMethodCommonVariables(
            cmTest, methodTestBlk, reqParamsJsonJVar, cookie);
      }
    }
    if (this.getRequestBodyClass() != null) {
      JVar requestBodyVar =
          methodTestBlk.decl(this.getRequestBodyClass(), "requestBody", JExpr._null());
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
              .arg(((AbstractJClass) this.getRequestBodyClass()).dotclass()));
      jInvocationTest.arg(requestBodyVar);
    }
    if (this.getResponseBodyClass() != cm.VOID) {
      JVar responseBodyVar =
          methodTestBlk.decl(this.getResponseBodyClass(), "responseBody", JExpr._null());

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
      if (this.getCommonMethod().isResponseIsArray()) {
        JForLoop forLoop = ifBlock1._for();
        JVar initIndexVar = forLoop.init(cmTest.INT, "index", JExpr.lit(0));
        forLoop.test(initIndexVar.lt(responseBodyVar.invoke("size")));
        forLoop.update(initIndexVar.incr());
        JBlock forBlock1 = forLoop.body();
        JVar responseBodyIndexVar =
            forBlock1.decl(
                this.getElementClass(),
                "item" + this.getElementClass().name(),
                responseBodyVar.invoke("get").arg(initIndexVar));

        MethodGeneratorHelper.logResponseBody(
            cmTest,
            log,
            packageNamePrefix,
            1,
            this.getCommonMethod().getResponse(),
            responseBodyIndexVar,
            forBlock1);

      } else {
        MethodGeneratorHelper.logResponseBody(
            cmTest,
            log,
            packageNamePrefix,
            1,
            this.getCommonMethod().getResponse(),
            responseBodyVar,
            ifBlock1);
      }
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

  public void processServiceMockMethod() {
    this.jServiceMockMethod =
        this.serviceGenerator
            .getServiceMockClass()
            .method(JMod.PUBLIC, this.responseBodyClass, this.methodName);
    String methodRemark = commonMethod.getRemark();
    this.jServiceMockMethod.javadoc().append(methodRemark);

    MethodGeneratorHelper.processServiceMethodVariables(
        this.jServiceMockMethod, this.commonMethod.getHeadVariables(), this.cm);
    MethodGeneratorHelper.processServiceMethodVariables(
        this.jServiceMockMethod, this.commonMethod.getPathVariables(), this.cm);
    MethodGeneratorHelper.processServiceMethodVariables(
        this.jServiceMockMethod, this.commonMethod.getParameters(), this.cm);
    MethodGeneratorHelper.processServiceMethodVariables(
        this.jServiceMockMethod, this.commonMethod.getCookieVariables(), this.cm);
    ;

    if (this.requestBodyClass != null) {
      this.jServiceMockMethod.param(this.requestBodyClass, "requestBody");
    }
    this.jServiceMockMethod.annotate(cm.ref("java.lang.Override"));
    JBlock serviceMockMethodBlock = this.jServiceMockMethod.body();
    if (this.responseBodyClass != cm.VOID) {
      if (!commonMethod.isResponseIsArray()) {
        if (responseBodyClass.name().endsWith("Boolean")) {
          serviceMockMethodBlock._return(cm.ref(mockHelperClassName).staticInvoke("geBoolean"));
        } else if (responseBodyClass.name().endsWith("Integer")) {
          serviceMockMethodBlock._return(cm.ref(mockHelperClassName).staticInvoke("geInteger"));
        } else if (responseBodyClass.name().endsWith("Long")) {
          serviceMockMethodBlock._return(cm.ref(mockHelperClassName).staticInvoke("geLong"));
        } else if (responseBodyClass.name().endsWith("Double")) {
          serviceMockMethodBlock._return(cm.ref(mockHelperClassName).staticInvoke("geDouble"));
        } else if (responseBodyClass.name().endsWith("String")) {
          serviceMockMethodBlock._return(
              cm.ref(mockHelperClassName)
                  .staticInvoke("geStringWithAlphabetic")
                  .arg(JExpr.lit(10)));
        } else if (responseBodyClass.name().endsWith("Date")) {
          serviceMockMethodBlock._return(
              cm.ref(mockHelperClassName).staticInvoke("geDate").arg(JExpr.lit(10)));
        } else {
          JVar responseBodyVar =
              this.composeResponseBody(
                  0, serviceMockMethodBlock, commonMethod.getResponse(), this.elementClass);
          serviceMockMethodBlock._return(responseBodyVar);
        }
      } else {
        if (elementClass.name().endsWith("Boolean")) {
          serviceMockMethodBlock._return(
              cm.ref(mockHelperClassName).staticInvoke("geBooleanList").arg(JExpr.lit(10)));
        } else if (elementClass.name().endsWith("Integer")) {
          serviceMockMethodBlock._return(
              cm.ref(mockHelperClassName).staticInvoke("geIntegerList").arg(JExpr.lit(10)));
        } else if (elementClass.name().endsWith("Long")) {
          serviceMockMethodBlock._return(
              cm.ref(mockHelperClassName).staticInvoke("geLongList").arg(JExpr.lit(10)));
        } else if (elementClass.name().endsWith("Double")) {
          serviceMockMethodBlock._return(
              cm.ref(mockHelperClassName).staticInvoke("geDoubleList").arg(JExpr.lit(10)));
        } else if (elementClass.name().endsWith("String")) {
          serviceMockMethodBlock._return(
              cm.ref(mockHelperClassName).staticInvoke("geStringListWithAscii").arg(JExpr.lit(10)));
        } else if (elementClass.name().endsWith("Date")) {
          serviceMockMethodBlock._return(
              cm.ref(mockHelperClassName).staticInvoke("geDateList").arg(JExpr.lit(10)));
        } else {
          JVar responseVar =
              serviceMockMethodBlock.decl(
                  this.responseBodyClass,
                  "response",
                  JExpr._new(cm.ref("java.util.ArrayList").narrow(this.elementClass)));
          JVar responseBodyVar =
              this.composeResponseBody(
                  0, serviceMockMethodBlock, commonMethod.getResponse(), this.elementClass);
          serviceMockMethodBlock.add(responseVar.invoke("add").arg(responseBodyVar));
          serviceMockMethodBlock._return(responseVar);
        }
      }
    }
  }

  private JVar composeResponseBody(
      int loopSeq, JBlock methodBlk, PacketObject responseBody, AbstractJType responseBodyClass) {
    JVar responseVar =
        composeResponseBodyField(loopSeq, methodBlk, responseBody, responseBodyClass);
    int start = 1;
    Map<String, PacketObject> packetObjectMap = responseBody.getObjects();
    if (packetObjectMap != null && packetObjectMap.size() > 0) {
      for (String reName : packetObjectMap.keySet()) {
        PacketObject ro = packetObjectMap.get(reName);
        AbstractJClass roClass = cm.ref(this.packageNamePrefix + ro.get_class());
        if (!ro.is_new()) {
          roClass = cm.ref(ro.get_class());
        }
        JVar roVar = this.composeResponseBody(loopSeq + start++, methodBlk, ro, roClass);
        String tterMethodName = reName;
        if (reName.length() > 1) {
          String char2 = String.valueOf(reName.charAt(1));
          if (!char2.equals(char2.toUpperCase())) {
            tterMethodName = StringHelper.toUpperCaseFirstOne(reName);
          }
        }
        methodBlk.add(responseVar.invoke("set" + tterMethodName).arg(roVar));
      }
    }
    Map<String, PacketObject> roList = responseBody.getLists();
    if (roList != null && roList.size() > 0) {
      for (String listName : roList.keySet()) {
        int index = 0;
        PacketObject ro = roList.get(listName);
        AbstractJType roListEntityClass = cm.ref(this.packageNamePrefix + ro.get_class());
        if (!ro.is_new()) {
          roListEntityClass = cm.ref(ro.get_class());
        }
        String varNamePrefixList =
            StringHelper.toLowerCaseFirstOne(
                roListEntityClass.name().substring(roListEntityClass.name().lastIndexOf(".") + 1));
        JVar listsVar =
            methodBlk.decl(
                cm.ref("java.util.List").narrow(roListEntityClass),
                varNamePrefixList + "list",
                JExpr._new(cm.ref("java.util.ArrayList").narrow(roListEntityClass)));
        JVar iSizeVar =
            methodBlk.decl(
                cm.INT,
                "iSize" + listName + loopSeq + index,
                cm.ref(mockHelperClassName)
                    .staticInvoke("geInteger")
                    .arg(JExpr.lit(10))
                    .invoke("intValue"));
        JForLoop forLoop = methodBlk._for();
        JVar iVar = forLoop.init(cm.INT, "i" + loopSeq + index, JExpr.lit(0));
        forLoop.test(iVar.lt(iSizeVar));
        forLoop.update(iVar.incr());
        JBlock forBody = forLoop.body();
        JVar roVar = composeResponseBody(loopSeq + start++, forBody, ro, roListEntityClass);
        forBody.add(listsVar.invoke("add").arg(roVar));
        String tterMethodName = listName;
        if (listName.length() > 1) {
          String char2 = String.valueOf(listName.charAt(1));
          if (!char2.equals(char2.toUpperCase())) {
            tterMethodName = StringHelper.toUpperCaseFirstOne(listName);
          }
        }
        methodBlk.add(responseVar.invoke("set" + tterMethodName).arg(listsVar));
        index++;
      }
    }
    return responseVar;
  }

  private JVar composeResponseBodyField(
      int loopSeq, JBlock methodBlk, PacketObject responseBody, AbstractJType responseBodyClass) {
    String varNamePrefix =
        StringHelper.toLowerCaseFirstOne(
                responseBody.get_class().substring(responseBody.get_class().lastIndexOf(".") + 1))
            + loopSeq;
    JVar reponseBodyVar =
        methodBlk.decl(responseBodyClass, varNamePrefix, JExpr._new(responseBodyClass));
    Map<String, PacketField> restFields = responseBody.getFields();
    if (restFields != null && restFields.size() > 0) {
      for (String name : restFields.keySet()) {
        PacketField restField = restFields.get(name);
        String dataType = restField.getDatatype();
        String length = restField.getLength();
        AbstractJType jType;
        String primitiveType = null;
        if (dataType.endsWith(":List")) {
          primitiveType = dataType.split(":")[0];
          jType = cm.ref("java.util.List").narrow(cm.ref(primitiveType));
        } else {
          jType = cm.ref(dataType);
        }
        String fieldName = restField.getFieldName();
        String tterMethodName = fieldName;
        if (fieldName.length() > 1) {
          String char2 = String.valueOf(fieldName.charAt(1));
          if (!char2.equals(char2.toUpperCase())) {
            tterMethodName = StringHelper.toUpperCaseFirstOne(fieldName);
          }
        }
        String setter = restField.getSetter();
        if (setter == null || setter.equals("")) {
          tterMethodName = "set" + tterMethodName;
        }
        if (primitiveType != null) {
          if (primitiveType.endsWith("String")) {
            methodBlk.add(
                reponseBodyVar
                    .invoke(tterMethodName)
                    .arg(
                        (cm.ref(mockHelperClassName)
                            .staticInvoke("geStringListWithAscii")
                            .arg(JExpr.lit(10)))));
          } else if (primitiveType.endsWith("Boolean")) {
            methodBlk.add(
                reponseBodyVar
                    .invoke(tterMethodName)
                    .arg(
                        (cm.ref(mockHelperClassName)
                            .staticInvoke("geBooleanList")
                            .arg(JExpr.lit(10)))));
          } else if (primitiveType.endsWith("Integer")) {
            methodBlk.add(
                reponseBodyVar
                    .invoke(tterMethodName)
                    .arg(
                        (cm.ref(mockHelperClassName)
                            .staticInvoke("geIntegerList")
                            .arg(JExpr.lit(10)))));
          } else if (primitiveType.endsWith("Long")) {
            methodBlk.add(
                reponseBodyVar
                    .invoke(tterMethodName)
                    .arg(
                        (cm.ref(mockHelperClassName)
                            .staticInvoke("geLongList")
                            .arg(JExpr.lit(10)))));
          } else if (primitiveType.endsWith("Float")) {
            methodBlk.add(
                reponseBodyVar
                    .invoke(tterMethodName)
                    .arg(
                        (cm.ref(mockHelperClassName)
                            .staticInvoke("geFloatList")
                            .arg(JExpr.lit(10)))));
          } else if (primitiveType.endsWith("Double")) {
            methodBlk.add(
                reponseBodyVar
                    .invoke(tterMethodName)
                    .arg(
                        (cm.ref(mockHelperClassName)
                            .staticInvoke("geDoubleList")
                            .arg(JExpr.lit(10)))));
          } else if (primitiveType.endsWith("Date")) {
            methodBlk.add(
                reponseBodyVar
                    .invoke(tterMethodName)
                    .arg(
                        (cm.ref(mockHelperClassName)
                            .staticInvoke("geDateList")
                            .arg(JExpr.lit(10)))));
          } else {
            throw new CodeGException(
                "CG504",
                responseBodyClass.name() + "." + tterMethodName + " handled failed:for" + dataType);
          }
        } else if (jType.name().endsWith("String")) {
          methodBlk.add(
              reponseBodyVar
                  .invoke(tterMethodName)
                  .arg(
                      (cm.ref(mockHelperClassName)
                          .staticInvoke("geStringWithAscii")
                          .arg(JExpr.lit(Integer.parseInt(length))))));
        } else if (jType.name().endsWith("Boolean")) {
          methodBlk.add(
              reponseBodyVar
                  .invoke(tterMethodName)
                  .arg((cm.ref(mockHelperClassName).staticInvoke("geBoolean"))));
        } else if (jType.name().endsWith("Integer")) {
          methodBlk.add(
              reponseBodyVar
                  .invoke(tterMethodName)
                  .arg(
                      (cm.ref(mockHelperClassName).staticInvoke("geInteger").arg(JExpr.lit(100)))));
        } else if (jType.name().endsWith("Long")) {
          methodBlk.add(
              reponseBodyVar
                  .invoke(tterMethodName)
                  .arg((cm.ref(mockHelperClassName).staticInvoke("geLong").arg(JExpr.lit(100)))));
        } else if (jType.name().endsWith("Float")) {
          methodBlk.add(
              reponseBodyVar
                  .invoke(tterMethodName)
                  .arg((cm.ref(mockHelperClassName).staticInvoke("geFloat").arg(JExpr.lit(100)))));
        } else if (jType.name().endsWith("Double")) {
          methodBlk.add(
              reponseBodyVar
                  .invoke(tterMethodName)
                  .arg((cm.ref(mockHelperClassName).staticInvoke("geDouble").arg(JExpr.lit(100)))));
        } else if (jType.name().endsWith("Date")) {
          methodBlk.add(
              reponseBodyVar
                  .invoke(tterMethodName)
                  .arg((cm.ref(mockHelperClassName).staticInvoke("geDate").arg(JExpr.lit(100)))));
        } else {
          throw new CodeGException(
              "CG504",
              responseBodyClass.name() + "." + tterMethodName + " handled failed:for" + dataType);
        }
      }
    }
    return reponseBodyVar;
  }

  @Override
  public void generate() {
    this.methodName = commonMethod.getName();

    String methodVersion = commonMethod.getVersion();
    if (methodVersion != null && !methodVersion.equals("")) {
      this.methodName = this.methodName + methodVersion;
    }
    this.doParse();
    if (!this.isApi()) {
      this.processServiceMethod();
      if (this.serviceGenerator.isSupportTest()) {
        this.processServiceTestMethod();
      }
      if (!this.isClient()) {
        if (this.getMockModel() != MockModel.MockModel_Common) {
          this.processServiceMockMethod();
        }
      }
    }
  }
}
