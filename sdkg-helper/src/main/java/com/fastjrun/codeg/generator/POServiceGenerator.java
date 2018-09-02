
package com.fastjrun.codeg.generator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fastjrun.codeg.common.CodeGConstants;
import com.fastjrun.codeg.common.CodeGException;
import com.fastjrun.codeg.common.CodeGMsgContants;
import com.fastjrun.codeg.common.CodeModelConstants;
import com.fastjrun.codeg.common.CommonMethod;
import com.fastjrun.codeg.common.CommonService;
import com.fastjrun.codeg.common.PacketField;
import com.fastjrun.codeg.common.PacketObject;
import com.fastjrun.codeg.helper.StringHelper;
import com.sun.codemodel.ClassType;
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

public abstract class POServiceGenerator extends BaseGenerator implements CodeModelConstants {

    protected Map<String, PacketObject> packetMap;
    protected Map<String, JDefinedClass> packetClassMap;
    protected Map<String, CommonService> serviceMap;
    protected CodeGConstants.MockModel mockModel = MockModel.MockModel_Common;
    JClass mockHelperClass = cm.ref("com.fastjrun.helper.MockHelper");
    private boolean client = true;

    public Map<String, JDefinedClass> getPacketClassMap() {
        return packetClassMap;
    }

    public void setPacketClassMap(Map<String, JDefinedClass> packetClassMap) {
        this.packetClassMap = packetClassMap;
    }

    public boolean isClient() {
        return client;
    }

    public void setClient(boolean client) {
        this.client = client;
    }

    public Map<String, PacketObject> getPacketMap() {
        return packetMap;
    }

    public void setPacketMap(Map<String, PacketObject> packetMap) {
        this.packetMap = packetMap;
    }

    public Map<String, CommonService> getServiceMap() {
        return serviceMap;
    }

    public void setServiceMap(Map<String, CommonService> serviceMap) {
        this.serviceMap = serviceMap;
    }

    public MockModel getMockModel() {
        return mockModel;
    }

    public void setMockModel(MockModel mockModel) {
        this.mockModel = mockModel;
    }

    public JDefinedClass processPO(PacketObject po) {
        JDefinedClass dc;
        try {
            dc = cm._class(this.packageNamePrefix + po.get_class());
        } catch (JClassAlreadyExistsException e) {
            String msg = po.get_class() + " is already exists.";
            this.commonLog.getLog().error(msg, e);
            throw new CodeGException(CodeGMsgContants.CODEG_CLASS_EXISTS, msg, e);
        }
        String parent = "com.fastjrun.packet.BaseBody";
        dc._extends(cm.ref(parent));

        dc._implements(cm.ref("java.io.Serializable"));
        Long hashCode = 0L;
        hashCode += dc.getClass().getName().hashCode();

        Map<String, PacketField> fields = po.getFields();
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
                this.processField(index, field, dc, hashCode, toStringMethodBlk, toStringSBVar);
                index++;
            }
            toStringMethodBlk.invoke(toStringSBVar, "append").arg(JExpr.lit("]"));
        }

        Map<String, PacketObject> objects = po.getObjects();
        if (objects != null && objects.size() > 0) {
            int index = 0;
            toStringMethodBlk.invoke(toStringSBVar, "append").arg(JExpr.lit("object ["));
            for (String key : objects.keySet()) {
                hashCode += key.hashCode();
                PacketObject object = objects.get(key);
                object.setParent("com.fastjrun.packet.BaseBody");
                JDefinedClass jObjectClass =
                        this.processObjectORList(key, object, true, hashCode, dc, index, toStringMethodBlk,
                                toStringSBVar);
                index++;

            }
            toStringMethodBlk.invoke(toStringSBVar, "append").arg(JExpr.lit("]"));
        }

        Map<String, PacketObject> lists = po.getLists();
        if (lists != null && lists.size() > 0) {
            int index = 0;
            toStringMethodBlk.invoke(toStringSBVar, "append").arg(JExpr.lit("list ["));
            for (String key : lists.keySet()) {
                hashCode += key.hashCode();
                PacketObject list = lists.get(key);
                list.setParent("com.fastjrun.packet.BaseBody");
                JDefinedClass jListObject =
                        this.processObjectORList(key, list, false, hashCode, dc, index, toStringMethodBlk,
                                toStringSBVar);
                index++;

            }
            toStringMethodBlk.invoke(toStringSBVar, "append").arg(JExpr.lit("]"));
        }
        dc.field(JMod.PRIVATE + JMod.STATIC + JMod.FINAL, cm.LONG, "serialVersionUID", JExpr.lit(hashCode));
        toStringMethodBlk.invoke(toStringSBVar, "append").arg(JExpr.lit("]"));
        toStringMethodBlk._return(toStringSBVar.invoke("toString"));
        this.addClassDeclaration(dc);
        return dc;
    }

    public JDefinedClass processService(CommonService commonService) {
        JDefinedClass dcService;
        try {
            dcService = cm._class(this.packageNamePrefix + "service." + commonService.get_class(),
                    ClassType.INTERFACE);
        } catch (JClassAlreadyExistsException e) {
            String msg = commonService.get_class() + " is already exists.";
            this.commonLog.getLog().error(msg, e);
            throw new CodeGException(CodeGMsgContants.CODEG_CLASS_EXISTS, msg, e);
        }
        List<CommonMethod> methods = commonService.getMethods();
        for (CommonMethod method : methods) {
            PacketObject response;
            String methodName = method.getName();
            String methodRemark = method.getRemark();

            String methodVersion = method.getVersion();
            if (methodVersion != null && !methodVersion.equals("")) {
                methodName = methodName + methodVersion;
            }
            response = method.getResponse();

            JType responseBodyClass;
            if (response == null) {
                responseBodyClass = cm.VOID;

            } else {
                String responseClassP = response.get_class();
                if (method.isResponseIsArray()) {
                    responseBodyClass =
                            cm.ref("java.util.List").narrow(cm.ref(this.packageNamePrefix + responseClassP));
                } else {
                    responseBodyClass = cm.ref(this.packageNamePrefix + responseClassP);
                }
            }
            JMethod serviceMethod = dcService.method(JMod.NONE, responseBodyClass, methodName);
            serviceMethod.javadoc().append(methodRemark);

            List<PacketField> headVariables = method.getHeadVariables();
            if (headVariables != null && headVariables.size() > 0) {
                for (int index = 0; index < headVariables.size(); index++) {
                    PacketField headVariable = headVariables.get(index);
                    JType jType = cm.ref(headVariable.getDatatype());
                    serviceMethod.param(jType, headVariable.getName());
                }
            }
            List<PacketField> pathVariables = method.getPathVariables();
            if (pathVariables != null && pathVariables.size() > 0) {
                for (int index = 0; index < pathVariables.size(); index++) {
                    PacketField pathVariable = pathVariables.get(index);
                    JType jType = cm.ref(pathVariable.getDatatype());
                    serviceMethod.param(jType, pathVariable.getName());
                }
            }
            List<PacketField> parameters = method.getParameters();
            if (parameters != null && parameters.size() > 0) {
                for (int index = 0; index < parameters.size(); index++) {
                    PacketField parameter = parameters.get(index);
                    JClass jClass = cm.ref(parameter.getDatatype());
                    serviceMethod.param(jClass, parameter.getName());
                }
            }
            PacketObject request = method.getRequest();

            if (request != null) {
                JClass requestBodyClass = cm.ref(this.packageNamePrefix + request.get_class());
                String paramName = "request";
                serviceMethod.param(requestBodyClass, paramName);
            }
        }
        this.addClassDeclaration(dcService);
        return dcService;
    }

    public JDefinedClass processServiceMock(CommonService commonService, JDefinedClass serviceClass) {
        JDefinedClass dcServiceMock;
        try {
            dcServiceMock = cm._class("com.fastjrun.mock." + commonService.get_class() + "Mock");
            dcServiceMock._implements(serviceClass);
            dcServiceMock.annotate(cm.ref("org.springframework.stereotype.Service")).param("value",
                    commonService.getName());
        } catch (JClassAlreadyExistsException e) {
            String msg = commonService.get_class() + " is already exists.";
            this.commonLog.getLog().error(msg, e);
            throw new CodeGException(CodeGMsgContants.CODEG_CLASS_EXISTS, msg, e);
        }
        List<CommonMethod> methods = commonService.getMethods();
        for (CommonMethod method : methods) {
            PacketObject response;
            String methodName = method.getName();
            String methodRemark = method.getRemark();

            String methodVersion = method.getVersion();
            if (methodVersion != null && !methodVersion.equals("")) {
                methodName = methodName + methodVersion;
            }
            response = method.getResponse();

            JType responseBodyClass;
            if (response == null) {
                responseBodyClass = cm.VOID;

            } else {
                String responseClassP = response.get_class();
                if (method.isResponseIsArray()) {
                    responseBodyClass =
                            cm.ref("java.util.List").narrow(cm.ref(this.packageNamePrefix + responseClassP));
                } else {
                    responseBodyClass = cm.ref(this.packageNamePrefix + responseClassP);
                }
            }
            JMethod serviceMockMethod = dcServiceMock.method(JMod.PUBLIC, responseBodyClass, methodName);
            serviceMockMethod.javadoc().append(methodRemark);

            List<PacketField> headVariables = method.getHeadVariables();
            if (headVariables != null && headVariables.size() > 0) {
                for (int index = 0; index < headVariables.size(); index++) {
                    PacketField headVariable = headVariables.get(index);
                    JType jType = cm.ref(headVariable.getDatatype());
                    serviceMockMethod.param(jType, headVariable.getName());
                }
            }
            List<PacketField> pathVariables = method.getPathVariables();
            if (pathVariables != null && pathVariables.size() > 0) {
                for (int index = 0; index < pathVariables.size(); index++) {
                    PacketField pathVariable = pathVariables.get(index);
                    JType jType = cm.ref(pathVariable.getDatatype());
                    serviceMockMethod.param(jType, pathVariable.getName());
                }
            }
            List<PacketField> parameters = method.getParameters();
            if (parameters != null && parameters.size() > 0) {
                for (int index = 0; index < parameters.size(); index++) {
                    PacketField parameter = parameters.get(index);
                    JClass jClass = cm.ref(parameter.getDatatype());
                    serviceMockMethod.param(jClass, parameter.getName());
                }
            }
            PacketObject request = method.getRequest();

            if (request != null) {
                JClass requestBodyClass = cm.ref(this.packageNamePrefix + request.get_class());
                String paramName = "request";
                serviceMockMethod.param(requestBodyClass, paramName);
            }
            JBlock serviceMockMethodBlock = serviceMockMethod.body();
            if (response != null) {
                if (!method.isResponseIsArray()) {
                    JVar responseBodyVar = this.composeResponseBody(serviceMockMethodBlock, response,
                            responseBodyClass);
                    serviceMockMethodBlock._return(responseBodyVar);
                } else {

                    String responseClassP = response.get_class();
                    JVar responseBodyVar = this.composeResponseBody(serviceMockMethodBlock, response,
                            cm.ref(this.packageNamePrefix + responseClassP));
                    JVar responseVar = serviceMockMethodBlock
                            .decl(responseBodyClass, "response", JExpr._new(
                                    cm.ref("java.util.ArrayList")
                                            .narrow(cm.ref(this.packageNamePrefix + responseClassP))));
                    serviceMockMethodBlock.invoke(responseVar, "add").arg(responseBodyVar);
                    serviceMockMethodBlock._return(responseVar);
                }

            }

        }
        this.addClassDeclaration(dcServiceMock);
        return dcServiceMock;
    }

    private JVar composeResponseBody(JBlock methodBlk, PacketObject responseBody, JType responseBodyClass) {
        JVar responseVar = composeResponseBodyField(methodBlk, responseBody, responseBodyClass);
        Map<String, PacketObject> robjects = responseBody.getObjects();
        if (robjects != null && robjects.size() > 0) {
            for (String reName : robjects.keySet()) {
                PacketObject ro = robjects.get(reName);
                JClass roClass = cm.ref(this.packageNamePrefix + ro.get_class());
                JVar roVar = this.composeResponseBody(methodBlk, ro, roClass);
                String tterMethodName = reName;
                if (reName.length() > 1) {
                    String char2 = String.valueOf(reName.charAt(1));
                    if (!char2.equals(char2.toUpperCase())) {
                        tterMethodName = StringHelper.toUpperCaseFirstOne(reName);
                    }
                }
                methodBlk.invoke(responseVar, "set" + tterMethodName).arg(roVar);
            }
        }
        Map<String, PacketObject> roList = responseBody.getLists();
        if (roList != null && roList.size() > 0) {
            for (String listName : roList.keySet()) {
                int index = 0;
                PacketObject ro = roList.get(listName);
                JType roListEntityClass = cm.ref(this.packageNamePrefix + ro.get_class());
                String varNamePrefixList = StringHelper.toLowerCaseFirstOne(
                        roListEntityClass.name().substring(roListEntityClass.name().lastIndexOf(".") + 1));
                JVar listsVar = methodBlk.decl(cm.ref("java.util.List").narrow(roListEntityClass),
                        varNamePrefixList + "list",
                        JExpr._new(cm.ref("java.util.ArrayList").narrow(roListEntityClass)));
                JVar iSizeVar = methodBlk.decl(cm.INT, "iSize" + String.valueOf(index++),
                        mockHelperClass.staticInvoke("geInteger").arg(JExpr.lit(10)).invoke("intValue"));
                JForLoop forLoop = methodBlk._for();
                JVar iVar = forLoop.init(cm.INT, "i" + index++, JExpr.lit(0));
                forLoop.test(iVar.lt(iSizeVar));
                forLoop.update(iVar.incr());
                JBlock forBody = forLoop.body();
                JVar roVar = composeResponseBodyList(1, forBody, ro, listName, roListEntityClass);
                forBody.invoke(listsVar, "add").arg(roVar);
                String tterMethodName = listName;
                if (listName.length() > 1) {
                    String char2 = String.valueOf(listName.charAt(1));
                    if (!char2.equals(char2.toUpperCase())) {
                        tterMethodName = StringHelper.toUpperCaseFirstOne(listName);
                    }
                }
                methodBlk.invoke(responseVar, "set" + tterMethodName).arg(listsVar);
            }
        }
        return responseVar;
    }

    private JVar composeResponseBodyField(JBlock methodBlk, PacketObject responseBody, JType responseBodyClass) {
        String varNamePrefix = StringHelper
                .toLowerCaseFirstOne(responseBody.get_class().substring(responseBody.get_class().lastIndexOf(".") + 1));
        JVar reponseBodyVar = methodBlk.decl(responseBodyClass, varNamePrefix, JExpr._new(responseBodyClass));
        Map<String, PacketField> restFields = responseBody.getFields();
        for (String fieldName : restFields.keySet()) {
            PacketField restField = restFields.get(fieldName);
            String dataType = restField.getDatatype();
            String length = restField.getLength();
            JType jType = null;
            String primitiveType = null;
            if (dataType.endsWith(":List")) {
                primitiveType = dataType.split(":")[0];
                jType = cm.ref("java.util.List").narrow(cm.ref(primitiveType));
            } else {
                jType = cm.ref(dataType);
            }
            String tterMethodName = fieldName;
            if (fieldName.length() > 1) {
                String char2 = String.valueOf(fieldName.charAt(1));
                if (!char2.equals(char2.toUpperCase())) {
                    tterMethodName = StringHelper.toUpperCaseFirstOne(fieldName);
                }
            }
            if (primitiveType != null) {
                JVar fieldNameVar = methodBlk.decl(cm.ref("java.util.List").narrow(cm.ref(primitiveType)), fieldName,
                        JExpr._new(cm.ref("java.util.ArrayList").narrow(cm.ref(primitiveType))));
                if (primitiveType.endsWith("String")) {
                    methodBlk.assign(fieldNameVar,
                            mockHelperClass.staticInvoke("geStringListWithAscii").arg(JExpr.lit(10)));
                } else if (primitiveType.endsWith("Boolean")) {
                    methodBlk.assign(fieldNameVar, mockHelperClass.staticInvoke("geBooleanList").arg(JExpr.lit(10)));
                } else if (primitiveType.endsWith("Integer")) {
                    methodBlk.assign(fieldNameVar, mockHelperClass.staticInvoke("geIntegerList").arg(JExpr.lit(10)));
                } else if (primitiveType.endsWith("Long")) {
                    methodBlk.assign(fieldNameVar, mockHelperClass.staticInvoke("geLongList").arg(JExpr.lit(10)));
                } else if (primitiveType.endsWith("Float")) {
                    methodBlk.assign(fieldNameVar, mockHelperClass.staticInvoke("geFloatList").arg(JExpr.lit(10)));
                } else if (primitiveType.endsWith("Double")) {
                    methodBlk.assign(fieldNameVar, mockHelperClass.staticInvoke("geDoubleList").arg(JExpr.lit(10)));
                } else {
                    throw new CodeGException("CG504",
                            responseBodyClass.name() + "-" + fieldNameVar + " handled failed:for" + dataType);
                }
                methodBlk.invoke(reponseBodyVar, "set" + tterMethodName).arg((fieldNameVar));
            } else if (jType.name().endsWith("String")) {
                methodBlk.invoke(reponseBodyVar, "set" + tterMethodName).arg(
                        (mockHelperClass.staticInvoke("geStringWithAscii").arg(JExpr.lit(Integer.parseInt(length)))));
            } else if (jType.name().endsWith("Boolean")) {
                methodBlk.assign(reponseBodyVar, mockHelperClass.staticInvoke("geBooleanList").arg(JExpr.lit(10)));
            } else if (jType.name().endsWith("Integer")) {
                methodBlk.invoke(reponseBodyVar, "set" + tterMethodName)
                        .arg((mockHelperClass.staticInvoke("geInteger").arg(JExpr.lit(100))));
            } else if (jType.name().endsWith("Long")) {
                methodBlk.invoke(reponseBodyVar, "set" + tterMethodName)
                        .arg((mockHelperClass.staticInvoke("geLong").arg(JExpr.lit(100))));
            } else if (jType.name().endsWith("Float")) {
                methodBlk.invoke(reponseBodyVar, "set" + tterMethodName)
                        .arg((mockHelperClass.staticInvoke("geFloat").arg(JExpr.lit(100))));
            } else if (jType.name().endsWith("Double")) {
                methodBlk.invoke(reponseBodyVar, "set" + tterMethodName)
                        .arg((mockHelperClass.staticInvoke("geDouble").arg(JExpr.lit(100))));
            } else {
                throw new CodeGException("CG504",
                        responseBodyClass.name() + "-" + tterMethodName + " handled failed:for" + dataType);
            }

        }
        return reponseBodyVar;

    }

    private JVar composeResponseBodyList(int loopSeq, JBlock methodBlk, PacketObject responseBody, String listName,
                                         JType responseBodyClass) {
        JVar reponseBodyVar = composeResponseBodyField(methodBlk, responseBody, responseBodyClass);
        Map<String, PacketObject> robjects = responseBody.getObjects();
        if (robjects != null && robjects.size() > 0) {
            for (String reName : robjects.keySet()) {
                PacketObject ro2 = robjects.get(reName);
                JClass ro2Class = cm.ref(this.packageNamePrefix + ro2.get_class());
                JVar ro2Var = this.composeResponseBody(methodBlk, ro2, ro2Class);
                String tterMethodName = reName;
                if (reName.length() > 1) {
                    String char2 = String.valueOf(reName.charAt(1));
                    if (!char2.equals(char2.toUpperCase())) {
                        tterMethodName = StringHelper.toUpperCaseFirstOne(reName);
                    }
                }
                methodBlk.invoke(reponseBodyVar, "set" + tterMethodName).arg(ro2Var);
            }
        }
        Map<String, PacketObject> roList = responseBody.getLists();
        if (roList != null && roList.size() > 0) {
            int index = 0;
            for (String indexName : roList.keySet()) {
                index++;
                PacketObject roEntity = roList.get(indexName);
                JType roListEntityClass = cm.ref(this.packageNamePrefix + roEntity.get_class());
                String varNamePrefix = listName + StringHelper.toUpperCaseFirstOne(
                        roListEntityClass.name().substring(roListEntityClass.name().lastIndexOf(".") + 1));
                JVar listsVar = methodBlk.decl(cm.ref("java.util.List").narrow(roListEntityClass),
                        varNamePrefix + "List" + loopSeq,
                        JExpr._new(cm.ref("java.util.ArrayList").narrow(roListEntityClass)));
                JVar iSizeVar = methodBlk.decl(cm.INT, "iSize" + String.valueOf(loopSeq) + String.valueOf(index),
                        mockHelperClass.staticInvoke("geInteger").arg(JExpr.lit(10)).invoke("intValue"));
                JForLoop forLoop = methodBlk._for();
                JVar iVar = forLoop.init(cm.INT, "i" + String.valueOf(loopSeq) + String.valueOf(index), JExpr.lit(0));
                forLoop.test(iVar.lt(iSizeVar));
                forLoop.update(iVar.incr());
                JBlock forBody = forLoop.body();
                JVar ro2Var = composeResponseBodyList(loopSeq + 1, forBody, roEntity, indexName, roListEntityClass);
                forBody.invoke(listsVar, "add").arg(ro2Var);

                methodBlk.invoke(reponseBodyVar, "set" + StringHelper.toUpperCaseFirstOne(indexName)).arg(listsVar);
            }

        }
        return reponseBodyVar;

    }

    protected void addClassDeclaration(JDefinedClass jClass) {
        if (this.skipNotice && this.skipCopyright && this.skipNotice) {
            return;
        }
        JDocComment jDoc = jClass.javadoc();
        if (!this.skipNotice) {
            jDoc.append(this.notice);
        }
        String tempYearCodegTime = YEAR_CODEG_TIME;
        if (this.yearCodegTime != null && !this.yearCodegTime.equals("")) {
            tempYearCodegTime = this.yearCodegTime;
        }
        if (!this.skipCopyright) {
            jDoc.addXdoclet("Copyright " + tempYearCodegTime + " " + this.company + ". All rights reserved.");
        }
        if (!this.skipAuthor) {
            jDoc.addXdoclet("author " + this.author);
        }
    }

    private void processField(int index, PacketField field, JDefinedClass dc, Long hashCode,
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
        if (this.getMockModel() == MockModel.MockModel_Swagger) {
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

    public JDefinedClass processObjectORList(String key, PacketObject po, boolean isObject, Long hashCode,
                                             JDefinedClass dc, int index, JBlock toStringMethodBlk,
                                             JVar toStringSBVar) {
        JDefinedClass poDc = this.processPO(po);
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
        this.addClassDeclaration(poDc);
        return poDc;
    }

    protected void generatePO() {
        this.packetClassMap = new HashMap<>();
        if (this.packetMap != null) {
            for (String key : this.packetMap.keySet()) {
                PacketObject po = this.packetMap.get(key);
                JDefinedClass jDc = this.processPO(po);
                this.packetClassMap.put(key, jDc);
            }
        }
    }

    protected void generateService() {
        for (String key : this.serviceMap.keySet()) {
            CommonService service = this.serviceMap.get(key);
            JDefinedClass jDc = this.processService(service);
            if (this.getMockModel() != null) {
                if (this.getMockModel() != MockModel.MockModel_Common) {
                    this.processServiceMock(service, jDc);
                }
            }
        }
    }

}