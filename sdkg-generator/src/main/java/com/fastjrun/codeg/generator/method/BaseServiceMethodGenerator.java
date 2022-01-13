/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.codeg.generator.method;

import com.fastjrun.codeg.common.*;
import com.fastjrun.codeg.generator.BaseServiceGenerator;
import com.fastjrun.codeg.helper.StringHelper;
import com.helger.jcodemodel.*;
import org.apache.commons.lang.StringUtils;

import java.util.Map;

public abstract class BaseServiceMethodGenerator extends AbstractMethodGenerator {

    protected CommonMethod commonMethod;

    protected String methodName;

    protected JMethod jServiceMethod;

    protected AbstractJType requestBodyClass;

    protected AbstractJType responseBodyClass;

    protected AbstractJClass elementClass;

    protected BaseServiceGenerator serviceGenerator;

    protected JFieldVar fieldVar;

    protected JMethod jServiceMockMethod;

    public JFieldVar getFieldVar() {
        return fieldVar;
    }

    public void setFieldVar(JFieldVar fieldVar) {
        this.fieldVar = fieldVar;
    }

    public BaseServiceGenerator getServiceGenerator() {
        return serviceGenerator;
    }

    public void setServiceGenerator(BaseServiceGenerator serviceGenerator) {
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

    public CommonMethod getCommonMethod() {
        return commonMethod;
    }

    public void setCommonMethod(CommonMethod commonMethod) {
        this.commonMethod = commonMethod;
    }

    public abstract void doParse();

    public void processServiceMethod() {
        if (!this.commonMethod.isNeedApi() && this.isClient()) {
            return;
        }
        this.jServiceMethod =
                this.serviceGenerator
                        .getServiceClass()
                        .method(JMod.NONE, this.responseBodyClass, this.methodName);
        String methodRemark = commonMethod.getRemark();
        this.jServiceMethod.javadoc().append(methodRemark);

        MethodGeneratorHelper.processServiceMethodVariables(
                this.jServiceMethod, this.commonMethod.getHeadVariables(), this.cm, this.packageNamePrefix);
        MethodGeneratorHelper.processServiceMethodVariables(
                this.jServiceMethod, this.commonMethod.getPathVariables(), this.cm, this.packageNamePrefix);
        MethodGeneratorHelper.processServiceMethodVariables(
                this.jServiceMethod, this.commonMethod.getParameters(), this.cm, this.packageNamePrefix);
        MethodGeneratorHelper.processServiceMethodVariables(
                this.jServiceMethod,
                this.commonMethod.getCookieVariables(),
                this.cm,
                this.packageNamePrefix);
        MethodGeneratorHelper.processServiceMethodVariables(
                this.jServiceMethod, this.commonMethod.getWebParameters(), this.cm, this.packageNamePrefix);
        if (this.requestBodyClass != null) {

            String varName=this.commonMethod.getRequestName();
            if(StringUtils.isBlank(varName)){
                if(this.requestBodyClass.isArray()){
                    varName = StringHelper.toLowerCaseFirstOne(this.requestBodyClass.elementType().name())+"s";
                }else{
                    varName = StringHelper.toLowerCaseFirstOne(this.requestBodyClass.name());
                }
            }

            this.jServiceMethod.param(this.requestBodyClass, varName);
        }
    }

    public void processServiceMockMethod() {
        this.jServiceMockMethod =
                this.serviceGenerator.getServiceMockClass()
                        .method(JMod.PUBLIC, this.responseBodyClass, this.methodName);
        String methodRemark = this.commonMethod.getRemark();
        this.jServiceMockMethod.javadoc().append(methodRemark);

        MethodGeneratorHelper.processServiceMethodVariables(
                this.jServiceMockMethod,
                this.commonMethod.getHeadVariables(),
                this.cm,
                this.packageNamePrefix);
        MethodGeneratorHelper.processServiceMethodVariables(
                this.jServiceMockMethod,
                this.commonMethod.getPathVariables(),
                this.cm,
                this.packageNamePrefix);
        MethodGeneratorHelper.processServiceMethodVariables(
                this.jServiceMockMethod,
                this.commonMethod.getParameters(),
                this.cm,
                this.packageNamePrefix);
        MethodGeneratorHelper.processServiceMethodVariables(
                this.jServiceMockMethod,
                this.commonMethod.getCookieVariables(),
                this.cm,
                this.packageNamePrefix);

        MethodGeneratorHelper.processServiceMethodVariables(
                this.jServiceMockMethod,
                this.commonMethod.getWebParameters(),
                this.cm,
                this.packageNamePrefix);

        if (this.requestBodyClass != null) {
            String varName=this.commonMethod.getRequestName();
            if(StringUtils.isBlank(varName)){
                if(this.requestBodyClass.isArray()){
                    varName = StringHelper.toLowerCaseFirstOne(this.requestBodyClass.elementType().name())+"s";
                }else{
                    varName = StringHelper.toLowerCaseFirstOne(this.requestBodyClass.name());
                }
            }
            this.jServiceMockMethod.param(this.requestBodyClass, varName);
        }
        this.jServiceMockMethod.annotate(cm.ref("java.lang.Override"));
        JBlock serviceMockMethodBlock = this.jServiceMockMethod.body();
        if (this.responseBodyClass != cm.VOID) {
            if (this.commonMethod.isResponseIsArray()) {
                if (this.elementClass.name().endsWith("Boolean")) {
                    serviceMockMethodBlock._return(cm.ref(this.serviceGenerator.getMockHelperName()).staticInvoke("geBooleanList").arg(JExpr.lit(10)));
                } else if (this.elementClass.name().endsWith("Integer")) {
                    serviceMockMethodBlock._return(
                            cm.ref(this.serviceGenerator.getMockHelperName()).staticInvoke("geIntegerList").arg(JExpr.lit(10)));
                } else if (this.elementClass.name().endsWith("Long")) {
                    serviceMockMethodBlock._return(
                            cm.ref(this.serviceGenerator.getMockHelperName()).staticInvoke("geLongList").arg(JExpr.lit(10)));
                } else if (this.elementClass.name().endsWith("Double")) {
                    serviceMockMethodBlock._return(
                            cm.ref(this.serviceGenerator.getMockHelperName()).staticInvoke("geDoubleList").arg(JExpr.lit(10)));
                } else if (this.elementClass.name().endsWith("String")) {
                    serviceMockMethodBlock._return(
                            cm.ref(this.serviceGenerator.getMockHelperName()).staticInvoke("geStringListWithAscii").arg(JExpr.lit(10)));
                } else if (this.elementClass.name().endsWith("Date")) {
                    serviceMockMethodBlock._return(
                            cm.ref(this.serviceGenerator.getMockHelperName()).staticInvoke("geDateList").arg(JExpr.lit(10)));
                } else {
                    JVar responseVar =
                            serviceMockMethodBlock.decl(
                                    this.responseBodyClass, "response", JExpr._new(cm.ref("java.util.ArrayList")));
                    JVar responseBodyVar =
                            this.composeResponseBody(
                                    0, serviceMockMethodBlock, this.commonMethod.getResponse(), this.elementClass);
                    serviceMockMethodBlock.add(responseVar.invoke("add").arg(responseBodyVar));
                    serviceMockMethodBlock._return(responseVar);
                }
            } else if (this.commonMethod.isResponseIsPage()) {
                if (this.elementClass.name().endsWith("Boolean")) {
                    serviceMockMethodBlock._return(
                            cm.ref(this.serviceGenerator.getMockHelperName()).staticInvoke("geBooleanPage").arg(JExpr.lit(10)));
                } else if (this.elementClass.name().endsWith("Integer")) {
                    serviceMockMethodBlock._return(
                            cm.ref(this.serviceGenerator.getMockHelperName()).staticInvoke("geIntegerPage").arg(JExpr.lit(10)));
                } else if (this.elementClass.name().endsWith("Long")) {
                    serviceMockMethodBlock._return(
                            cm.ref(this.serviceGenerator.getMockHelperName()).staticInvoke("geLongPage").arg(JExpr.lit(10)));
                } else if (this.elementClass.name().endsWith("Double")) {
                    serviceMockMethodBlock._return(
                            cm.ref(this.serviceGenerator.getMockHelperName()).staticInvoke("geDoublePage").arg(JExpr.lit(10)));
                } else if (this.elementClass.name().endsWith("String")) {
                    serviceMockMethodBlock._return(
                            cm.ref(this.serviceGenerator.getMockHelperName()).staticInvoke("geStringPageWithAscii").arg(JExpr.lit(10)));
                } else if (this.elementClass.name().endsWith("Date")) {
                    serviceMockMethodBlock._return(
                            cm.ref(this.serviceGenerator.getMockHelperName()).staticInvoke("geDatePage").arg(JExpr.lit(10)));
                } else {
                    JVar responseVar =
                            serviceMockMethodBlock.decl(
                                    this.responseBodyClass,
                                    "response",
                                    JExpr._new(cm.ref(this.serviceGenerator.getPageResultName())));

                    JVar listVar =
                            serviceMockMethodBlock.decl(
                                    cm.ref("java.util.ArrayList").narrow(this.elementClass),
                                    "list",
                                    JExpr._new(cm.ref("java.util.ArrayList")));
                    JVar responseBodyVar =
                            this.composeResponseBody(
                                    0, serviceMockMethodBlock, this.commonMethod.getResponse(), this.elementClass);
                    serviceMockMethodBlock.add(listVar.invoke("add").arg(responseBodyVar));
                    serviceMockMethodBlock.assign(responseVar, cm.ref(this.serviceGenerator.getMockHelperName()).staticInvoke("geObject").arg(listVar));
                    serviceMockMethodBlock._return(responseVar);
                }
            } else {
                if (this.responseBodyClass.name().endsWith("Boolean")) {
                    serviceMockMethodBlock._return(cm.ref(this.serviceGenerator.getMockHelperName()).staticInvoke("geBoolean"));
                } else if (this.responseBodyClass.name().endsWith("Integer")) {
                    serviceMockMethodBlock._return(
                            cm.ref(this.serviceGenerator.getMockHelperName()).staticInvoke("geInteger").arg(JExpr.lit(10)));
                } else if (this.responseBodyClass.name().endsWith("Long")) {
                    serviceMockMethodBlock._return(
                            cm.ref(this.serviceGenerator.getMockHelperName()).staticInvoke("geLong").arg(JExpr.lit(10)));
                } else if (this.responseBodyClass.name().endsWith("Double")) {
                    serviceMockMethodBlock._return(
                            cm.ref(this.serviceGenerator.getMockHelperName()).staticInvoke("geDouble").arg(JExpr.lit(10)));
                } else if (this.responseBodyClass.name().endsWith("String")) {
                    serviceMockMethodBlock._return(
                            cm.ref(this.serviceGenerator.getMockHelperName())
                                    .staticInvoke("geStringWithAlphabetic")
                                    .arg(JExpr.lit(10)));
                } else if (this.responseBodyClass.name().endsWith("Date")) {
                    serviceMockMethodBlock._return(cm.ref(this.serviceGenerator.getMockHelperName()).staticInvoke("geDate"));
                } else {
                    JVar responseBodyVar =
                            this.composeResponseBody(
                                    0, serviceMockMethodBlock, this.commonMethod.getResponse(), this.elementClass);
                    serviceMockMethodBlock._return(responseBodyVar);
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
                if (ro.getName().equals(responseBody.getName())) {
                    continue;
                }
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
                PacketObject ro = roList.get(listName);
                if (ro.getName().equals(responseBody.getName())) {
                    continue;
                }
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

                int index = 0;
                JVar iSizeVar =
                        methodBlk.decl(
                                cm.INT,
                                "iSize" + listName + loopSeq + index,
                                cm.ref(this.serviceGenerator.getMockHelperName())
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
                                                (cm.ref(this.serviceGenerator.getMockHelperName())
                                                        .staticInvoke("geStringListWithAscii")
                                                        .arg(JExpr.lit(10)))));
                    } else if (primitiveType.endsWith("Boolean")) {
                        methodBlk.add(
                                reponseBodyVar
                                        .invoke(tterMethodName)
                                        .arg(
                                                (cm.ref(this.serviceGenerator.getMockHelperName())
                                                        .staticInvoke("geBooleanList")
                                                        .arg(JExpr.lit(10)))));
                    } else if (primitiveType.endsWith("Integer")) {
                        methodBlk.add(
                                reponseBodyVar
                                        .invoke(tterMethodName)
                                        .arg(
                                                (cm.ref(this.serviceGenerator.getMockHelperName())
                                                        .staticInvoke("geIntegerList")
                                                        .arg(JExpr.lit(10)))));
                    } else if (primitiveType.endsWith("Long")) {
                        methodBlk.add(
                                reponseBodyVar
                                        .invoke(tterMethodName)
                                        .arg(
                                                (cm.ref(this.serviceGenerator.getMockHelperName())
                                                        .staticInvoke("geLongList")
                                                        .arg(JExpr.lit(10)))));
                    } else if (primitiveType.endsWith("Float")) {
                        methodBlk.add(
                                reponseBodyVar
                                        .invoke(tterMethodName)
                                        .arg(
                                                (cm.ref(this.serviceGenerator.getMockHelperName())
                                                        .staticInvoke("geFloatList")
                                                        .arg(JExpr.lit(10)))));
                    } else if (primitiveType.endsWith("Double")) {
                        methodBlk.add(
                                reponseBodyVar
                                        .invoke(tterMethodName)
                                        .arg(
                                                (cm.ref(this.serviceGenerator.getMockHelperName())
                                                        .staticInvoke("geDoubleList")
                                                        .arg(JExpr.lit(10)))));
                    } else if (primitiveType.endsWith("Date")) {
                        methodBlk.add(
                                reponseBodyVar
                                        .invoke(tterMethodName)
                                        .arg(
                                                (cm.ref(this.serviceGenerator.getMockHelperName())
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
                                            (cm.ref(this.serviceGenerator.getMockHelperName())
                                                    .staticInvoke("geStringWithAscii")
                                                    .arg(JExpr.lit(Integer.parseInt(length))))));
                } else if (jType.name().endsWith("Boolean")) {
                    methodBlk.add(
                            reponseBodyVar
                                    .invoke(tterMethodName)
                                    .arg((cm.ref(this.serviceGenerator.getMockHelperName()).staticInvoke("geBoolean"))));
                } else if (jType.name().endsWith("Integer")) {
                    methodBlk.add(
                            reponseBodyVar
                                    .invoke(tterMethodName)
                                    .arg(
                                            (cm.ref(this.serviceGenerator.getMockHelperName()).staticInvoke("geInteger").arg(JExpr.lit(100)))));
                } else if (jType.name().endsWith("Long")) {
                    methodBlk.add(
                            reponseBodyVar
                                    .invoke(tterMethodName)
                                    .arg((cm.ref(this.serviceGenerator.getMockHelperName()).staticInvoke("geLong").arg(JExpr.lit(100)))));
                } else if (jType.name().endsWith("Float")) {
                    methodBlk.add(
                            reponseBodyVar
                                    .invoke(tterMethodName)
                                    .arg((cm.ref(this.serviceGenerator.getMockHelperName()).staticInvoke("geFloat").arg(JExpr.lit(100)))));
                } else if (jType.name().endsWith("Double")) {
                    methodBlk.add(
                            reponseBodyVar
                                    .invoke(tterMethodName)
                                    .arg((cm.ref(this.serviceGenerator.getMockHelperName()).staticInvoke("geDouble").arg(JExpr.lit(100)))));
                } else if (jType.name().endsWith("Date")) {
                    methodBlk.add(
                            reponseBodyVar
                                    .invoke(tterMethodName)
                                    .arg((cm.ref(this.serviceGenerator.getMockHelperName()).staticInvoke("geDate"))));
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
            if (!this.isClient()) {
                if (mockModel != CodeGConstants.MockModel.MockModel_Common) {
                    this.processServiceMockMethod();
                }
            }
        }
    }
}
