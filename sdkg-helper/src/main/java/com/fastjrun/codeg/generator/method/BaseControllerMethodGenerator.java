package com.fastjrun.codeg.generator.method;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestMethod;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fastjrun.codeg.common.CodeGException;
import com.fastjrun.codeg.common.CommonController;
import com.fastjrun.codeg.common.CommonMethod;
import com.fastjrun.codeg.common.PacketField;
import com.fastjrun.codeg.common.PacketObject;
import com.fastjrun.codeg.generator.BaseCMGenerator;
import com.fastjrun.codeg.helper.StringHelper;
import com.fastjrun.codeg.processer.ExchangeProcessor;
import com.fastjrun.utils.JacksonUtils;
import com.sun.codemodel.JAnnotationArrayMember;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JForLoop;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;

public abstract class BaseControllerMethodGenerator extends BaseCMGenerator {

    static JClass jSONObjectClass = cmTest.ref("com.fasterxml.jackson.databind.JsonNode");

    static JClass mockHelperClass = cm.ref("com.fastjrun.helper.MockHelper");

    protected JType responseBodyClass;
    protected JClass elementClass;
    protected JClass responseClass;
    protected JType requestBodyClass;
    protected JType requestClass;

    protected CommonMethod commonMethod;

    protected String methodName;

    protected JMethod jServiceMethod;

    protected JMethod jServiceMockMethod;

    protected JMethod jClientMethod;

    protected JMethod jClientTestMethod;

    protected JMethod jcontrollerMethod;

    protected ObjectNode methodParamInJsonObject;

    protected ExchangeProcessor exchangeProcessor;

    public ExchangeProcessor getExchangeProcessor() {
        return exchangeProcessor;
    }

    public void setExchangeProcessor(ExchangeProcessor exchangeProcessor) {
        this.exchangeProcessor = exchangeProcessor;
    }

    public ObjectNode getMethodParamInJsonObject() {
        return methodParamInJsonObject;
    }

    public void setMethodParamInJsonObject(ObjectNode methodParamInJsonObject) {
        this.methodParamInJsonObject = methodParamInJsonObject;
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

    public JType getResponseBodyClass() {
        return responseBodyClass;
    }

    public void setResponseBodyClass(JType responseBodyClass) {
        this.responseBodyClass = responseBodyClass;
    }

    public JClass getElementClass() {
        return elementClass;
    }

    public void setElementClass(JClass elementClass) {
        this.elementClass = elementClass;
    }

    public JClass getResponseClass() {
        return responseClass;
    }

    public void setResponseClass(JClass responseClass) {
        this.responseClass = responseClass;
    }

    public JType getRequestBodyClass() {
        return requestBodyClass;
    }

    public void setRequestBodyClass(JClass requestBodyClass) {
        this.requestBodyClass = requestBodyClass;
    }

    public JType getRequestClass() {
        return requestClass;
    }

    public void setRequestClass(JType requestClass) {
        this.requestClass = requestClass;
    }

    public JMethod getJcontrollerMethod() {
        return jcontrollerMethod;
    }

    public void setJcontrollerMethod(JMethod jcontrollerMethod) {
        this.jcontrollerMethod = jcontrollerMethod;
    }

    public CommonMethod getCommonMethod() {
        return commonMethod;
    }

    public void setCommonMethod(CommonMethod commonMethod) {
        this.commonMethod = commonMethod;
    }

    protected void parseResponseBodyClass() {
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
                this.responseBodyClass =
                        cm.ref("java.util.List").narrow(elementClass);
            } else {
                this.responseBodyClass = elementClass;
            }
        }
    }

    protected void parseRequestBodyClass() {
        PacketObject request = this.commonMethod.getRequest();
        if (request != null) {
            if (request.is_new()) {
                this.requestBodyClass = cm.ref(this.packageNamePrefix + request.get_class());
            } else {
                this.requestBodyClass = cm.ref(request.get_class());
            }

        }
    }

    public void processServiceMethod(JDefinedClass jServiceClass) {
        this.parseResponseClass();
        this.parseRequestClass();
        this.methodName = commonMethod.getName();

        String methodVersion = commonMethod.getVersion();
        if (methodVersion != null && !methodVersion.equals("")) {
            this.methodName = this.methodName + methodVersion;
        }
        this.jServiceMethod = jServiceClass.method(JMod.NONE, this.responseBodyClass, this.methodName);
        String methodRemark = commonMethod.getRemark();
        this.jServiceMethod.javadoc().append(methodRemark);

        this.processServiceMethodVariables(this.jServiceMethod, this.commonMethod.getHeadVariables());
        this.processServiceMethodVariables(this.jServiceMethod, this.commonMethod.getPathVariables());
        this.processServiceMethodVariables(this.jServiceMethod, this.commonMethod.getParameters());
        this.processServiceMethodVariables(this.jServiceMethod, this.commonMethod.getCookieVariables());

        if (this.requestBodyClass != null) {
            this.jServiceMethod.param(this.requestBodyClass, "requestBody");
        }
    }

    protected void processServiceMethodVariables(JMethod jmethod, List<PacketField> variables) {
        if (variables != null && variables.size() > 0) {
            for (int index = 0; index < variables.size(); index++) {
                PacketField variable = variables.get(index);
                JType jType = cm.ref(variable.getDatatype());
                jmethod.param(jType, variable.getName());
            }
        }
    }

    public void processServiceMockMethod(JDefinedClass jServiceMockClass) {
        this.jServiceMockMethod = jServiceMockClass.method(JMod.PUBLIC, this.responseBodyClass, this.methodName);
        String methodRemark = commonMethod.getRemark();
        this.jServiceMockMethod.javadoc().append(methodRemark);

        this.processServiceMethodVariables(this.jServiceMockMethod, this.commonMethod.getHeadVariables());
        this.processServiceMethodVariables(this.jServiceMockMethod, this.commonMethod.getPathVariables());
        this.processServiceMethodVariables(this.jServiceMockMethod, this.commonMethod.getParameters());
        this.processServiceMethodVariables(this.jServiceMockMethod, this.commonMethod.getCookieVariables());

        if (this.requestBodyClass != null) {
            this.jServiceMockMethod.param(this.requestBodyClass, "requestBody");
        }

        JBlock serviceMockMethodBlock = this.jServiceMockMethod.body();
        if (this.responseBodyClass != cm.VOID) {
            JVar responseBodyVar = this.composeResponseBody(0, serviceMockMethodBlock, commonMethod.getResponse
                    (), this.responseBodyClass);
            if (!commonMethod.isResponseIsArray()) {
                serviceMockMethodBlock._return(responseBodyVar);
            } else {
                JVar responseVar = serviceMockMethodBlock
                        .decl(this.responseBodyClass, "response", JExpr._new(
                                cm.ref("java.util.ArrayList")
                                        .narrow(
                                                this.elementClass)));
                serviceMockMethodBlock.invoke(responseVar, "add").arg(responseBodyVar);
                serviceMockMethodBlock._return(responseVar);
            }

        }

    }

    private JVar composeResponseBody(int loopSeq, JBlock methodBlk, PacketObject responseBody,
                                     JType responseBodyClass) {
        JVar responseVar = composeResponseBodyField(methodBlk, responseBody, responseBodyClass);
        Map<String, PacketObject> packetObjectMap = responseBody.getObjects();
        if (packetObjectMap != null && packetObjectMap.size() > 0) {
            for (String reName : packetObjectMap.keySet()) {
                PacketObject ro = packetObjectMap.get(reName);
                JClass roClass = cm.ref(this.packageNamePrefix + ro.get_class());
                if (!ro.is_new()) {
                    roClass = cm.ref(ro.get_class());
                }
                JVar roVar = this.composeResponseBody(loopSeq + 1, methodBlk, ro, roClass);
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
                if (!ro.is_new()) {
                    roListEntityClass = cm.ref(ro.get_class());
                }
                String varNamePrefixList = StringHelper.toLowerCaseFirstOne(
                        roListEntityClass.name().substring(roListEntityClass.name().lastIndexOf(".") + 1));
                JVar listsVar = methodBlk.decl(cm.ref("java.util.List").narrow(roListEntityClass),
                        varNamePrefixList + "list",
                        JExpr._new(cm.ref("java.util.ArrayList").narrow(roListEntityClass)));
                JVar iSizeVar = methodBlk.decl(cm.INT, "iSize" + loopSeq + index,
                        mockHelperClass.staticInvoke("geInteger").arg(JExpr.lit(10)).invoke("intValue"));
                JForLoop forLoop = methodBlk._for();
                JVar iVar = forLoop.init(cm.INT, "i" + loopSeq + index, JExpr.lit(0));
                forLoop.test(iVar.lt(iSizeVar));
                forLoop.update(iVar.incr());
                JBlock forBody = forLoop.body();
                JVar roVar = composeResponseBody(loopSeq + 1, forBody, ro, roListEntityClass);
                forBody.invoke(listsVar, "add").arg(roVar);
                String tterMethodName = listName;
                if (listName.length() > 1) {
                    String char2 = String.valueOf(listName.charAt(1));
                    if (!char2.equals(char2.toUpperCase())) {
                        tterMethodName = StringHelper.toUpperCaseFirstOne(listName);
                    }
                }
                methodBlk.invoke(responseVar, "set" + tterMethodName).arg(listsVar);
                index++;
            }
        }
        return responseVar;
    }

    private JVar composeResponseBodyField(JBlock methodBlk, PacketObject responseBody, JType
            responseBodyClass) {
        String varNamePrefix = StringHelper
                .toLowerCaseFirstOne(responseBody.get_class().substring(responseBody.get_class().lastIndexOf(".") + 1));
        JVar reponseBodyVar = methodBlk.decl(responseBodyClass, varNamePrefix, JExpr._new(responseBodyClass));
        Map<String, PacketField> restFields = responseBody.getFields();
        if (restFields != null && restFields.size() > 0) {
            for (String fieldName : restFields.keySet()) {
                PacketField restField = restFields.get(fieldName);
                String dataType = restField.getDatatype();
                String length = restField.getLength();
                JType jType;
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
                if (dataType.endsWith(":List")) {
                    JVar fieldNameVar =
                            methodBlk.decl(cm.ref("java.util.List").narrow(cm.ref(primitiveType)), fieldName,
                                    JExpr._new(cm.ref("java.util.ArrayList").narrow(cm.ref(primitiveType))));
                    if (primitiveType.endsWith("String")) {
                        methodBlk.assign(fieldNameVar,
                                mockHelperClass.staticInvoke("geStringListWithAscii").arg(JExpr.lit(10)));
                    } else if (primitiveType.endsWith("Boolean")) {
                        methodBlk
                                .assign(fieldNameVar, mockHelperClass.staticInvoke("geBooleanList").arg(JExpr.lit(10)));
                    } else if (primitiveType.endsWith("Integer")) {
                        methodBlk
                                .assign(fieldNameVar, mockHelperClass.staticInvoke("geIntegerList").arg(JExpr.lit(10)));
                    } else if (primitiveType.endsWith("Long")) {
                        methodBlk.assign(fieldNameVar, mockHelperClass.staticInvoke("geLongList").arg(JExpr.lit(10)));
                    } else if (primitiveType.endsWith("Float")) {
                        methodBlk.assign(fieldNameVar, mockHelperClass.staticInvoke("geFloatList").arg(JExpr.lit(10)));
                    } else if (primitiveType.endsWith("Double")) {
                        methodBlk.assign(fieldNameVar, mockHelperClass.staticInvoke("geDoubleList").arg(JExpr.lit(10)));
                    } else if (primitiveType.endsWith("Date")) {
                        methodBlk.assign(fieldNameVar, mockHelperClass.staticInvoke("geDateList").arg(JExpr.lit(10)));
                    } else {
                        throw new CodeGException("CG504",
                                responseBodyClass.name() + "-" + fieldNameVar + " handled failed:for" + dataType);
                    }
                    methodBlk.invoke(reponseBodyVar, "set" + tterMethodName).arg((fieldNameVar));
                } else if (jType.name().endsWith("String")) {
                    methodBlk.invoke(reponseBodyVar, "set" + tterMethodName).arg(
                            (mockHelperClass.staticInvoke("geStringWithAscii")
                                     .arg(JExpr.lit(Integer.parseInt(length)))));
                } else if (jType.name().endsWith("Boolean")) {
                    String setter = restField.getSetter();
                    if (setter == null || setter.equals("")) {
                        setter = "set" + tterMethodName;
                    }
                    methodBlk.invoke(reponseBodyVar, setter)
                            .arg((mockHelperClass.staticInvoke("geBoolean")));
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
                } else if (jType.name().endsWith("Date")) {
                    methodBlk.invoke(reponseBodyVar, "set" + tterMethodName)
                            .arg((mockHelperClass.staticInvoke("geDate").arg(JExpr.lit(100))));
                } else {
                    throw new CodeGException("CG504",
                            responseBodyClass.name() + "-" + tterMethodName + " handled failed:for" + dataType);
                }
            }
        }
        return reponseBodyVar;
    }

    public void processClientTestMethod(JDefinedClass clientTestClass) {
        JMethod clientTestMethod = clientTestClass.method(JMod.PUBLIC, cmTest.VOID,
                "test" + this.methodName);

        JAnnotationUse methodTestAnnotationTest = clientTestMethod
                .annotate(cmTest.ref("org.testng.annotations.Test"));
        JBlock methodTestBlk = clientTestMethod.body();

        methodTestAnnotationTest.param("dataProvider", "loadParam");

        JAnnotationUse methodTestAnnotationParameters = clientTestMethod
                .annotate(cmTest.ref("org.testng.annotations.Parameters"));
        JAnnotationArrayMember parametersArrayMember = methodTestAnnotationParameters.paramArray("value");
        parametersArrayMember.param("reqParamsJsonStr");
        JVar reqParamsJsonStrJVar = clientTestMethod.param(cmTest.ref("String"), "reqParamsJsonStr");
        methodTestBlk.invoke(JExpr.ref("log"), "debug").arg(reqParamsJsonStrJVar);
        JVar reqParamsJsonJVar = methodTestBlk.decl(jSONObjectClass, "reqParamsJson", cmTest
                .ref("com.fastjrun.utils.JacksonUtils").staticInvoke("toJsonNode")
                .arg(reqParamsJsonStrJVar));

        JInvocation jInvocationTest = JExpr.invoke(JExpr.ref("baseApplicationClient"), methodName);

        // headParams
        List<PacketField> headVariables = this.commonMethod.getHeadVariables();

        if (headVariables != null && headVariables.size() > 0) {
            for (int index = 0; index < headVariables.size(); index++) {
                PacketField headVariable = headVariables.get(index);
                jInvocationTest.arg(JExpr.ref(headVariable.getNameAlias()));
                JClass jType = cmTest.ref(headVariable.getDatatype());
                if (jType.name().endsWith("Boolean")) {
                    methodTestBlk.decl(jType, headVariable.getNameAlias(), reqParamsJsonJVar.invoke("get")
                            .arg(JExpr.lit(headVariable.getNameAlias())).invoke("asBoolean"));
                } else if (jType.name().endsWith("Integer")) {
                    methodTestBlk.decl(jType, headVariable.getNameAlias(), reqParamsJsonJVar.invoke("get")
                            .arg(JExpr.lit(headVariable.getNameAlias())).invoke("asInt"));
                } else if (jType.name().endsWith("Long")) {
                    methodTestBlk.decl(jType, headVariable.getNameAlias(), reqParamsJsonJVar.invoke("get")
                            .arg(JExpr.lit(headVariable.getNameAlias())).invoke("asLong"));
                } else if (jType.name().endsWith("Double")) {
                    methodTestBlk.decl(jType, headVariable.getNameAlias(), reqParamsJsonJVar.invoke("get")
                            .arg(JExpr.lit(headVariable.getNameAlias())).invoke("asDouble"));
                } else {
                    methodTestBlk.decl(jType, headVariable.getNameAlias(), reqParamsJsonJVar.invoke("get")
                            .arg(JExpr.lit(headVariable.getNameAlias())).invoke("asText"));
                }
            }

        }

        List<PacketField> pathVariables = this.commonMethod.getPathVariables();
        if (pathVariables != null && pathVariables.size() > 0) {
            for (int index = 0; index < pathVariables.size(); index++) {
                PacketField pathVariable = pathVariables.get(index);
                jInvocationTest.arg(JExpr.ref(pathVariable.getName()));
                JClass jType = cmTest.ref(pathVariable.getDatatype());
                if (jType.name().endsWith("Boolean")) {
                    methodTestBlk.decl(jType, pathVariable.getName(), reqParamsJsonJVar.invoke("get")
                            .arg(JExpr.lit(pathVariable.getName())).invoke("asBoolean"));
                } else if (jType.name().endsWith("Integer")) {
                    methodTestBlk.decl(jType, pathVariable.getName(), reqParamsJsonJVar.invoke("get")
                            .arg(JExpr.lit(pathVariable.getName())).invoke("asInt"));
                } else if (jType.name().endsWith("Long")) {
                    methodTestBlk.decl(jType, pathVariable.getName(), reqParamsJsonJVar.invoke("get")
                            .arg(JExpr.lit(pathVariable.getName())).invoke("asLong"));
                } else if (jType.name().endsWith("Double")) {
                    methodTestBlk.decl(jType, pathVariable.getName(), reqParamsJsonJVar.invoke("get")
                            .arg(JExpr.lit(pathVariable.getName())).invoke("asDouble"));
                } else {
                    methodTestBlk.decl(jType, pathVariable.getName(), reqParamsJsonJVar.invoke("get")
                            .arg(JExpr.lit(pathVariable.getName())).invoke("asText"));
                }

            }
        }

        List<PacketField> parameters = this.commonMethod.getParameters();
        if (parameters != null && parameters.size() > 0) {
            for (int index = 0; index < parameters.size(); index++) {
                PacketField parameter = parameters.get(index);
                jInvocationTest.arg(JExpr.ref(parameter.getName()));
                JClass jType = cmTest.ref(parameter.getDatatype());
                if (jType.name().endsWith("Boolean")) {
                    methodTestBlk.decl(jType, parameter.getName(), reqParamsJsonJVar.invoke("get")
                            .arg(JExpr.lit(parameter.getName())).invoke("asBoolean"));
                } else if (jType.name().endsWith("Integer")) {
                    methodTestBlk.decl(jType, parameter.getName(), reqParamsJsonJVar.invoke("get")
                            .arg(JExpr.lit(parameter.getName())).invoke("asInt"));
                } else if (jType.name().endsWith("Long")) {
                    methodTestBlk.decl(jType, parameter.getName(), reqParamsJsonJVar.invoke("get")
                            .arg(JExpr.lit(parameter.getName())).invoke("asLong"));
                } else if (jType.name().endsWith("Double")) {
                    methodTestBlk.decl(jType, parameter.getName(), reqParamsJsonJVar.invoke("get")
                            .arg(JExpr.lit(parameter.getName())).invoke("asDouble"));
                } else {
                    methodTestBlk.decl(jType, parameter.getName(), reqParamsJsonJVar.invoke("get")
                            .arg(JExpr.lit(parameter.getName())).invoke("asText"));
                }

            }
        }

        List<PacketField> cookies = this.commonMethod.getCookieVariables();
        if (cookies != null && cookies.size() > 0) {
            for (int index = 0; index < cookies.size(); index++) {
                PacketField cookie = cookies.get(index);
                jInvocationTest.arg(JExpr.ref(cookie.getName()));
                JClass jType = cmTest.ref(cookie.getDatatype());
                if (jType.name().endsWith("Boolean")) {
                    methodTestBlk.decl(jType, cookie.getName(), reqParamsJsonJVar.invoke("get")
                            .arg(JExpr.lit(cookie.getName())).invoke("asBoolean"));
                } else if (jType.name().endsWith("Integer")) {
                    methodTestBlk.decl(jType, cookie.getName(), reqParamsJsonJVar.invoke("get")
                            .arg(JExpr.lit(cookie.getName())).invoke("asInt"));
                } else if (jType.name().endsWith("Long")) {
                    methodTestBlk.decl(jType, cookie.getName(), reqParamsJsonJVar.invoke("get")
                            .arg(JExpr.lit(cookie.getName())).invoke("asLong"));
                } else if (jType.name().endsWith("Double")) {
                    methodTestBlk.decl(jType, cookie.getName(), reqParamsJsonJVar.invoke("get")
                            .arg(JExpr.lit(cookie.getName())).invoke("asDouble"));
                } else {
                    methodTestBlk.decl(jType, cookie.getName(), reqParamsJsonJVar.invoke("get")
                            .arg(JExpr.lit(cookie.getName())).invoke("asText"));
                }
            }
        }
        if (this.requestBodyClass != null) {
            JVar requestBodyStrVar = methodTestBlk.decl(jSONObjectClass, "reqJsonRequestBody",
                    reqParamsJsonJVar.invoke("get").arg(JExpr.lit("requestBody")));
            JVar requestBodyVar = methodTestBlk.decl(this.requestBodyClass, "requestBody",
                    cm.ref("com.fastjrun.utils.JacksonUtils").staticInvoke("readValue")
                            .arg(requestBodyStrVar.invoke("toString"))
                            .arg(((JClass) this.requestBodyClass).dotclass()));
            jInvocationTest.arg(requestBodyVar);
        }
        if (responseBodyClass != cm.VOID) {
            JVar responseBodyVar = methodTestBlk.decl(this.responseBodyClass, "responseBody", jInvocationTest);
            methodTestBlk.invoke(JExpr.refthis("log"), "debug").arg(cm
                    .ref("com.fastjrun.utils.JacksonUtils").staticInvoke("toJSon")
                    .arg(responseBodyVar));
            if (this.commonMethod.isResponseIsArray()) {
                JBlock ifBlock1 = methodTestBlk._if(responseBodyVar.ne(JExpr._null()))._then();
                JForLoop forLoop = ifBlock1._for();
                JVar initIndexVar = forLoop.init(cm.INT, "index", JExpr.lit(0));
                forLoop.test(initIndexVar.lt(responseBodyVar.invoke("size")));
                forLoop.update(initIndexVar.incr());
                JBlock forBlock1 = forLoop.body();
                JVar responseBodyIndexVar =
                        forBlock1.decl(this.elementClass, "item" + this.elementClass.name(),
                                responseBodyVar.invoke("get").arg(initIndexVar));

                this.logResponseBody(this.commonMethod.getResponse(), responseBodyIndexVar, forBlock1);

            } else {
                this.logResponseBody(this.commonMethod.getResponse(), responseBodyVar, methodTestBlk);
            }

        } else {
            methodTestBlk.add(jInvocationTest);
        }
        methodTestBlk.add(cm.ref("org.testng.Assert").staticInvoke("assertTrue").arg(JExpr.lit(true)));

    }

    private void logResponseBodyField(PacketObject responseBody, JVar responseBodyVar, JBlock methodTestBlk) {
        Map<String, PacketField> fields = responseBody.getFields();
        if (fields != null) {
            for (String fieldName : fields.keySet()) {
                PacketField restField = fields.get(fieldName);
                boolean canBeNull = restField.isCanBeNull();
                String dataType = restField.getDatatype();
                JClass jType;
                JClass primitiveType = null;
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
                        commonLog.getLog().error(fieldName + "'s length is assigned a wrong value", e);
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
                JVar fieldNameVar = methodTestBlk
                        .decl(jType, StringHelper.toLowerCaseFirstOne(responseBodyVar.type().name()) + tterMethodName,
                                responseBodyVar.invoke(getter));
                methodTestBlk.invoke(JExpr.ref("log"), "debug").arg(fieldNameVar);
                if (!canBeNull) {
                    methodTestBlk.add(cmTest.ref("org.testng.Assert").staticInvoke("assertNotNull").arg(fieldNameVar));
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

                    ifBlock2.add(cmTest.ref("org.testng.Assert").staticInvoke("fail").arg(JExpr.lit(
                            sbFailReason.toString())
                            .plus(JExpr.ref("actualLength"))));
                }

            }
        }

    }

    private void logResponseBody(PacketObject responseBody, JVar responseBodyVar, JBlock methodTestBlk) {
        logResponseBodyField(responseBody, responseBodyVar, methodTestBlk);
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
                JClass roClass;
                if (ro.is_new()) {
                    roClass = cmTest.ref(this.packageNamePrefix + ro.get_class());
                } else {
                    roClass = cmTest.ref(ro.get_class());
                }
                JVar reNameVar =
                        methodTestBlk.decl(roClass,
                                responseBody.getName() + tterMethodName,
                                responseBodyVar.invoke("get" + tterMethodName));
                this.logResponseBody(ro, reNameVar, methodTestBlk);
            }
        }
        Map<String, PacketObject> roLists = responseBody.getLists();
        if (roLists != null && roLists.size() > 0) {
            for (String roName : roLists.keySet()) {
                PacketObject ro = roLists.get(roName);
                String tterMethodName = roName;
                if (roName.length() > 1) {
                    String char2 = String.valueOf(roName.charAt(1));
                    if (!char2.equals(char2.toUpperCase())) {
                        tterMethodName = StringHelper.toUpperCaseFirstOne(roName);
                    }
                }
                JClass roClass;
                if (ro.is_new()) {
                    roClass = cmTest.ref(this.packageNamePrefix + ro.get_class());
                } else {
                    roClass = cmTest.ref(ro.get_class());
                }
                JVar roListVar =
                        methodTestBlk.decl(cmTest.ref("java.util.List")
                                        .narrow(roClass),
                                responseBody.getName() + tterMethodName + "List",
                                responseBodyVar.invoke("get" + tterMethodName));
                JBlock ifBlock1 = methodTestBlk._if(roListVar.ne(JExpr._null()))._then();
                JForLoop forLoop = ifBlock1._for();
                JVar initIndexVar =
                        forLoop.init(cmTest.INT, responseBody.getName() + tterMethodName + "ListIndex", JExpr.lit(0));
                forLoop.test(initIndexVar.lt(roListVar.invoke("size")));
                forLoop.update(initIndexVar.incr());
                JBlock forBlock1 = forLoop.body();
                JVar responseBodyIndexVar =
                        forBlock1.decl(roClass, roName + roClass
                                        .name(),
                                roListVar.invoke("get").arg(initIndexVar));

                this.logResponseBody(ro, responseBodyIndexVar, forBlock1);
            }
        }
    }

    public void processClientTestPraram() {

        this.methodParamInJsonObject = JacksonUtils.createObjectNode();

        // headParams
        List<PacketField> headVariables = this.commonMethod.getHeadVariables();
        if (headVariables != null && headVariables.size() > 0) {
            for (int index = 0; index < headVariables.size(); index++) {
                PacketField headVariable = headVariables.get(index);
                methodParamInJsonObject.put(headVariable.getNameAlias(), headVariable.getDatatype());

            }

        }
        List<PacketField> pathVariables = this.commonMethod.getPathVariables();
        if (pathVariables != null && pathVariables.size() > 0) {
            for (int index = 0; index < pathVariables.size(); index++) {
                PacketField pathVariable = pathVariables.get(index);
                methodParamInJsonObject.put(pathVariable.getName(), pathVariable.getDatatype());

            }
        }

        List<PacketField> parameters = this.commonMethod.getParameters();
        if (parameters != null && parameters.size() > 0) {
            for (int index = 0; index < parameters.size(); index++) {
                PacketField parameter = parameters.get(index);
                methodParamInJsonObject.put(parameter.getName(), parameter.getDatatype());

            }
        }
        List<PacketField> cookies = this.commonMethod.getCookieVariables();
        if (cookies != null && cookies.size() > 0) {
            for (int index = 0; index < cookies.size(); index++) {
                PacketField cookie = cookies.get(index);
                methodParamInJsonObject.put(cookie.getName(), cookie.getDatatype());

            }
        }

        if (this.requestBodyClass != null) {
            ObjectNode jsonRequestParam = this.composeRequestBody(this.commonMethod.getRequest());
            methodParamInJsonObject.set("requestBody", jsonRequestParam);

        }
    }

    private ObjectNode composeRequestBody(PacketObject requestBody) {
        ObjectNode jsonRequestBody = this.composeRequestBodyField(requestBody);
        Map<String, PacketObject> robjects = requestBody.getObjects();
        if (robjects != null && robjects.size() > 0) {
            for (String reName : robjects.keySet()) {
                PacketObject ro = robjects.get(reName);
                ObjectNode jsonRe = this.composeRequestBody(ro);
                jsonRequestBody.set(reName, jsonRe);
            }
        }
        Map<String, PacketObject> roList = requestBody.getLists();
        if (roList != null && roList.size() > 0) {
            for (String listName : roList.keySet()) {
                PacketObject ro = roList.get(listName);
                ObjectNode jsonRe = this.composeRequestBody(ro);
                ArrayNode jsonAy = JacksonUtils.createArrayNode();
                jsonAy.add(jsonRe);
                jsonRequestBody.set(listName, jsonAy);
            }
        }
        return jsonRequestBody;
    }

    private ObjectNode composeRequestBodyField(PacketObject requestBody) {
        ObjectNode jsonRequestBody = JacksonUtils.createObjectNode();
        Map<String, PacketField> fields = requestBody.getFields();
        if (fields != null) {
            for (String fieldName : fields.keySet()) {
                PacketField restField = fields.get(fieldName);
                String dataType = restField.getDatatype();
                JClass jType;
                JClass primitiveType;
                if (dataType.endsWith(":List")) {
                    primitiveType = cmTest.ref(dataType.split(":")[0]);
                    jType = cmTest.ref("java.util.List").narrow(primitiveType);
                } else {
                    // Integer、Double、Long、Boolean、Character、Float
                    jType = cmTest.ref(dataType);
                }
                jsonRequestBody.put(fieldName, jType.name());

            }
        }
        return jsonRequestBody;

    }

    public void processControllerMethod(CommonController commonController, JDefinedClass controllerClass) {

        RequestMethod requestMethod = RequestMethod.POST;
        switch (this.commonMethod.getHttpMethod().toUpperCase()) {
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
        this.jcontrollerMethod = controllerClass.method(JMod.PUBLIC, this.responseClass, this.methodName);
        String methodRemark = this.commonMethod.getRemark();
        this.jcontrollerMethod.javadoc().append(methodRemark);
        String methodPath = this.commonMethod.getPath();
        if (methodPath == null || methodPath.equals("")) {
            methodPath = "/" + this.commonMethod.getName();
        }
        String methodVersion = this.commonMethod.getVersion();
        if (methodVersion != null && !methodVersion.equals("")) {
            methodPath = methodPath + "/" + methodVersion;
        }

        JBlock controllerMethodBlk = this.jcontrollerMethod.body();

        methodPath = methodPath + this.processRequest();

        JInvocation jInvocation = JExpr.invoke(JExpr.refthis(commonController.getServiceName()), this.methodName);

        if (this.getMockModel() == MockModel.MockModel_Swagger) {
            this.jcontrollerMethod.annotate(cm.ref("io.swagger.annotations.ApiOperation"))
                    .param("value", methodRemark).param("notes", methodRemark);
        }
        List<PacketField> headVariables = this.commonMethod.getHeadVariables();
        if (headVariables != null && headVariables.size() > 0) {
            for (int index = 0; index < headVariables.size(); index++) {
                PacketField headVariable = headVariables.get(index);
                JType jType = cm.ref(headVariable.getDatatype());
                JVar headVariableJVar = this.jcontrollerMethod.param(jType, headVariable.getName());
                headVariableJVar.annotate(cm.ref("org.springframework.web.bind.annotation.RequestHeader"))
                        .param("name", headVariable.getName()).param("required", true);
                jInvocation.arg(headVariableJVar);
                if (this.getMockModel() == MockModel.MockModel_Swagger) {
                    headVariableJVar.annotate(cm.ref("io.swagger.annotations.ApiParam"))
                            .param("name", headVariable.getName()).param("value", headVariable.getRemark())
                            .param("required", true);
                }
            }
        }
        List<PacketField> pathVariables = this.commonMethod.getPathVariables();
        if (pathVariables != null && pathVariables.size() > 0) {
            for (int index = 0; index < pathVariables.size(); index++) {
                PacketField pathVariable = pathVariables.get(index);
                JType jType = cm.ref(pathVariable.getDatatype());
                JVar pathVariableJVar = this.jcontrollerMethod.param(jType, pathVariable.getName());
                pathVariableJVar.annotate(cm.ref("org.springframework.web.bind.annotation.PathVariable"))
                        .param("name", pathVariable.getName()).param("required", true);
                methodPath = methodPath + "/{" + pathVariable.getName() + "}";

                jInvocation.arg(pathVariableJVar);
                if (this.getMockModel() == MockModel.MockModel_Swagger) {
                    pathVariableJVar.annotate(cm.ref("io.swagger.annotations.ApiParam"))
                            .param("name", pathVariable.getName()).param("value", pathVariable.getRemark())
                            .param("required", true);
                }
            }
        }
        List<PacketField> parameters = this.commonMethod.getParameters();
        if (parameters != null && parameters.size() > 0) {
            for (int index = 0; index < parameters.size(); index++) {
                PacketField parameter = parameters.get(index);
                JClass jClass = cm.ref(parameter.getDatatype());
                JVar parameterJVar = this.jcontrollerMethod.param(jClass, parameter.getName());

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
        JAnnotationUse jAnnotationUse = this.jcontrollerMethod
                .annotate(cm.ref("org.springframework.web.bind.annotation.RequestMapping"));
        jAnnotationUse.param("value", methodPath).param("method", requestMethod);

        String[] resTypes = this.commonMethod.getResType().split(",");
        if (resTypes.length == 1) {
            jAnnotationUse.param("produces", resTypes[0]);
        } else {
            JAnnotationArrayMember jAnnotationArrayMember = jAnnotationUse.paramArray("produces");
            for (int i = 0; i < resTypes.length; i++) {
                jAnnotationArrayMember.param(resTypes[i]);
            }
        }

        List<PacketField> cookieVariables = this.commonMethod.getCookieVariables();
        if (cookieVariables != null && cookieVariables.size() > 0) {
            for (int index = 0; index < cookieVariables.size(); index++) {
                PacketField cookieVariable = cookieVariables.get(index);
                JType jType = cm.ref(cookieVariable.getDatatype());
                JVar cookieJVar = this.jcontrollerMethod.param(jType, cookieVariable.getName());
                cookieJVar.annotate(cm.ref("org.springframework.web.bind.annotation.CookieValue"))
                        .param("name", cookieVariable.getName()).param("required", true);
                jInvocation.arg(cookieJVar);
                if (this.getMockModel() == MockModel.MockModel_Swagger) {
                    cookieJVar.annotate(cm.ref("io.swagger.annotations.ApiParam"))
                            .param("name", cookieVariable.getName())
                            .param("value", "cookie:" + cookieVariable.getRemark())
                            .param("required", true);
                }
                controllerMethodBlk.invoke(JExpr.ref("log"), "debug").arg(cookieJVar);
            }
        }

        if (this.requestBodyClass != null) {
            JVar requestParam = this.jcontrollerMethod.param(this.requestBodyClass, "requestBody");
            requestParam.annotate(cm.ref("org.springframework.web.bind.annotation.RequestBody"));
            requestParam.annotate(cm.ref("javax.validation.Valid"));
            jInvocation.arg(JExpr.ref("requestBody"));
        }
        this.processResponse(controllerMethodBlk, jInvocation);

    }

    protected String processRequest() {
        return this.exchangeProcessor.processRequest(this, jcontrollerMethod, this.mockModel);
    }

    protected void processResponse(JBlock methodBlk, JInvocation jInvocation) {
        exchangeProcessor.processResponse(this, methodBlk, jInvocation);
    }

    protected void parseResponseClass() {
        this.parseResponseBodyClass();
        exchangeProcessor.parseResponseClass(this);
    }

    protected void parseRequestClass() {
        this.parseRequestBodyClass();
        exchangeProcessor.parseRequestClass(this);
    }
}