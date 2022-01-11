/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.codeg.generator.method;

import com.fastjrun.codeg.common.CodeGException;
import com.fastjrun.codeg.common.CommonMethod;
import com.fastjrun.codeg.common.PacketField;
import com.fastjrun.codeg.common.PacketObject;
import com.fastjrun.codeg.generator.ServiceGenerator;
import com.fastjrun.codeg.helper.StringHelper;
import com.helger.jcodemodel.*;

import java.util.Map;

public class ServiceMethodGenerator extends AbstractMethodGenerator {

    protected String mockHelperClassName;

    protected CommonMethod commonMethod;

    protected String methodName;

    protected JMethod jServiceMethod;

    protected JMethod jServiceMockMethod;

    protected AbstractJType requestBodyClass;

    protected AbstractJType responseBodyClass;

    protected AbstractJClass elementClass;

    protected ServiceGenerator serviceGenerator;

    protected JFieldVar fieldVar;

    public String getMockHelperClassName() {
        return mockHelperClassName;
    }

    public void setMockHelperClassName(String mockHelperClassName) {
        this.mockHelperClassName = mockHelperClassName;
    }

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
            if (this.commonMethod.isRequestIsArray()) {
                this.requestBodyClass = requestBodyClass.array();
            }
            if (this.commonMethod.isRequestIsList()) {
                this.requestBodyClass = cm.ref("java.util.List").narrow(requestBodyClass);
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
            } else if (this.commonMethod.isResponseIsPage()) {
                this.responseBodyClass = cm.ref("com.fastjrun.example.dto.PageResult").narrow(elementClass);
            } else {
                this.responseBodyClass = elementClass;
            }
        }
    }

    public void processServiceMethod() {
        if(!this.commonMethod.isNeedApi()&&this.isClient()){
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
            this.jServiceMethod.param(this.requestBodyClass, StringHelper.toLowerCaseFirstOne(this.requestBodyClass.name()));
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
            this.jServiceMockMethod.param(this.requestBodyClass, StringHelper.toLowerCaseFirstOne(this.requestBodyClass.name()));
        }
        this.jServiceMockMethod.annotate(cm.ref("java.lang.Override"));
        JBlock serviceMockMethodBlock = this.jServiceMockMethod.body();
        if (this.responseBodyClass != cm.VOID) {
            if (commonMethod.isResponseIsArray()) {
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
                                    this.responseBodyClass, "response", JExpr._new(cm.ref("java.util.ArrayList")));
                    JVar responseBodyVar =
                            this.composeResponseBody(
                                    0, serviceMockMethodBlock, commonMethod.getResponse(), this.elementClass);
                    serviceMockMethodBlock.add(responseVar.invoke("add").arg(responseBodyVar));
                    serviceMockMethodBlock._return(responseVar);
                }
            } else if (commonMethod.isResponseIsPage()) {
                if (elementClass.name().endsWith("Boolean")) {
                    serviceMockMethodBlock._return(
                            cm.ref(mockHelperClassName).staticInvoke("geBooleanPage").arg(JExpr.lit(10)));
                } else if (elementClass.name().endsWith("Integer")) {
                    serviceMockMethodBlock._return(
                            cm.ref(mockHelperClassName).staticInvoke("geIntegerPage").arg(JExpr.lit(10)));
                } else if (elementClass.name().endsWith("Long")) {
                    serviceMockMethodBlock._return(
                            cm.ref(mockHelperClassName).staticInvoke("geLongPage").arg(JExpr.lit(10)));
                } else if (elementClass.name().endsWith("Double")) {
                    serviceMockMethodBlock._return(
                            cm.ref(mockHelperClassName).staticInvoke("geDoublePage").arg(JExpr.lit(10)));
                } else if (elementClass.name().endsWith("String")) {
                    serviceMockMethodBlock._return(
                            cm.ref(mockHelperClassName).staticInvoke("geStringPageWithAscii").arg(JExpr.lit(10)));
                } else if (elementClass.name().endsWith("Date")) {
                    serviceMockMethodBlock._return(
                            cm.ref(mockHelperClassName).staticInvoke("geDatePage").arg(JExpr.lit(10)));
                } else {
                    JVar responseVar =
                            serviceMockMethodBlock.decl(
                                    this.responseBodyClass,
                                    "response",
                                    JExpr._new(cm.ref("com.fastjrun.example.dto.PageResult")));

                    JVar listVar =
                            serviceMockMethodBlock.decl(
                                    cm.ref("java.util.ArrayList").narrow(this.elementClass),
                                    "list",
                                    JExpr._new(cm.ref("java.util.ArrayList")));
                    JVar responseBodyVar =
                            this.composeResponseBody(
                                    0, serviceMockMethodBlock, commonMethod.getResponse(), this.elementClass);
                    serviceMockMethodBlock.add(listVar.invoke("add").arg(responseBodyVar));
                    serviceMockMethodBlock.add(responseVar.invoke("setRows").arg(listVar));
                    serviceMockMethodBlock.add(
                            responseVar
                                    .invoke("setCurrPage")
                                    .arg(cm.ref(mockHelperClassName).staticInvoke("geInteger").arg(JExpr.lit(10))));
                    serviceMockMethodBlock.add(
                            responseVar
                                    .invoke("setPageSize")
                                    .arg(cm.ref(mockHelperClassName).staticInvoke("geInteger").arg(JExpr.lit(10))));
                    serviceMockMethodBlock.add(
                            responseVar
                                    .invoke("setTotalPage")
                                    .arg(cm.ref(mockHelperClassName).staticInvoke("geInteger").arg(JExpr.lit(10))));
                    serviceMockMethodBlock.add(
                            responseVar
                                    .invoke("setTotal")
                                    .arg(cm.ref(mockHelperClassName).staticInvoke("geInteger").arg(JExpr.lit(10))));
                    serviceMockMethodBlock._return(responseVar);
                }
            } else {
                if (responseBodyClass.name().endsWith("Boolean")) {
                    serviceMockMethodBlock._return(cm.ref(mockHelperClassName).staticInvoke("geBoolean"));
                } else if (responseBodyClass.name().endsWith("Integer")) {
                    serviceMockMethodBlock._return(
                            cm.ref(mockHelperClassName).staticInvoke("geInteger").arg(JExpr.lit(10)));
                } else if (responseBodyClass.name().endsWith("Long")) {
                    serviceMockMethodBlock._return(
                            cm.ref(mockHelperClassName).staticInvoke("geLong").arg(JExpr.lit(10)));
                } else if (responseBodyClass.name().endsWith("Double")) {
                    serviceMockMethodBlock._return(
                            cm.ref(mockHelperClassName).staticInvoke("geDouble").arg(JExpr.lit(10)));
                } else if (responseBodyClass.name().endsWith("String")) {
                    serviceMockMethodBlock._return(
                            cm.ref(mockHelperClassName)
                                    .staticInvoke("geStringWithAlphabetic")
                                    .arg(JExpr.lit(10)));
                } else if (responseBodyClass.name().endsWith("Date")) {
                    serviceMockMethodBlock._return(cm.ref(mockHelperClassName).staticInvoke("geDate"));
                } else {
                    JVar responseBodyVar =
                            this.composeResponseBody(
                                    0, serviceMockMethodBlock, commonMethod.getResponse(), this.elementClass);
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
                                    .arg((cm.ref(mockHelperClassName).staticInvoke("geDate"))));
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
                if (this.getMockModel() != MockModel.MockModel_Common) {
                    this.processServiceMockMethod();
                }
            }
        }
    }
}
