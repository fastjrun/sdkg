/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.codeg.generator;

import com.fastjrun.codeg.common.*;
import com.fastjrun.codeg.generator.common.BaseCMGenerator;
import com.fastjrun.helper.StringHelper;
import com.helger.jcodemodel.*;

import java.util.Map;

public class PacketGenerator extends BaseCMGenerator {

  private PacketObject packetObject;

  private AbstractJClass packetObjectJClass;

  public PacketObject getPacketObject() {
    return packetObject;
  }

  public void setPacketObject(PacketObject packetObject) {
    this.packetObject = packetObject;
  }

  public AbstractJClass getPacketObjectJClass() {
    return packetObjectJClass;
  }

  public void setPacketObjectJClass(AbstractJClass packetObjectJClass) {
    this.packetObjectJClass = packetObjectJClass;
  }

  @Override
  public void generate() {
    if (!this.packetObject.is_new()) {
      this.packetObjectJClass = cm.ref(packetObject.get_class());
    } else {
      JDefinedClass dc;
      try {
        dc = cm._class(this.packageNamePrefix + packetObject.get_class());
      } catch (JClassAlreadyExistsException e) {
        String msg = packetObject.get_class() + " is already exists.";
        log.error(msg, e);
        throw new CodeGException(CodeGMsgContants.CODEG_CLASS_EXISTS, msg, e);
      }

      log.debug(packetObject.get_class());
      dc._implements(cm.ref("java.io.Serializable"));
      if (this.packetObject.getParent() != null && !this.packetObject.getParent().equals("")) {
        dc._extends(cm.ref(this.packetObject.getParent()));
      }
      this.packetObjectJClass = dc;
      Long hashCode = 0L;
      hashCode += dc.getClass().getName().hashCode();

      Map<String, PacketField> fields = packetObject.getFields();
      JMethod toStringMethod = dc.method(JMod.PUBLIC, cm.ref("String"), "toString");
      toStringMethod.annotate(cm.ref("Override"));
      JBlock toStringMethodBlk = toStringMethod.body();
      JVar toStringSBVar =
          toStringMethodBlk.decl(
              cm.ref("StringBuilder"), "sb", JExpr._new(cm.ref("StringBuilder")));
      toStringMethodBlk.add(
          toStringSBVar.invoke("append").arg(JExpr.lit(dc.name()).plus(JExpr.lit(" ["))));
      if (fields != null && fields.size() > 0) {
        int index = 0;
        toStringMethodBlk.add(toStringSBVar.invoke("append").arg(JExpr.lit("field [")));
        for (PacketField field : fields.values()) {
          this.processField(index, field, dc, hashCode, toStringMethodBlk, toStringSBVar);
          index++;
        }
        toStringMethodBlk.add(toStringSBVar.invoke("append").arg(JExpr.lit("]")));
      }

      Map<String, PacketObject> objects = packetObject.getObjects();
      if (objects != null && objects.size() > 0) {
        int index = 0;
        toStringMethodBlk.add(toStringSBVar.invoke("append").arg(JExpr.lit("object [")));
        for (String key : objects.keySet()) {
          hashCode += key.hashCode();
          PacketObject object = objects.get(key);
          AbstractJClass jObjectClass =
              this.processObjectORList(
                  key, object, true, hashCode, dc, index, toStringMethodBlk, toStringSBVar);
          hashCode += jObjectClass.hashCode();
          // javabean命名规范：属性第二字母大写，则setter和getter方法首字母和第二字母都大写
          String tterMethodName = key;
          if (key.length() > 1) {
            String char2 = String.valueOf(key.charAt(1));
            if (!char2.equals(char2.toUpperCase())) {
              tterMethodName = StringHelper.toUpperCaseFirstOne(key);
            }
          }
          JFieldRef nameRef = JExpr.refthis(key);

          JMethod setMethod;
          JVar jvar;
          JMethod getMethod;
          dc.field(JMod.PRIVATE, jObjectClass, key);
          setMethod = dc.method(JMod.PUBLIC, cm.VOID, "set" + tterMethodName);
          getMethod = dc.method(JMod.PUBLIC, jObjectClass, "get" + tterMethodName);
          jvar = setMethod.param(jObjectClass, key);
          JBlock setMethodBlk = setMethod.body();
          hashCode += setMethod.name().hashCode();
          setMethodBlk.assign(nameRef, jvar);

          hashCode += getMethod.name().hashCode();
          JBlock getMethodBlk = getMethod.body();
          getMethodBlk._return(nameRef);

          if (index > 0) {
            toStringMethodBlk.add(toStringSBVar.invoke("append").arg(JExpr.lit(",")));
          }
          toStringMethodBlk.add(toStringSBVar.invoke("append").arg(JExpr.lit(key)));
          toStringMethodBlk.add(toStringSBVar.invoke("append").arg(JExpr.lit("=")));
          JConditional jConditional = toStringMethodBlk._if(nameRef.ne(JExpr._null()));
          jConditional._else().add(toStringSBVar.invoke("append").arg(JExpr.lit("null")));
          JBlock jThenBlock = jConditional._then();
          jThenBlock.add(toStringSBVar.invoke("append").arg(nameRef));
        }
        toStringMethodBlk.add(toStringSBVar.invoke("append").arg(JExpr.lit("]")));
      }

      Map<String, PacketObject> lists = packetObject.getLists();
      if (lists != null && lists.size() > 0) {
        int index = 0;
        toStringMethodBlk.add(toStringSBVar.invoke("append").arg(JExpr.lit("list [")));
        for (String key : lists.keySet()) {
          hashCode += key.hashCode();
          PacketObject list = lists.get(key);
          AbstractJClass jListObject =
              this.processObjectORList(
                  key, list, false, hashCode, dc, index, toStringMethodBlk, toStringSBVar);
          hashCode += jListObject.hashCode();
          // javabean命名规范：属性第二字母大写，则setter和getter方法首字母和第二字母都大写
          String tterMethodName = key;
          if (key.length() > 1) {
            String char2 = String.valueOf(key.charAt(1));
            if (!char2.equals(char2.toUpperCase())) {
              tterMethodName = StringHelper.toUpperCaseFirstOne(key);
            }
          }
          JFieldRef nameRef = JExpr.refthis(key);

          JMethod setMethod;
          JVar jvar;
          JMethod getMethod;
          dc.field(JMod.PRIVATE, cm.ref("java.util.List").narrow(jListObject), key);
          setMethod = dc.method(JMod.PUBLIC, cm.VOID, "set" + tterMethodName);
          getMethod =
              dc.method(
                  JMod.PUBLIC,
                  cm.ref("java.util.List").narrow(jListObject),
                  "get" + tterMethodName);
          jvar = setMethod.param(cm.ref("java.util.List").narrow(jListObject), key);
          index++;
          JBlock setMethodBlk = setMethod.body();
          hashCode += setMethod.name().hashCode();
          setMethodBlk.assign(nameRef, jvar);

          hashCode += getMethod.name().hashCode();
          JBlock getMethodBlk = getMethod.body();
          getMethodBlk._return(nameRef);

          if (index > 0) {
            toStringMethodBlk.add(toStringSBVar.invoke("append").arg(JExpr.lit(",")));
          }
          toStringMethodBlk.add(toStringSBVar.invoke("append").arg(JExpr.lit(key)));
          toStringMethodBlk.add(toStringSBVar.invoke("append").arg(JExpr.lit("=")));
          JConditional jConditional = toStringMethodBlk._if(nameRef.ne(JExpr._null()));
          jConditional._else().add(toStringSBVar.invoke("append").arg(JExpr.lit("null")));
          JBlock jThenBlock = jConditional._then();
          JForLoop forLoop = jThenBlock._for();
          JVar iVar = forLoop.init(cm.INT, "i", JExpr.lit(0));
          forLoop.test(iVar.lt(nameRef.invoke("size")));
          forLoop.update(iVar.incr());
          JBlock forBody = forLoop.body();
          String poDcName = StringHelper.toLowerCaseFirstOne(jListObject.name());
          JVar poDcObjectVar = forBody.decl(jListObject, poDcName, nameRef.invoke("get").arg(iVar));
          forBody
              ._if(iVar.eq(JExpr.lit(0)))
              ._then()
              .add(toStringSBVar.invoke("append").arg(JExpr.lit("[")));
          forBody
              ._if(iVar.gt(JExpr.lit(0)))
              ._then()
              .add(toStringSBVar.invoke("append").arg(JExpr.lit(",")));
          forBody.add(toStringSBVar.invoke("append").arg(JExpr.lit("list.")));
          forBody.add(toStringSBVar.invoke("append").arg(iVar));
          forBody.add(toStringSBVar.invoke("append").arg(JExpr.lit("=")));
          forBody.add(toStringSBVar.invoke("append").arg(poDcObjectVar));
          jThenBlock.add(toStringSBVar.invoke("append").arg(JExpr.lit("]")));
        }
        toStringMethodBlk.add(toStringSBVar.invoke("append").arg(JExpr.lit("]")));
      }
      dc.field(
          JMod.PRIVATE + JMod.STATIC + JMod.FINAL,
          cm.LONG,
          "serialVersionUID",
          JExpr.lit(hashCode));
      toStringMethodBlk.add(toStringSBVar.invoke("append").arg(JExpr.lit("]")));
      toStringMethodBlk._return(toStringSBVar.invoke("toString"));
      this.addClassDeclaration(dc);
    }
  }

  public void processField(
      int index,
      PacketField field,
      JDefinedClass dc,
      Long hashCode,
      JBlock toStringMethodBlk,
      JVar toStringSBVar) {

    String fieldName = field.getFieldName();
    String dataType = field.getDatatype();
    hashCode += fieldName.hashCode();
    AbstractJType jType;
    if (dataType.endsWith(":List")) {
      String primitiveType = dataType.split(":")[0];
      jType = cm.ref("java.util.List").narrow(cm.ref(primitiveType));
    } else {
      jType = cm.ref(dataType);
    }
    JFieldVar fieldVar = dc.field(JMod.PRIVATE, jType, fieldName);
    if (!fieldName.equals(field.getName())) {
      fieldVar
          .annotate(cm.ref("com.fasterxml.jackson.annotation.JsonProperty"))
          .param(field.getName());
    }
    if (this.getMockModel() == CodeGConstants.MockModel.MockModel_Swagger) {
      fieldVar
          .annotate(cm.ref("io.swagger.annotations.ApiModelProperty"))
          .param("value", field.getRemark())
          .param("required", JExpr.lit(!field.isCanBeNull()));
    }
    JDocComment jdoc = fieldVar.javadoc();
    // 成员变量注释
    jdoc.add(field.getRemark());
    JFieldRef nameRef = JExpr.refthis(fieldName);
    if (index > 0) {
      toStringMethodBlk.add(toStringSBVar.invoke("append").arg(JExpr.lit(",")));
    }

    toStringMethodBlk.add(toStringSBVar.invoke("append").arg(JExpr.lit(fieldName)));
    toStringMethodBlk.add(toStringSBVar.invoke("append").arg(JExpr.lit("=")));
    toStringMethodBlk.add(toStringSBVar.invoke("append").arg(nameRef));

    // javabean命名规范：属性第二字母大写，则setter和getter方法首字母和第二字母都大写

    String tterMethodName = fieldName;
    if (fieldName.length() > 1) {
      String char2 = String.valueOf(fieldName.charAt(1));
      if (!char2.equals(char2.toUpperCase())) {
        tterMethodName = StringHelper.toUpperCaseFirstOne(fieldName);
      }
    }

    JMethod getMethod = dc.method(JMod.PUBLIC, jType, "get" + tterMethodName);
    hashCode += getMethod.name().hashCode();
    JBlock getMethodBlk = getMethod.body();
    getMethodBlk._return(nameRef);
    JMethod setMethod = dc.method(JMod.PUBLIC, cm.VOID, "set" + tterMethodName);
    JVar jvar = setMethod.param(jType, fieldName);
    hashCode += setMethod.name().hashCode();
    JBlock setMethodBlk = setMethod.body();

    // 对于String的属性在返回的报文中，默认为空字符串
    if (dc.getPackage().name().endsWith("res") && jvar.type().name().equals("String")) {
      JConditional ifBlock = setMethodBlk._if(jvar.eq(JExpr._null()));
      ifBlock._then().assign(nameRef, JExpr.lit(""));
      ifBlock._else().assign(nameRef, jvar);

    } else {
      setMethodBlk.assign(nameRef, jvar);
    }
  }

  public AbstractJClass processObjectORList(
      String key,
      PacketObject po,
      boolean isObject,
      Long hashCode,
      JDefinedClass dc,
      int index,
      JBlock toStringMethodBlk,
      JVar toStringSBVar) {
    if (po.is_new()) {
      return cm.ref(this.packageNamePrefix + po.get_class());
    } else {
      return cm.ref(po.get_class());
    }
  }
}
