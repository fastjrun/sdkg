package com.fastjrun.codeg.bundle;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import com.fastjrun.codeg.CodeGException;
import com.fastjrun.codeg.CodeGenerator;
import com.fastjrun.codeg.bundle.common.PacketField;
import com.fastjrun.codeg.bundle.common.PacketObject;
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
import com.sun.codemodel.JForLoop;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;

public abstract class PacketGenerator extends CodeGenerator {

    protected String[] bundleFiles;
    protected boolean mock = false;
    Map<String, PacketObject> packetMap;
    Map<String, JClass> poClassMap;

    public boolean isMock() {
        return mock;
    }

    public void setMock(boolean mock) {
        this.mock = mock;
    }

    public String[] getBundleFiles() {
        return bundleFiles;
    }

    public void setBundleFiles(String[] bundleFiles) {
        this.bundleFiles = bundleFiles;
    }

    protected JClass processBody(PacketObject body, JClass parentClass, boolean isSwagger) {
        try {

            JDefinedClass dc = cm._class(this.packageNamePrefix + body.get_class());

            if (parentClass != null) {
                dc._extends(parentClass);
            } else {
                if (body.getResponseClass() == null) {
                    dc._extends(cm.ref("com.fastjrun.packet.BaseResponseBody"));
                } else {
                    String responseBodyClassName = "com.fastjrun.packet.EmptyResponseBody";
                    if (!body.getResponseClass().equals(responseBodyClassName)) {
                        responseBodyClassName = this.packageNamePrefix + body.getResponseClass();
                    }
                    dc._extends(cm.ref("com.fastjrun.packet.BaseRequestBody").narrow(cm.ref(responseBodyClassName)));
                    JMethod jGetResponseBodyClassmethod = dc.method(JMod.PUBLIC, cm.ref("Class").narrow(cm.ref
                                    (responseBodyClassName)),
                            "getResponseBodyClass");
                    jGetResponseBodyClassmethod.annotate(cm.ref("Override"));
                    jGetResponseBodyClassmethod.body()._return(cm.ref(responseBodyClassName).dotclass());

                }

            }
            dc._implements(cm.ref("java.io.Serializable"));
            Long hashCode = 0L;
            hashCode += dc.getClass().getName().hashCode();
            this.addClassDeclaration(dc);

            Map<String, PacketField> fields = body.getFields();
            JMethod toStringMethod = dc.method(JMod.PUBLIC, cm.ref("String"), "toString");
            toStringMethod.annotate(cm.ref("Override"));
            JBlock toStringMethodBlk = toStringMethod.body();
            JVar toStringSBVar = toStringMethodBlk.decl(cm.ref("StringBuilder"), "sb",
                    JExpr._new(cm.ref("StringBuilder")));
            toStringMethodBlk.invoke(toStringSBVar, "append").arg(JExpr.lit(dc.name()).plus(JExpr.lit(" [")));
            if (fields != null && fields.size() > 0) {
                int index = 0;
                toStringMethodBlk.invoke(toStringSBVar, "append").arg(JExpr.lit("field ["));
                for (PacketField field : fields.values()) {
                    this.processField(index, field, isSwagger, dc, hashCode, toStringMethodBlk, toStringSBVar);
                    index++;
                }
                toStringMethodBlk.invoke(toStringSBVar, "append").arg(JExpr.lit("]"));
            }

            Map<String, PacketObject> objects = body.getObjects();
            if (objects != null && objects.size() > 0) {
                int index = 0;
                toStringMethodBlk.invoke(toStringSBVar, "append").arg(JExpr.lit("object ["));
                for (String key : objects.keySet()) {
                    hashCode += key.hashCode();
                    PacketObject object = objects.get(key);
                    JClass jObjectClass =
                            this.processRO(key, object, true, hashCode, isSwagger, dc, index, toStringMethodBlk,
                                    toStringSBVar);
                    this.poClassMap.put(object.get_class(), jObjectClass);

                }
                toStringMethodBlk.invoke(toStringSBVar, "append").arg(JExpr.lit("]"));
            }

            Map<String, PacketObject> lists = body.getLists();
            if (lists != null && lists.size() > 0) {
                int index = 0;
                toStringMethodBlk.invoke(toStringSBVar, "append").arg(JExpr.lit("list ["));
                for (String key : lists.keySet()) {
                    hashCode += key.hashCode();
                    PacketObject list = lists.get(key);
                    JClass jListObject =
                            this.processRO(key, list, false, hashCode, isSwagger, dc, index, toStringMethodBlk,
                                    toStringSBVar);
                    this.poClassMap.put(list.get_class(), jListObject);
                    index++;

                }
                toStringMethodBlk.invoke(toStringSBVar, "append").arg(JExpr.lit("]"));
            }
            dc.field(JMod.PRIVATE + JMod.STATIC + JMod.FINAL, cm.LONG, "serialVersionUID", JExpr.lit(hashCode));
            toStringMethodBlk.invoke(toStringSBVar, "append").arg(JExpr.lit("]"));
            toStringMethodBlk._return(toStringSBVar.invoke("toString"));

            return dc;
        } catch (JClassAlreadyExistsException e) {
            throw new CodeGException("CG502", body.get_class() + " create failed:" + e.getExistingClass().binaryName());
        }
    }

    private void processField(int index, PacketField field, boolean isSwagger, JDefinedClass dc, Long hashCode,
                              JBlock toStringMethodBlk, JVar toStringSBVar) {

        String fieldName = field.getName();
        String dataType = field.getDatatype();
        hashCode += fieldName.hashCode();
        JType jType;
        if (dataType.endsWith(":List")) {
            String primitiveType = dataType.split(":")[0];
            jType = cm.ref("java.util.List").narrow(cm.ref(primitiveType));
        } else {
            jType = cm.ref(dataType);
        }
        JFieldVar fieldVar = dc.field(JMod.PRIVATE, jType, fieldName);
        if (isSwagger) {
            fieldVar.annotate(cm.ref("io.swagger.annotations.ApiModelProperty")).param("value", field.getRemark())
                    .param("required", JExpr.lit(!field.isCanBeNull()));
        }
        JDocComment jdoc = fieldVar.javadoc();
        // 成员变量注释
        jdoc.add(field.getRemark());
        JFieldRef nameRef = JExpr.refthis(fieldName);
        if (index > 0) {
            toStringMethodBlk.invoke(toStringSBVar, "append").arg(JExpr.lit(","));
        }

        toStringMethodBlk.invoke(toStringSBVar, "append").arg(JExpr.lit(fieldName));
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

    private JClass processRO(String key, PacketObject po, boolean isObject, Long hashCode, boolean isSwagger,
                             JDefinedClass dc, int index, JBlock toStringMethodBlk, JVar toStringSBVar) {
        JClass poDc = this.processBody(po, cm.ref("com.fastjrun.packet.BaseBody"), isSwagger);
        hashCode += poDc.hashCode();

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

        if (isObject) {
            dc.field(JMod.PRIVATE, poDc, key);
            setMethod = dc.method(JMod.PUBLIC, cm.VOID, "set" + tterMethodName);
            getMethod = dc.method(JMod.PUBLIC, poDc, "get" + tterMethodName);
            jvar = setMethod.param(poDc, key);
        } else {
            dc.field(JMod.PRIVATE, cm.ref("java.util.List").narrow(poDc), key);
            setMethod = dc.method(JMod.PUBLIC, cm.VOID, "set" + tterMethodName);
            getMethod = dc.method(JMod.PUBLIC, cm.ref("java.util.List").narrow(poDc), "get" + tterMethodName);
            jvar = setMethod.param(cm.ref("java.util.List").narrow(poDc), key);
        }
        JBlock setMethodBlk = setMethod.body();
        hashCode += setMethod.name().hashCode();
        setMethodBlk.assign(nameRef, jvar);

        hashCode += getMethod.name().hashCode();
        JBlock getMethodBlk = getMethod.body();
        getMethodBlk._return(nameRef);

        if (index > 0) {
            toStringMethodBlk.invoke(toStringSBVar, "append").arg(JExpr.lit(","));
        }
        toStringMethodBlk.invoke(toStringSBVar, "append").arg(JExpr.lit(key));
        toStringMethodBlk.invoke(toStringSBVar, "append").arg(JExpr.lit("="));
        JConditional jConditional = toStringMethodBlk._if(nameRef.ne(JExpr._null()));
        jConditional._else().invoke(toStringSBVar, "append").arg(JExpr.lit("null"));
        JBlock jThenBlock = jConditional._then();
        if (isObject) {
            jThenBlock.invoke(toStringSBVar, "append").arg(nameRef);
        } else {

            JForLoop forLoop = jThenBlock._for();
            JVar iVar = forLoop.init(cm.INT, "i", JExpr.lit(0));
            forLoop.test(iVar.lt(nameRef.invoke("size")));
            forLoop.update(iVar.incr());
            JBlock forBody = forLoop.body();
            String poDcName = StringHelper.toLowerCaseFirstOne(poDc.name());
            JVar poDcObjectVar = forBody.decl(poDc, poDcName, nameRef.invoke("get").arg(iVar));
            forBody._if(iVar.eq(JExpr.lit(0)))._then().invoke(toStringSBVar, "append").arg(JExpr.lit("["));
            forBody._if(iVar.gt(JExpr.lit(0)))._then().invoke(toStringSBVar, "append").arg(JExpr.lit(","));
            forBody.invoke(toStringSBVar, "append").arg(JExpr.lit("list."));
            forBody.invoke(toStringSBVar, "append").arg(iVar);
            forBody.invoke(toStringSBVar, "append").arg(JExpr.lit("="));
            forBody.invoke(toStringSBVar, "append").arg(poDcObjectVar);
            jThenBlock.invoke(toStringSBVar, "append").arg(JExpr.lit("]"));
        }
        return poDc;
    }

    protected void generatePO() {
        poClassMap = new HashMap<>();
        for (String key : this.packetMap.keySet()) {
            PacketObject restPacket = this.packetMap.get(key);
            Callable<JClass> task = new GeneratorPacketTask(this, restPacket);
            FutureTask<JClass> future = new FutureTask<>(task);
            new Thread(future).start();
            try {
                poClassMap.put(key, future.get());
            } catch (InterruptedException | ExecutionException e) {
                log.error(key + " packat class add to map error:" + e.getMessage());
            }
        }

        this.waitForCodeGFinished(poClassMap);

    }

    protected boolean waitForCodeGFinished(Map<String, JClass> classMap) {
        // 遍历任务的结果
        boolean isFinished = false;
        while (!isFinished) {
            isFinished = true;
            for (JClass fs : classMap.values()) {
                //log.debug(fs);
                if (fs == null) {
                    isFinished = false;
                    break;
                }
            }
        }

        return true;
    }

    class GeneratorPacketTask implements Callable<JClass> {

        private PacketGenerator codeGenerator;

        private PacketObject body;

        public GeneratorPacketTask(PacketGenerator codeGenerator, PacketObject body) {
            this.codeGenerator = codeGenerator;
            this.body = body;
        }

        public JClass call() {
            return codeGenerator.processBody(body, null, codeGenerator.isMock());
        }

    }

}