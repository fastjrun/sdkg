package com.fastjrun.codeg.bundle;

import java.util.Map;

import com.fastjrun.codeg.CodeGException;
import com.fastjrun.codeg.CodeGenerator;
import com.fastjrun.codeg.bundle.common.RestField;
import com.fastjrun.codeg.bundle.common.RestObject;
import com.fastjrun.codeg.helper.StringHelper;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JConditional;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JDocComment;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldRef;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;

public abstract class PacketGenerator extends CodeGenerator {

    protected String[] bundleFiles;

    public String[] getBundleFiles() {
        return bundleFiles;
    }

    public void setBundleFiles(String[] bundleFiles) {
        this.bundleFiles = bundleFiles;
    }

    protected JClass processBody(RestObject body, JClass parentClass) {
        try {
            JDefinedClass dc = cm._class(this.packageNamePrefix
                    + body.get_class());

            if (parentClass != null) {
                dc._extends(parentClass);
                dc._implements(cm.ref("java.io.Serializable"));
            }
            long hashCode = 0l;
            hashCode += dc.getClass().getName().hashCode();
            // 生成类级的 javadoc
            JDocComment jdoc = dc.javadoc();
            jdoc.addXdoclet("author fastjrun");
            Map<String, RestField> fields = body.getFields();
            JMethod toStringMethod = dc.method(JMod.PUBLIC, cm.ref("String"),
                    "toString");
            toStringMethod.annotate(cm.ref("Override"));
            JBlock toStringMethodBlk = toStringMethod.body();
            JVar toStringSBVar = toStringMethodBlk.decl(
                    cm.ref("StringBuilder"), "sb",
                    JExpr._new(cm.ref("StringBuilder")));
            toStringMethodBlk.invoke(toStringSBVar, "append").arg(
                    JExpr.lit(dc.name()).plus(JExpr.lit(" [")));
            if (fields != null && fields.size() > 0) {
                int index = 0;
                toStringMethodBlk.invoke(toStringSBVar, "append").arg(
                        JExpr.lit("field ["));
                for (RestField field : fields.values()) {
                    this.processField(index, field, dc, hashCode,
                            toStringMethodBlk, toStringSBVar);
                    index++;
                }
                toStringMethodBlk.invoke(toStringSBVar, "append").arg(
                        JExpr.lit("]"));
            }

            Map<String, RestObject> objects = body.getObjects();
            if (objects != null && objects.size() > 0) {
                int index = 0;
                toStringMethodBlk.invoke(toStringSBVar, "append").arg(
                        JExpr.lit("object ["));
                for (String key : objects.keySet()) {
                    hashCode += key.hashCode();
                    RestObject object = objects.get(key);
                    this.processRO(key, object, hashCode, dc, index,
                            toStringMethodBlk, toStringSBVar);

                }
                toStringMethodBlk.invoke(toStringSBVar, "append").arg(
                        JExpr.lit("]"));
            }

            Map<String, RestObject> lists = body.getLists();
            if (lists != null && lists.size() > 0) {
                int index = 0;
                toStringMethodBlk.invoke(toStringSBVar, "append").arg(
                        JExpr.lit("list ["));
                for (String key : lists.keySet()) {
                    hashCode += key.hashCode();
                    RestObject list = lists.get(key);
                    this.processList(list, dc, key, hashCode, index,
                            toStringMethodBlk, toStringSBVar);

                }
                toStringMethodBlk.invoke(toStringSBVar, "append").arg(
                        JExpr.lit("]"));
            }
            dc.field(JMod.PRIVATE + JMod.STATIC + JMod.FINAL, cm.LONG,
                    "serialVersionUID", JExpr.lit(hashCode));
            toStringMethodBlk.invoke(toStringSBVar, "append").arg(
                    JExpr.lit("]"));
            toStringMethodBlk._return(toStringSBVar.invoke("toString"));

            return dc;
        } catch (JClassAlreadyExistsException e) {
            throw new CodeGException("CG502", body.get_class()
                    + " create failed:" + e.getMessage());
        }
    }

    private void processField(int index, RestField field,
            JDefinedClass dc, long hashCode, JBlock toStringMethodBlk,
            JVar toStringSBVar) {

        String fieldName = field.getName();
        String dataType = field.getDatatype();
        hashCode += fieldName.hashCode();
        JType jType = null;
        if (dataType.endsWith(":List")) {
            String primitiveType = dataType.split(":")[0];
            jType = cm.ref("java.util.List").narrow(cm.ref(primitiveType));
        } else {
            jType = cm.ref(dataType);
        }
        JFieldVar fieldVar = dc.field(JMod.PRIVATE, jType, fieldName);
        JDocComment jdoc = fieldVar.javadoc();
        // 成员变量注释
        jdoc.add(field.getRemark());
        JFieldRef nameRef = JExpr.refthis(fieldName);
        if (index > 0) {
            toStringMethodBlk.invoke(toStringSBVar, "append").arg(
                    JExpr.lit(","));
        }

        toStringMethodBlk.invoke(toStringSBVar, "append").arg(
                JExpr.lit(fieldName));
        toStringMethodBlk.invoke(toStringSBVar, "append").arg(JExpr.lit("="));
        toStringMethodBlk.invoke(toStringSBVar, "append").arg(nameRef);

        // javabean命名规范：属性第二字母大写，则setter和getter方法首字母和第二字母都大写

        String tterMethodName = fieldName;
        if (fieldName.length() > 1) {
            String char2 = String.valueOf(fieldName.charAt(1));
            if (!char2.equals(char2.toUpperCase())) {
                tterMethodName = StringHelper.toUpperCaseFirstOne(fieldName);
            }
        }

        JMethod getMethod = dc.method(JMod.PUBLIC, jType, "get"
                + tterMethodName);
        hashCode += getMethod.name().hashCode();
        JBlock getMethodBlk = getMethod.body();
        getMethodBlk._return(nameRef);
        JMethod setMethod = dc.method(JMod.PUBLIC, cm.VOID, "set"
                + tterMethodName);
        JVar jvar = setMethod.param(jType, fieldName);
        hashCode += setMethod.name().hashCode();
        JBlock setMethodBlk = setMethod.body();

        // 对于String的属性在返回的报文中，默认为空字符串
        if (dc.getPackage().name().endsWith("res")
                && jvar.type().name().equals("String")) {
            JConditional ifBlock = setMethodBlk._if(jvar.eq(JExpr._null()));
            ifBlock._then().assign(nameRef, JExpr.lit(""));
            ifBlock._else().assign(nameRef, jvar);

        } else {
            setMethodBlk.assign(nameRef, jvar);
        }
    }

    private void processRO(String key, RestObject object, long hashCode, JDefinedClass dc, int index,
            JBlock toStringMethodBlk, JVar toStringSBVar) {
        JClass objectDc = this.processBody(object, null);
        hashCode += objectDc.hashCode();
        JFieldVar fieldVar = dc.field(JMod.PRIVATE, objectDc, key);
        JFieldRef nameRef = JExpr.refthis(key);
        // javabean命名规范：属性第二字母大写，则setter和getter方法首字母和第二字母都大写

        String tterMethodName = key;
        if (key.length() > 1) {
            String char2 = String.valueOf(key.charAt(1));
            if (!char2.equals(char2.toUpperCase())) {
                tterMethodName = StringHelper.toUpperCaseFirstOne(key);
            }
        }

        JMethod getMethod = dc.method(JMod.PUBLIC, objectDc, "get"
                + tterMethodName);
        hashCode += getMethod.name().hashCode();
        JBlock getMethodBlk = getMethod.body();
        getMethodBlk._return(nameRef);
        JMethod setMethod = dc.method(JMod.PUBLIC, cm.VOID, "set"
                + tterMethodName);
        JVar jvar = setMethod.param(objectDc, key);
        JBlock setMethodBlk = setMethod.body();
        hashCode += setMethod.name().hashCode();
        setMethodBlk.assign(nameRef, jvar);
        if (index > 0) {
            toStringMethodBlk.invoke(toStringSBVar, "append").arg(
                    JExpr.lit(","));
        }
        index++;
        toStringMethodBlk.invoke(toStringSBVar, "append").arg(JExpr.lit(key));
        toStringMethodBlk.invoke(toStringSBVar, "append").arg(JExpr.lit("="));
        toStringMethodBlk.invoke(toStringSBVar, "append").arg(nameRef);
    }

    private void processList(RestObject list,
            JDefinedClass dc, String key, long hashCode, int index,
            JBlock toStringMethodBlk, JVar toStringSBVar) {
        JClass listDc = this.processBody(list, null);
        JFieldVar fieldVar = dc.field(JMod.PRIVATE, cm.ref("java.util.List")
                .narrow(listDc), key);
        JFieldRef nameRef = JExpr.refthis(key);

        // javabean命名规范：属性第二字母大写，则setter和getter方法首字母和第二字母都大写

        String tterMethodName = key;
        if (key.length() > 1) {
            String char2 = String.valueOf(key.charAt(1));
            if (!char2.equals(char2.toUpperCase())) {
                tterMethodName = StringHelper.toUpperCaseFirstOne(key);
            }
        }

        JMethod getMethod = dc.method(JMod.PUBLIC, cm.ref("java.util.List")
                .narrow(listDc), "get" + tterMethodName);
        hashCode += getMethod.name().hashCode();
        JBlock getMethodBlk = getMethod.body();
        getMethodBlk._return(nameRef);
        JMethod setMethod = dc.method(JMod.PUBLIC, cm.VOID, "set"
                + tterMethodName);
        JVar jvar = setMethod.param(cm.ref("java.util.List").narrow(listDc),
                key);
        hashCode += setMethod.name().hashCode();
        JBlock setMethodBlk = setMethod.body();
        setMethodBlk.assign(nameRef, jvar);
        if (index > 0) {
            toStringMethodBlk.invoke(toStringSBVar, "append").arg(
                    JExpr.lit(","));
        }
        index++;
        toStringMethodBlk.invoke(toStringSBVar, "append").arg(JExpr.lit(key));
        toStringMethodBlk.invoke(toStringSBVar, "append").arg(JExpr.lit("="));
        toStringMethodBlk.invoke(toStringSBVar, "append").arg(nameRef);
    }
}
