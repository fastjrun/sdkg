/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.codeg.generator.method;

import com.fastjrun.codeg.common.PacketField;
import com.fastjrun.codeg.common.PacketObject;
import com.fastjrun.codeg.helper.StringHelper;
import com.helger.jcodemodel.*;
import org.slf4j.Logger;

import java.util.List;
import java.util.Map;

import static com.fastjrun.codeg.common.CodeGConstants.JacksonUtilsClassName;
import static com.fastjrun.codeg.generator.common.BaseCMGenerator.JSONOBJECTCLASS_NAME;

public class MethodGeneratorHelper {

  public static void processServiceMethodVariables(
          JMethod jmethod, List<PacketField> variables, JCodeModel cm, String packageNamePrefix)  {
    if (variables != null && variables.size() > 0) {
      for (int index = 0; index < variables.size(); index++) {
        PacketField variable = variables.get(index);
        String dataType = variable.getDatatype();
        if (variable.is_new()) {
          dataType = packageNamePrefix + dataType;
        }

        AbstractJType jType;
        if (dataType.endsWith(":List")) {
          String primitiveType = dataType.split(":")[0];
          jType = cm.ref("java.util.List").narrow(cm.ref(primitiveType));
        } else if (dataType.endsWith(":Array")) {
          String primitiveType = dataType.split(":")[0];
          jType = cm.ref(primitiveType).array();
        } else{
          jType = cm.ref(dataType);
        }
        jmethod.param(jType, variable.getName());
      }
    }
  }

  public static void processMethodCommonVariables(
      JCodeModel cmTest, JBlock methodTestBlk, JVar reqParamsJsonJVar, PacketField parameter, String packageNamePrefix) {
    String dataType = parameter.getDatatype();
    if (parameter.is_new()) {
      dataType = packageNamePrefix + dataType;
    }
    AbstractJType jType;
    String methodName = "readValue";
    String primitiveType = dataType;
    if (dataType.endsWith(":List")) {
      primitiveType = dataType.split(":")[0];
      jType = cmTest.ref("java.util.List").narrow(cmTest.ref(primitiveType));
    } else if (dataType.endsWith(":Array")) {
      primitiveType = dataType.split(":")[0];
      jType = cmTest.ref(primitiveType).array();
      methodName = "readList";
    } else{
      jType = cmTest.ref(dataType);
    }
    String paramterName = parameter.getName();
    if (parameter.getNameAlias() != null && !parameter.getNameAlias().equals("")) {
      paramterName = parameter.getNameAlias();
    }
    JVar jVar = methodTestBlk.decl(jType, paramterName, JExpr._null());
    JVar jJsonVar = methodTestBlk.decl(cmTest.ref(JSONOBJECTCLASS_NAME), paramterName + "jSon",
            reqParamsJsonJVar.invoke("get").arg(JExpr.lit(paramterName)));
    JBlock jNotNullBlock = methodTestBlk._if(jJsonVar.ne(JExpr._null()))._then();
    if (dataType.endsWith(":Array")) {
      jNotNullBlock.add(cmTest.ref(JacksonUtilsClassName).staticInvoke(methodName).arg(
              jJsonVar).arg(cmTest.ref(primitiveType).dotclass()).invoke(
              "toArray").arg(jVar));
    } else{
      jNotNullBlock.assign(jVar, cmTest.ref(JacksonUtilsClassName).staticInvoke(methodName).arg(
              jJsonVar.invoke("asText")).arg(cmTest.ref(primitiveType).dotclass()));

    }
  }

  private static void logResponseBodyField(
      JCodeModel cmTest,
      Logger log,
      int loopSeq,
      PacketObject responseBody,
      JVar responseBodyVar,
      JBlock methodTestBlk) {
    Map<String, PacketField> fields = responseBody.getFields();
    if (fields != null) {
      for (String name : fields.keySet()) {
        PacketField restField = fields.get(name);
        String fieldName=restField.getFieldName();
        boolean canBeNull = restField.isCanBeNull();
        String dataType = restField.getDatatype();
        AbstractJClass jType;
        AbstractJClass primitiveType = null;
        if (dataType.endsWith(":List")) {
          primitiveType = cmTest.ref(dataType.split(":")[0]);
          jType = cmTest.ref("java.util.List").narrow(primitiveType);
        } else {
          // Integer、Double、Long、Boolean、Character、Float、java.util.Date
          jType = cmTest.ref(dataType);
        }
        String lengthInString = restField.getLength();
        int length = 0;
        if (!"".equals(lengthInString)) {
          try {
            length = Integer.parseInt(lengthInString);
          } catch (Exception e) {
            log.error(fieldName + "'s length is assigned a wrong value", e);
          }
        }
        String tterMethodName = fieldName;
        if (tterMethodName.length() > 1) {
          String char2 = String.valueOf(tterMethodName.charAt(1));
          if (!char2.equals(char2.toUpperCase())) {
            tterMethodName = StringHelper.toUpperCaseFirstOne(tterMethodName);
          }
        }
        String getter = restField.getGetter();
        if (getter == null || getter.equals("")) {
          getter = "get" + tterMethodName;
        }
        JVar fieldNameVar =
            methodTestBlk.decl(
                jType,
                StringHelper.toLowerCaseFirstOne(responseBodyVar.type().name())
                    + loopSeq
                    + tterMethodName,
                responseBodyVar.invoke(getter));
        methodTestBlk.add(
            JExpr.ref("log")
                .invoke("debug")
                .arg(JExpr.lit(fieldNameVar.name() + "{}"))
                .arg(fieldNameVar));
        if (!canBeNull) {
          methodTestBlk.add(
              cmTest.ref("org.testng.Assert").staticInvoke("assertNotNull").arg(fieldNameVar));
        }
        if (primitiveType == null && jType.name().equals("String")) {
          JBlock ifBlock1 = methodTestBlk._if(fieldNameVar.ne(JExpr._null()))._then();
          ifBlock1.decl(cmTest.INT, "actualLength", fieldNameVar.invoke("length"));
          JBlock ifBlock2 = ifBlock1._if(JExpr.ref("actualLength").gt(JExpr.lit(length)))._then();
          StringBuilder sbFailReason = new StringBuilder();
          sbFailReason.append(fieldName);
          sbFailReason.append("'s length defined as ");
          sbFailReason.append(length);
          sbFailReason.append(",but returned value's length is ");

          ifBlock2.add(
              cmTest
                  .ref("org.testng.Assert")
                  .staticInvoke("fail")
                  .arg(JExpr.lit(sbFailReason.toString()).plus(JExpr.ref("actualLength"))));
        }
      }
    }
  }

  public static void logResponseBody(
      JCodeModel cmTest,
      Logger log,
      String packageNamePrefix,
      int loopSeq,
      PacketObject responseBody,
      JVar responseBodyVar,
      JBlock methodTestBlk) {

    logResponseBodyField(cmTest, log, loopSeq, responseBody, responseBodyVar, methodTestBlk);
    int start = 1;
    Map<String, PacketObject> robjects = responseBody.getObjects();
    if (robjects != null && robjects.size() > 0) {
      for (String reName : robjects.keySet()) {
        PacketObject ro = robjects.get(reName);
        String tterMethodName = reName;
        if (reName.length() > 1) {
          String char2 = String.valueOf(reName.charAt(1));
          if (!char2.equals(char2.toUpperCase())) {
            tterMethodName = StringHelper.toUpperCaseFirstOne(reName);
          }
        }
        AbstractJClass roClass;
        if (ro.is_new()) {
          roClass = cmTest.ref(packageNamePrefix + ro.get_class());
        } else {
          roClass = cmTest.ref(ro.get_class());
        }
        JVar reNameVar =
            methodTestBlk.decl(
                roClass,
                responseBody.getName() + tterMethodName + loopSeq,
                responseBodyVar.invoke("get" + tterMethodName));
        logResponseBody(
            cmTest, log, packageNamePrefix, loopSeq + start++, ro, reNameVar, methodTestBlk);
      }
    }
    Map<String, PacketObject> roLists = responseBody.getLists();
    if (roLists != null && roLists.size() > 0) {
      int index = 0;
      for (String roName : roLists.keySet()) {
        PacketObject ro = roLists.get(roName);
        String tterMethodName = roName;
        if (roName.length() > 1) {
          String char2 = String.valueOf(roName.charAt(1));
          if (!char2.equals(char2.toUpperCase())) {
            tterMethodName = StringHelper.toUpperCaseFirstOne(roName);
          }
        }
        AbstractJClass roClass;
        if (ro.is_new()) {
          roClass = cmTest.ref(packageNamePrefix + ro.get_class());
        } else {
          roClass = cmTest.ref(ro.get_class());
        }
        JVar roListVar =
            methodTestBlk.decl(
                cmTest.ref("java.util.List").narrow(roClass),
                responseBody.getName() + tterMethodName + "List" + loopSeq + index,
                responseBodyVar.invoke("get" + tterMethodName));
        JBlock ifBlock1 = methodTestBlk._if(roListVar.ne(JExpr._null()))._then();
        JForLoop forLoop = ifBlock1._for();
        JVar initIndexVar =
            forLoop.init(
                cmTest.INT,
                responseBody.getName() + tterMethodName + "ListIndex" + loopSeq + index,
                JExpr.lit(0));
        forLoop.test(initIndexVar.lt(roListVar.invoke("size")));
        forLoop.update(initIndexVar.incr());
        JBlock forBlock1 = forLoop.body();
        JVar responseBodyIndexVar =
            forBlock1.decl(
                roClass, roName + roClass.name(), roListVar.invoke("get").arg(initIndexVar));

        logResponseBody(
            cmTest, log, packageNamePrefix, loopSeq + start++, ro, responseBodyIndexVar, forBlock1);
        index++;
      }
    }
  }
}
