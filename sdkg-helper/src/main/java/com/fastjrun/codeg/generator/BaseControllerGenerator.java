package com.fastjrun.codeg.generator;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fastjrun.codeg.common.CodeGException;
import com.fastjrun.codeg.common.CodeGMsgContants;
import com.fastjrun.codeg.common.CommonController;
import com.fastjrun.codeg.common.CommonMethod;
import com.fastjrun.codeg.common.CommonService;
import com.fastjrun.codeg.common.PacketField;
import com.fastjrun.codeg.common.PacketObject;
import com.fastjrun.codeg.helper.StringHelper;
import com.fastjrun.utils.JacksonUtils;
import com.sun.codemodel.ClassType;
import com.sun.codemodel.JAnnotationArrayMember;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JForLoop;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;

public abstract class BaseControllerGenerator extends BaseCMGenerator {

    static JClass mockHelperClass = cm.ref("com.fastjrun.helper.MockHelper");

    static JClass jSONObjectClass = cmTest.ref("com.fasterxml.jackson.databind.JsonNode");

    protected String servicePackageName = "service";

    protected String webPackageName = "web.controller";
    protected String mockPackageName = "com.fastjrun.mock";

    protected CommonController commonController;

    protected JDefinedClass serviceClass;
    protected JDefinedClass serviceMockClass;

    protected String clientName;

    protected JDefinedClass clientClass;

    protected JDefinedClass clientTestClass;

    protected Properties clientTestParam;

    public JDefinedClass getClientClass() {
        return clientClass;
    }

    public void setClientClass(JDefinedClass clientClass) {
        this.clientClass = clientClass;
    }

    public JDefinedClass getClientTestClass() {
        return clientTestClass;
    }

    public void setClientTestClass(JDefinedClass clientTestClass) {
        this.clientTestClass = clientTestClass;
    }

    public Properties getClientTestParam() {
        return clientTestParam;
    }

    public void setClientTestParam(Properties clientTestParam) {
        this.clientTestParam = clientTestParam;
    }

    public JDefinedClass getServiceMockClass() {
        return serviceMockClass;
    }

    public void setServiceMockClass(JDefinedClass serviceMockClass) {
        this.serviceMockClass = serviceMockClass;
    }

    public CommonController getCommonController() {
        return commonController;
    }

    public void setCommonController(CommonController commonController) {
        this.commonController = commonController;
    }

    public JDefinedClass getServiceClass() {
        return serviceClass;
    }

    public void setServiceClass(JDefinedClass serviceClass) {
        this.serviceClass = serviceClass;
    }

    protected void processService(CommonService commonService) {
        try {
            this.serviceClass = cm._class(this.packageNamePrefix + this.servicePackageName + commonService.get_class(),
                    ClassType.INTERFACE);
        } catch (JClassAlreadyExistsException e) {
            String msg = commonService.get_class() + " is already exists.";
            this.commonLog.getLog().error(msg, e);
            throw new CodeGException(CodeGMsgContants.CODEG_CLASS_EXISTS, msg, e);
        }
        this.addClassDeclaration(this.serviceClass);
    }

    protected JType parseResponseBodyClassFromCommonMethod(CommonMethod method) {
        PacketObject response = method.getResponse();

        JType responseBodyClass;
        JClass elementJClass = null;
        if (response == null) {
            responseBodyClass = cm.VOID;
        } else {
            String responseClassP = response.get_class();
            if (response.is_new()) {
                elementJClass = cm.ref(this.packageNamePrefix + responseClassP);
            } else {
                elementJClass = cm.ref(responseClassP);
            }
            if (method.isResponseIsArray()) {
                responseBodyClass =
                        cm.ref("java.util.List").narrow(elementJClass);
            } else {
                responseBodyClass = elementJClass;
            }
        }
        return responseBodyClass;
    }

    protected void processServiceMethod(CommonMethod method) {
        String methodName = method.getName();
        String methodRemark = method.getRemark();

        String methodVersion = method.getVersion();
        if (methodVersion != null && !methodVersion.equals("")) {
            methodName = methodName + methodVersion;
        }
        JType responseBodyClass = this.parseResponseBodyClassFromCommonMethod(method);
        JMethod serviceMethod = this.serviceClass.method(JMod.NONE, responseBodyClass, methodName);
        serviceMethod.javadoc().append(methodRemark);

        this.processServiceMethodVariables(serviceMethod, method.getHeadVariables());
        this.processServiceMethodVariables(serviceMethod, method.getPathVariables());
        this.processServiceMethodVariables(serviceMethod, method.getParameters());

        PacketObject request = method.getRequest();
        if (request != null) {
            JClass requestBodyClass;
            if (request.is_new()) {
                requestBodyClass = cm.ref(this.packageNamePrefix + request.get_class());
            } else {
                requestBodyClass = cm.ref(request.get_class());
            }
            String paramName = "request";
            serviceMethod.param(requestBodyClass, paramName);
        }
    }

    protected void processServiceMethodVariables(JMethod serviceMethod, List<PacketField> headVariables) {
        if (headVariables != null && headVariables.size() > 0) {
            for (int index = 0; index < headVariables.size(); index++) {
                PacketField headVariable = headVariables.get(index);
                JType jType = cm.ref(headVariable.getDatatype());
                serviceMethod.param(jType, headVariable.getName());
            }
        }
    }

    protected void processServiceMock(CommonService commonService) {
        try {
            this.serviceMockClass = cm._class("com.fastjrun.mock." + commonService.get_class() + "Mock");
            serviceMockClass._implements(this.serviceClass);
            serviceMockClass.annotate(cm.ref("org.springframework.stereotype.Service")).param("value",
                    commonService.getName());
        } catch (JClassAlreadyExistsException e) {
            String msg = commonService.get_class() + " is already exists.";
            this.commonLog.getLog().error(msg, e);
            throw new CodeGException(CodeGMsgContants.CODEG_CLASS_EXISTS, msg, e);
        }
        this.addClassDeclaration(this.serviceMockClass);
    }

    protected void processServiceMockMethod(CommonMethod method) {
        String methodName = method.getName();
        String methodRemark = method.getRemark();

        String methodVersion = method.getVersion();
        if (methodVersion != null && !methodVersion.equals("")) {
            methodName = methodName + methodVersion;
        }
        JType responseBodyClass = this.parseResponseBodyClassFromCommonMethod(method);
        JMethod serviceMockMethod = this.serviceMockClass.method(JMod.PUBLIC, responseBodyClass, methodName);
        serviceMockMethod.javadoc().append(methodRemark);

        JBlock serviceMockMethodBlock = serviceMockMethod.body();
        PacketObject response = method.getResponse();
        String responseClassP = response.get_class();
        if (responseBodyClass != cm.VOID) {
            if (!method.isResponseIsArray()) {
                if (responseBodyClass.name().endsWith("Boolean")) {
                    serviceMockMethodBlock._return(mockHelperClass.staticInvoke("geBoolean"));
                } else if (responseBodyClass.name().endsWith("Integer")) {
                    serviceMockMethodBlock._return(mockHelperClass.staticInvoke("geInteger"));
                } else if (responseBodyClass.name().endsWith("Long")) {
                    serviceMockMethodBlock._return(mockHelperClass.staticInvoke("geLong"));
                } else if (responseBodyClass.name().endsWith("Double")) {
                    serviceMockMethodBlock._return(mockHelperClass.staticInvoke("geDouble"));
                } else if (responseBodyClass.name().endsWith("String")) {
                    serviceMockMethodBlock._return(mockHelperClass.staticInvoke("geStringWithAlphabetic").arg
                            (JExpr.lit(10)));
                } else if (responseBodyClass.name().endsWith("Date")) {
                    serviceMockMethodBlock._return(mockHelperClass.staticInvoke("geDate").arg
                            (JExpr.lit(10)));
                } else {
                    JVar responseBodyVar = this.composeResponseBody(1, serviceMockMethodBlock, response,
                            responseBodyClass);
                    serviceMockMethodBlock._return(responseBodyVar);
                }
            } else {
                JClass elementClass;
                if (response.is_new()) {
                    elementClass = cm.ref(this.packageNamePrefix + response.get_class());
                } else {
                    elementClass = cm.ref(response.get_class());
                }
                if (responseClassP.endsWith("Boolean")) {
                    serviceMockMethodBlock
                            ._return(mockHelperClass.staticInvoke("geBooleanList").arg(JExpr.lit(10)));
                } else if (responseClassP.endsWith("Integer")) {
                    serviceMockMethodBlock
                            ._return(mockHelperClass.staticInvoke("geIntegerList").arg(JExpr.lit(10)));
                } else if (responseClassP.endsWith("Long")) {
                    serviceMockMethodBlock._return(mockHelperClass.staticInvoke("geLongList").arg(JExpr.lit(10)));
                } else if (responseClassP.endsWith("Double")) {
                    serviceMockMethodBlock._return(mockHelperClass.staticInvoke("geDoubleList").arg(JExpr.lit(10)));
                } else if (responseClassP.endsWith("String")) {
                    serviceMockMethodBlock._return(mockHelperClass.staticInvoke("geStringListWithAscii").arg
                            (JExpr.lit(10)));
                } else if (responseClassP.endsWith("Date")) {
                    serviceMockMethodBlock._return(mockHelperClass.staticInvoke("geDateList").arg
                            (JExpr.lit(10)));
                } else {
                    JVar responseBodyVar = this.composeResponseBody(1, serviceMockMethodBlock, response, elementClass);
                    JVar responseVar = serviceMockMethodBlock
                            .decl(responseBodyClass, "response", JExpr._new(
                                    cm.ref("java.util.ArrayList")
                                            .narrow(
                                                    elementClass)));
                    serviceMockMethodBlock.invoke(responseVar, "add").arg(responseBodyVar);
                    serviceMockMethodBlock._return(responseVar);
                }
            }

        }

    }

    private JVar composeResponseBody(int loopSeq, JBlock methodBlk, PacketObject responseBody,
                                     JType responseBodyClass) {
        JVar responseVar = composeResponseBodyField(loopSeq, methodBlk, responseBody, responseBodyClass);
        Map<String, PacketObject> robjects = responseBody.getObjects();
        if (robjects != null && robjects.size() > 0) {
            for (String reName : robjects.keySet()) {
                PacketObject ro = robjects.get(reName);
                JClass roClass = cm.ref(this.packageNamePrefix + ro.get_class());
                if (!ro.is_new()) {
                    roClass = cm.ref(ro.get_class());
                }
                JVar roVar = this.composeResponseBody(loopSeq++, methodBlk, ro, roClass);
                String tterMethodName = reName;
                if (reName.length() > 1) {
                    String char2 = String.valueOf(reName.charAt(1));
                    if (!char2.equals(char2.toUpperCase())) {
                        tterMethodName = StringHelper.toUpperCaseFirstOne(reName);
                    }
                    tterMethodName += loopSeq;
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
                JVar iSizeVar = methodBlk.decl(cm.INT, "iSize" + String.valueOf(index++),
                        mockHelperClass.staticInvoke("geInteger").arg(JExpr.lit(10)).invoke("intValue"));
                JForLoop forLoop = methodBlk._for();
                JVar iVar = forLoop.init(cm.INT, "i" + index++, JExpr.lit(0));
                forLoop.test(iVar.lt(iSizeVar));
                forLoop.update(iVar.incr());
                JBlock forBody = forLoop.body();
                JVar roVar = composeResponseBody(loopSeq++, forBody, ro, roListEntityClass);
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

    private JVar composeResponseBodyField(int loopSeq, JBlock methodBlk, PacketObject responseBody, JType
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
                    tterMethodName += loopSeq;
                }
                if (primitiveType != null) {
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

    protected abstract void processClient();

    protected void processClientTest() {
        try {
            clientTestClass = cmTest
                    ._class(this.getPackageNamePrefix() + "client." + this.clientName + "Test");
        } catch (JClassAlreadyExistsException e) {
            String msg = commonController.getName() + "Test is already exists.";
            this.commonLog.getLog().error(msg, e);
            throw new CodeGException(CodeGMsgContants.CODEG_CLASS_EXISTS, msg, e);
        }
        clientTestClass._extends(cmTest.ref("com.fastjrun.api.client.BaseApplicationClientTest").narrow(clientClass));
        this.addClassDeclaration(clientTestClass);
        JMethod clientTestPrepareApplicationClientMethod = clientTestClass.method(JMod.PUBLIC, cmTest.VOID,
                "prepareApplicationClient");
        JVar jVarEnvName = clientTestPrepareApplicationClientMethod.param(cmTest.ref("String"), "envName");
        clientTestPrepareApplicationClientMethod.annotate(cmTest.ref("Override"));
        clientTestPrepareApplicationClientMethod.annotate(cmTest.ref("org.testng.annotations.BeforeTest"));
        clientTestPrepareApplicationClientMethod.annotate(cmTest.ref("org.testng.annotations.Parameters"))
                .paramArray("value").param("envName");
        JBlock jBlock = clientTestPrepareApplicationClientMethod.body();
        jBlock.assign(JExpr.ref("baseApplicationClient"),
                JExpr._new(clientClass));
        jBlock.invoke(JExpr._this(), "init").arg(jVarEnvName);
    }

    protected void processClientTestMethod(CommonMethod method) {
        JType responseBodyClass = this.parseResponseBodyClassFromCommonMethod(method);

        String methodName = method.getName();

        String methodVersion = method.getVersion();
        if (methodVersion != null && !methodVersion.equals("")) {
            methodName = methodName + methodVersion;
        }
        JMethod clientTestMethod = clientTestClass.method(JMod.PUBLIC, cmTest.VOID,
                "test" + StringHelper.toUpperCaseFirstOne(methodName));

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
        List<PacketField> headVariables = method.getHeadVariables();

        JVar headParamsJvar = null;
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

        List<PacketField> pathVariables = method.getPathVariables();
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

        List<PacketField> parameters = method.getParameters();
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

        List<PacketField> cookies = method.getCookieVariables();
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
        PacketObject requestBody = method.getRequest();
        if (requestBody != null) {
            JClass requestBodyClass = cmTest.ref(this.packageNamePrefix + requestBody.get_class());
            JVar requestBodyStrVar = methodTestBlk.decl(jSONObjectClass, "reqJsonRequestBody",
                    reqParamsJsonJVar.invoke("get").arg(JExpr.lit("requestBody")));
            JVar requestBodyVar = methodTestBlk.decl(requestBodyClass, "requestBody",
                    cmTest.ref("com.fastjrun.utils.JacksonUtils").staticInvoke("readValue")
                            .arg(requestBodyStrVar.invoke("toString")).arg(requestBodyClass.dotclass()));
            jInvocationTest.arg(requestBodyVar);
        }
        if (responseBodyClass != cmTest.VOID) {
            PacketObject response = method.getResponse();
            JVar responseBodyVar = methodTestBlk.decl(responseBodyClass, "responseBody", jInvocationTest);
            methodTestBlk.invoke(JExpr.refthis("log"), "debug").arg(cmTest
                    .ref("com.fastjrun.utils.JacksonUtils").staticInvoke("toJSon")
                    .arg(responseBodyVar));
            if (method.isResponseIsArray()) {
                JBlock ifBlock1 = methodTestBlk._if(responseBodyVar.ne(JExpr._null()))._then();
                JForLoop forLoop = ifBlock1._for();
                JVar initIndexVar = forLoop.init(cmTest.INT, "index", JExpr.lit(0));
                forLoop.test(initIndexVar.lt(responseBodyVar.invoke("size")));
                forLoop.update(initIndexVar.incr());
                JBlock forBlock1 = forLoop.body();
                JClass elementJClass;
                String responseClassP = response.get_class();
                if (method.getResponse().is_new()) {
                    elementJClass = cm.ref(this.packageNamePrefix + responseClassP);
                } else {
                    elementJClass = cm.ref(responseClassP);
                }
                JVar responseBodyIndexVar =
                        forBlock1.decl(elementJClass, "item" + elementJClass.name(),
                                responseBodyVar.invoke("get").arg(initIndexVar));

                this.logResponseBody(response, responseBodyIndexVar, forBlock1);

            } else {
                this.logResponseBody(response, responseBodyVar, methodTestBlk);
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

    protected void processClientTestPraram(CommonMethod method) {
        String methodName = method.getName();

        String methodVersion = method.getVersion();
        if (methodVersion != null && !methodVersion.equals("")) {
            methodName = methodName + methodVersion;
        }

        ObjectNode methodParamInJsonObject = JacksonUtils.createObjectNode();

        StringBuilder sbMethod = new StringBuilder(this.clientName);

        sbMethod.append(".test").append(StringHelper.toUpperCaseFirstOne(methodName)).append(".n");

        // headParams
        List<PacketField> headVariables = method.getHeadVariables();
        if (headVariables != null && headVariables.size() > 0) {
            for (int index = 0; index < headVariables.size(); index++) {
                PacketField headVariable = headVariables.get(index);
                methodParamInJsonObject.put(headVariable.getNameAlias(), headVariable.getDatatype());

            }

        }
        List<PacketField> pathVariables = method.getPathVariables();
        if (pathVariables != null && pathVariables.size() > 0) {
            for (int index = 0; index < pathVariables.size(); index++) {
                PacketField pathVariable = pathVariables.get(index);
                methodParamInJsonObject.put(pathVariable.getName(), pathVariable.getDatatype());

            }
        }

        List<PacketField> parameters = method.getParameters();
        if (parameters != null && parameters.size() > 0) {
            for (int index = 0; index < parameters.size(); index++) {
                PacketField parameter = parameters.get(index);
                methodParamInJsonObject.put(parameter.getName(), parameter.getDatatype());

            }
        }
        List<PacketField> cookies = method.getCookieVariables();
        if (cookies != null && cookies.size() > 0) {
            for (int index = 0; index < cookies.size(); index++) {
                PacketField cookie = cookies.get(index);
                methodParamInJsonObject.put(cookie.getName(), cookie.getDatatype());

            }
        }

        // requestBody
        PacketObject requestBody = method.getRequest();
        if (requestBody != null) {
            ObjectNode jsonRequestParam = this.composeRequestBody(requestBody);
            methodParamInJsonObject.set("requestBody", jsonRequestParam);

        }
        this.clientTestParam.put(sbMethod.toString(), methodParamInJsonObject
                .toString().replaceAll("\n", "").replaceAll("\r", "")
                .trim());
    }

    protected ObjectNode composeRequestBody(PacketObject requestBody) {
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

    protected ObjectNode composeRequestBodyField(PacketObject requestBody) {
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
}