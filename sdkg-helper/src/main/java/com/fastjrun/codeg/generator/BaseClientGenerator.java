
package com.fastjrun.codeg.generator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

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

public abstract class BaseClientGenerator extends POServiceGenerator {

    protected Map<String, Properties> clientTestParamMap;
    protected Document testngXml;
    protected Map<String, CommonController> controllerMap;
    JClass jSONObjectClass = cmTest.ref("com.fasterxml.jackson.databind.JsonNode");

    public Map<String, CommonController> getControllerMap() {
        return controllerMap;
    }

    public void setControllerMap(
            Map<String, CommonController> controllerMap) {
        this.controllerMap = controllerMap;
    }

    public Document getTestngXml() {
        return testngXml;
    }

    public void setTestngXml(Document testngXml) {
        this.testngXml = testngXml;
    }

    public Map<String, Properties> getClientTestParamMap() {
        return clientTestParamMap;
    }

    public void setClientTestParamMap(Map<String, Properties> clientTestParamMap) {
        this.clientTestParamMap = clientTestParamMap;
    }

    public abstract JDefinedClass processController(CommonController commonController);

    public abstract JDefinedClass processClient(CommonController commonController);

    public JDefinedClass processClientTest(CommonController commonController) {
        JDefinedClass clientTestClass = null;
        CommonController.ControllerType controllerType = commonController.getControllerType();
        String clientName = commonController.getClientName() + controllerType.clientSuffix;

        try {
            clientTestClass = cmTest
                    ._class(this.getPackageNamePrefix() + "client." + clientName + "Test");
        } catch (JClassAlreadyExistsException e) {
            String msg = commonController.getName() + "Test is already exists.";
            this.commonLog.getLog().error(msg, e);
            throw new CodeGException(CodeGMsgContants.CODEG_CLASS_EXISTS, msg, e);
        }

        JClass clientClass = cm.ref(this.getPackageNamePrefix() + "client." + clientName);
        clientTestClass._extends(cmTest.ref("com.fastjrun.client.BaseApplicationClientTest").narrow(clientClass));

        CommonService service = commonController.getService();

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

        List<CommonMethod> methods = service.getMethods();
        for (CommonMethod method : methods) {
            PacketObject response;
            String methodName = method.getName();

            String methodVersion = method.getVersion();
            if (methodVersion != null && !methodVersion.equals("")) {
                methodName = methodName + methodVersion;
            }
            response = method.getResponse();
            String responseClassP;

            JType responseBodyClass;
            if (response == null) {
                responseBodyClass = cmTest.VOID;
                responseClassP = "";
            } else {
                responseClassP = response.get_class();
                if (method.isResponseIsArray()) {
                    responseBodyClass =
                            cmTest.ref("java.util.List").narrow(cm.ref(this.packageNamePrefix + responseClassP));
                } else {
                    responseBodyClass = cm.ref(this.packageNamePrefix + responseClassP);
                }
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

            String invokeMethodName;

            if (method.isResponseIsArray()) {
                invokeMethodName = "processList";
            } else {
                invokeMethodName = "process";
            }

            JInvocation jInvocationTest = JExpr.invoke(JExpr.ref("baseApplicationClient"), methodName);
            // path
            JClass stringBuilderClass = cm.ref("StringBuilder");
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
                JVar responseBodyVar = methodTestBlk.decl(responseBodyClass, "responseBody", jInvocationTest);
                if (method.isResponseIsArray()) {
                    JBlock ifBlock1 = methodTestBlk._if(responseBodyVar.ne(JExpr._null()))._then();
                    JForLoop forLoop = ifBlock1._for();
                    JVar initIndexVar = forLoop.init(cmTest.INT, "index", JExpr.lit(0));
                    forLoop.test(initIndexVar.lt(responseBodyVar.invoke("size")));
                    forLoop.update(initIndexVar.incr());
                    JBlock forBlock1 = forLoop.body();
                    JVar responseBodyIndexVar =
                            forBlock1.decl(cmTest.ref(this.packageNamePrefix + responseClassP), StringHelper
                                            .toLowerCaseFirstOne(cmTest.ref(this.packageNamePrefix + responseClassP)
                                                    .name()),
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

        return clientTestClass;

    }

    protected void generateTestngXml(int classThreadCount, int dataProviderThreadCount, int methodThreadCount) {
        Document document = DocumentHelper.createDocument();
        document.addDocType("suite", "SYSTEM", "http://testng.org/testng-1.0.dtd");
        Element rootNode = DocumentHelper.createElement("suite");
        rootNode.addAttribute("name", "clientTest");
        rootNode.addAttribute("parallel", "classes");
        rootNode.addAttribute("thread-count", String.valueOf(classThreadCount));
        rootNode.addAttribute("data-provider-thread-count", String.valueOf(dataProviderThreadCount));
        document.add(rootNode);
        Element testNode = DocumentHelper.createElement("test");
        testNode.addAttribute("name", "${envName}");
        testNode.addAttribute("parallel", "methods");
        testNode.addAttribute("thread-count", String.valueOf(methodThreadCount));
        rootNode.add(testNode);
        Element paramNode = DocumentHelper.createElement("parameter");
        paramNode.addAttribute("name", "envName");
        paramNode.addAttribute("value", "${envName}");
        testNode.add(paramNode);
        Element classesNode = DocumentHelper.createElement("classes");
        for (String key : this.controllerMap.keySet()) {
            CommonController commonController = this.controllerMap.get(key);
            Element classNode = DocumentHelper.createElement("class");
            classNode.addAttribute("name", this.packageNamePrefix + "client." + commonController.getClientName
                    () + commonController.getControllerType().clientSuffix + "Test");

            classesNode.add(classNode);

        }
        testNode.add(classesNode);

        this.testngXml = document;
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
                    // Integer、Double、Long、Boolean、Character、Float
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
                if (fieldName.length() > 1) {
                    String char2 = String.valueOf(fieldName.charAt(1));
                    if (!char2.equals(char2.toUpperCase())) {
                        tterMethodName = StringHelper.toUpperCaseFirstOne(fieldName);
                    }
                }
                JVar fieldNameVar = methodTestBlk
                        .decl(jType, StringHelper.toLowerCaseFirstOne(responseBodyVar.type().name()) + tterMethodName,
                                responseBodyVar.invoke("get" + tterMethodName));
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
                JVar reNameVar =
                        methodTestBlk.decl(cmTest.ref(this.packageNamePrefix + ro.get_class()), responseBody.getName() +
                                        tterMethodName,
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
                JVar roListVar =
                        methodTestBlk.decl(cmTest.ref("java.util.List")
                                        .narrow(cmTest.ref(this.packageNamePrefix + ro.get_class())),
                                responseBody.getName() + tterMethodName + "List",
                                responseBodyVar.invoke("get" + tterMethodName));
                JBlock ifBlock1 = methodTestBlk._if(responseBodyVar.ne(JExpr._null()))._then();
                JForLoop forLoop = ifBlock1._for();
                JVar initIndexVar =
                        forLoop.init(cmTest.INT, responseBody.getName() + tterMethodName + "ListIndex", JExpr.lit(0));
                forLoop.test(initIndexVar.lt(roListVar.invoke("size")));
                forLoop.update(initIndexVar.incr());
                JBlock forBlock1 = forLoop.body();
                JVar responseBodyIndexVar =
                        forBlock1.decl(cmTest.ref(this.packageNamePrefix + ro.get_class()), StringHelper
                                        .toLowerCaseFirstOne(cmTest.ref(this.packageNamePrefix + ro.get_class())
                                                .name()),
                                roListVar.invoke("get").arg(initIndexVar));

                this.logResponseBody(ro, responseBodyIndexVar, forBlock1);
            }
        }
    }

    public Properties processTestParam(CommonController commonController) {
        Properties clientTestParam = new Properties();
        CommonService service = commonController.getService();
        CommonController.ControllerType controllerType = commonController.getControllerType();
        String clientName = commonController.getClientName() + controllerType.clientSuffix;
        List<CommonMethod> methods = service.getMethods();
        for (CommonMethod method : methods) {

            String methodName = method.getName();

            String methodVersion = method.getVersion();
            if (methodVersion != null && !methodVersion.equals("")) {
                methodName = methodName + methodVersion;
            }

            ObjectNode methodParamInJsonObject = JacksonUtils.createObjectNode();

            StringBuilder sbMethod = new StringBuilder("#").append(clientName);

            sbMethod.append(".test").append(StringHelper.toUpperCaseFirstOne(methodName)).append(".n");
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

            // headParams
            List<PacketField> headVariables = method.getHeadVariables();
            if (headVariables != null && headVariables.size() > 0) {
                for (int index = 0; index < headVariables.size(); index++) {
                    PacketField headVariable = headVariables.get(index);

                    methodParamInJsonObject.put(headVariable.getNameAlias(), headVariable.getDatatype());

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

                ObjectNode jsonRequestParam = JacksonUtils.createObjectNode();

                this.composeRequestBody(requestBody, jsonRequestParam);

                methodParamInJsonObject.set("requestBody", jsonRequestParam);

            }
            clientTestParam.put(sbMethod.toString(), methodParamInJsonObject
                    .toString().replaceAll("\n", "").replaceAll("\r", "")
                    .trim());
        }
        return clientTestParam;

    }

    protected void composeRequestBody(PacketObject requestBody, ObjectNode jsonRequestBody) {
        composeRequestBodyField(requestBody, jsonRequestBody);
        Map<String, PacketObject> robjects = requestBody.getObjects();
        if (robjects != null && robjects.size() > 0) {
            for (String reName : robjects.keySet()) {
                PacketObject ro = robjects.get(reName);
                ObjectNode jsonRe = JacksonUtils.createObjectNode();
                this.composeRequestBody(ro, jsonRe);

                jsonRequestBody.set(reName, jsonRe);
            }
        }
        Map<String, PacketObject> roList = requestBody.getLists();
        if (roList != null && roList.size() > 0) {
            for (String listName : roList.keySet()) {
                PacketObject ro = roList.get(listName);
                ArrayNode jsonAy = JacksonUtils.createArrayNode();
                composeRequestBodyList(ro, jsonAy);
                jsonRequestBody.set(listName, jsonAy);
            }
        }
    }

    protected void composeRequestBodyField(PacketObject requestBody, ObjectNode jsonRequestBody) {
        Map<String, PacketField> fields = requestBody.getFields();
        Map<String, PacketObject> robjects = requestBody.getObjects();
        if (robjects != null) {
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

    }

    protected void composeRequestBodyList(PacketObject restObject, ArrayNode jsonAy) {

        ObjectNode jsonObject = JacksonUtils.createObjectNode();
        composeRequestBodyField(restObject, jsonObject);
        Map<String, PacketObject> robjects = restObject.getObjects();
        if (robjects != null && robjects.size() > 0) {
            for (String reName : robjects.keySet()) {
                PacketObject ro2 = robjects.get(reName);
                ObjectNode jsonRe = JacksonUtils.createObjectNode();

                this.composeRequestBody(ro2, jsonRe);
                jsonObject.set(reName, jsonRe);
            }
        }
        Map<String, PacketObject> roList = restObject.getLists();
        if (roList != null && roList.size() > 0) {
            for (String indexName : roList.keySet()) {
                PacketObject roEntity = roList.get(indexName);

                ArrayNode jsonAy2 = JacksonUtils.createArrayNode();
                composeRequestBodyList(roEntity, jsonAy2);

                jsonObject.set(indexName, jsonAy2);
            }

        }

        jsonAy.add(jsonObject);

    }

    protected boolean generateClient() {
        this.clientTestParamMap = new HashMap<>();
        for (String key : this.controllerMap.keySet()) {
            CommonController commonController = this.controllerMap.get(key);
            this.processClient(commonController);
            this.processClientTest(commonController);
            this.clientTestParamMap.put(commonController.getClientName() + commonController.getControllerType()
                    .clientSuffix, this
                    .processTestParam
                            (commonController));
        }
        this.generateTestngXml(5, 5, 5);
        return true;
    }

    protected boolean generateController() {
        for (String key : this.controllerMap.keySet()) {
            CommonController commonController = this.controllerMap.get(key);
            JDefinedClass jDc = this.processController(commonController);
        }
        return true;
    }
}