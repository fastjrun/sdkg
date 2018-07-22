package com.fastjrun.codeg.bundle;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fastjrun.codeg.CodeGException;
import com.fastjrun.codeg.bundle.common.CommonController;
import com.fastjrun.codeg.bundle.common.CommonController.ControllerType;
import com.fastjrun.codeg.bundle.common.CommonMethod;
import com.fastjrun.codeg.bundle.common.CommonService;
import com.fastjrun.codeg.bundle.common.PacketField;
import com.fastjrun.codeg.bundle.common.PacketObject;
import com.fastjrun.codeg.helper.BundleXMLParser;
import com.fastjrun.codeg.helper.StringHelper;
import com.fastjrun.utils.JacksonUtils;
import com.sun.codemodel.CodeWriter;
import com.sun.codemodel.JAnnotationArrayMember;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;
import com.sun.codemodel.writer.FileCodeWriter;

public class SDKGenerator extends BundleGenerator {

    JClass jSONObjectClass = cmTest.ref("com.fasterxml.jackson.databind.JsonNode");

    JClass jSONAssayClass = cmTest.ref("com.fasterxml.jackson.databind.JsonNode");

    Map<String, JClass> clientClassMap;

    private String appName;
    private boolean supportDubbo = false;

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public boolean isSupportDubbo() {
        return supportDubbo;
    }

    public void setSupportDubbo(boolean supportDubbo) {
        this.supportDubbo = supportDubbo;
    }

    private JClass processSDKORClient(CommonController commonController) {

        try {
            JDefinedClass clientClass = cm
                    ._class(this.packageNamePrefix + "client." + commonController.getClientName());
            File outFile = new File(this.moduleName + this.getTestDataName() + File.separator
                    + commonController.getClientName() + ".properties");
            outFile.getParentFile().mkdirs();
            if (outFile.exists()) {
                outFile.delete();
            }
            FileWriter resFw = new FileWriter(outFile);
            Properties restProp = new Properties();

            JClass StringClass = cm.ref("String");

            CommonService service = commonController.getService();
            JClass jParentClass = cm.ref("com.fastjrun.client.BaseApplicationClient");

            this.addClassDeclaration(clientClass);
            clientClass._extends(jParentClass);
            JClass dcService = serviceClassMap.get(service.getName());
            clientClass._implements(dcService);

            ControllerType controllerType = commonController.getControllerType();
            JClass baseClientClass;
            if (controllerType == ControllerType.ControllerType_API) {
                baseClientClass = cm.ref("com.fastjrun.client.DefaultApiClient");
            } else if (controllerType == ControllerType.ControllerType_APP) {
                baseClientClass = cm.ref("com.fastjrun.client.DefaultAppClient");
            } else {
                baseClientClass = cm.ref("com.fastjrun.client.DefaultGenericClient");
            }

            JMethod clientInitMethod = clientClass.method(JMod.PUBLIC, cm.VOID, "initSDKConfig");
            clientInitMethod.annotate(cm.ref("Override"));
            JVar paramVar = clientInitMethod.param(StringClass, "apiWorld");
            JBlock jInitBlock = clientInitMethod.body();
            jInitBlock.assign(JExpr.ref("baseClient"), JExpr._new(baseClientClass));
            jInitBlock.invoke(JExpr.ref("baseClient"), "initSDKConfig").arg(paramVar);

            JDefinedClass clientTestClass = cmTest
                    ._class(this.packageNamePrefix + "client." + commonController.getClientName() + "Test");
            clientTestClass._extends(cmTest.ref("com.fastjrun.test.BaseApplicationClientTest").narrow(clientClass));
            this.addClassDeclaration(clientTestClass);
            JMethod clientTestPrepareApplicationClientMethod = clientTestClass.method(JMod.PUBLIC, cmTest.VOID,
                    "prepareApplicationClient");
            JVar jVarEnvName = clientTestPrepareApplicationClientMethod.param(StringClass, "envName");
            clientTestPrepareApplicationClientMethod.annotate(cmTest.ref("Override"));
            clientTestPrepareApplicationClientMethod.annotate(cmTest.ref("org.testng.annotations.BeforeTest"));
            clientTestPrepareApplicationClientMethod.annotate(cmTest.ref("org.testng.annotations.Parameters"))
                    .paramArray("value").param("envName");
            JBlock jBlock = clientTestPrepareApplicationClientMethod.body();
            jBlock.assign(JExpr.ref("baseApplicationClient"),
                    JExpr._new(clientClass));
            jBlock.invoke(JExpr._this(), "initSDKConfig").arg(jVarEnvName);

            List<CommonMethod> methods = service.getMethods();
            for (CommonMethod method : methods) {
                JType jResponseClass;
                String methodName = method.getName();
                String methodRemark = method.getRemark();
                StringBuilder sbMethod = new StringBuilder("#").append(commonController.getClientName());
                String methodVersion = method.getVersion();
                String methodPath = method.getPath();
                if (methodPath == null || methodPath.equals("")) {
                    methodPath = "/" + methodName;
                }
                if (methodVersion != null && !methodVersion.equals("")) {
                    methodName += methodVersion;
                    methodPath += "/" + methodVersion;
                }

                PacketObject response = method.getResponse();
                if (response == null) {
                    jResponseClass = cm.VOID;
                } else {
                    String _class = response.get_class();
                    response = packetMap.get(_class);
                    jResponseClass = poClassMap.get(_class);
                }
                JMethod clientMethod = clientClass.method(JMod.PUBLIC, jResponseClass, methodName);

                JMethod clientTestMethod = clientTestClass.method(JMod.PUBLIC, cmTest.VOID,
                        "test" + StringHelper.toUpperCaseFirstOne(methodName));
                ObjectNode methodParamInJsonObject = JacksonUtils.createObjectNode();

                JBlock methodBlk = clientMethod.body();

                sbMethod.append(".test").append(StringHelper.toUpperCaseFirstOne(methodName)).append(".n");

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
                        .ref("com.fastjrun.utils.JacksonUtils").staticInvoke("toJsonNode").arg(reqParamsJsonStrJVar));

                JInvocation jInvocation = JExpr.invoke(JExpr.ref("baseClient"), "process");

                JInvocation jInvocationTest = JExpr.invoke(JExpr.ref("baseApplicationClient"), methodName);

                // path
                JClass stringBuilderClass = cm.ref("StringBuilder");
                JVar pathVar = methodBlk.decl(stringBuilderClass, "path",
                        JExpr._new(stringBuilderClass).arg(JExpr.lit(commonController.getPath() + methodPath)));
                List<PacketField> pathVariables = method.getPathVariables();
                if (pathVariables != null && pathVariables.size() > 0) {
                    for (int index = 0; index < pathVariables.size(); index++) {
                        PacketField pathVariable = pathVariables.get(index);
                        JClass jClass = cm.ref(pathVariable.getDatatype());
                        clientMethod.param(jClass, pathVariable.getName());
                        jInvocationTest.arg(JExpr.ref(pathVariable.getName()));

                        methodBlk.invoke(pathVar, "append").arg(JExpr.lit("/"));
                        methodBlk.invoke(pathVar, "append").arg(JExpr.ref(pathVariable.getName()));

                        methodBlk.invoke(JExpr.ref("log"), "debug").arg(JExpr.lit("pathVariable[{}] = {}"))
                                .arg(JExpr.lit(pathVariable.getName())).arg(JExpr.ref(pathVariable.getName()));

                        methodParamInJsonObject.put(pathVariable.getName(), pathVariable.getDatatype());

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
                jInvocation.arg(pathVar.invoke("toString"));

                methodBlk.invoke(JExpr.ref("log"), "debug").arg(JExpr.lit("path = {}")).arg(pathVar.invoke("toString"));
                // method
                jInvocation.arg(JExpr.lit(method.getHttpMethod().toUpperCase()));

                methodBlk.invoke(JExpr.ref("log"), "debug")
                        .arg(JExpr.lit("method = " + method.getHttpMethod().toUpperCase()));

                List<PacketField> parameters = method.getParameters();
                if (parameters != null && parameters.size() > 0) {// queryParams
                    JVar queryParamsJvar = methodBlk.decl(
                            cm.ref("java.util.Map").narrow(StringClass).narrow(StringClass), "queryParams",
                            JExpr._new(cm.ref("java.util.HashMap").narrow(StringClass).narrow(StringClass)));
                    for (int index = 0; index < parameters.size(); index++) {
                        PacketField parameter = parameters.get(index);
                        JClass jClass = cm.ref(parameter.getDatatype());
                        clientMethod.param(jClass, parameter.getName());

                        jInvocationTest.arg(JExpr.ref(parameter.getName()));

                        JExpression jInvocationParameter = JExpr.ref(parameter.getName());

                        if (jClass != StringClass) {
                            jInvocationParameter = StringClass.staticInvoke("valueOf").arg(jInvocationParameter);
                        }

                        methodBlk.invoke(queryParamsJvar, "put").arg(JExpr.lit(parameter.getName()))
                                .arg(jInvocationParameter);

                        methodBlk.invoke(JExpr.ref("log"), "debug").arg(JExpr.lit("paramter[{}] = {}"))
                                .arg(JExpr.lit(parameter.getName())).arg(JExpr.ref(parameter.getName()));

                        methodParamInJsonObject.put(parameter.getName(), parameter.getDatatype());

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
                    jInvocation.arg(queryParamsJvar);
                } else {
                    jInvocation.arg(JExpr._null());
                }

                // headParams
                List<PacketField> headVariables = method.getHeadVariables();
                if (headVariables != null && headVariables.size() > 0) {// headParams
                    JVar headParamsJvar = methodBlk.decl(
                            cm.ref("java.util.Map").narrow(StringClass).narrow(StringClass), "headParams",
                            JExpr._new(cm.ref("java.util.HashMap").narrow(StringClass).narrow(StringClass)));
                    for (int index = 0; index < headVariables.size(); index++) {
                        PacketField headVariable = headVariables.get(index);
                        JClass jClass = cm.ref(headVariable.getDatatype());
                        clientMethod.param(jClass, headVariable.getNameAlias());

                        jInvocationTest.arg(JExpr.ref(headVariable.getNameAlias()));
                        methodBlk.invoke(headParamsJvar, "put").arg(JExpr.lit(headVariable.getName()))
                                .arg(JExpr.ref(headVariable.getNameAlias()));

                        methodParamInJsonObject.put(headVariable.getNameAlias(), headVariable.getDatatype());

                        methodBlk.invoke(JExpr.ref("log"), "debug").arg(JExpr.lit("header[{}] = {}"))
                                .arg(JExpr.lit(headVariable.getNameAlias()))
                                .arg(JExpr.ref(headVariable.getNameAlias()));

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
                    jInvocation.arg(headParamsJvar);
                } else {
                    jInvocation.arg(JExpr._null());
                }

                // requestBody
                PacketObject requestBody = method.getRequest();
                PacketObject responsetBody = method.getResponse();
                JClass requestBodyClass;
                JClass responseBodyClass;
                if (responsetBody == null) {
                    responseBodyClass = cmTest.ref("com.fastjrun.packet.EmptyResponseBody");
                } else {
                    responseBodyClass = this.poClassMap.get(responsetBody.get_class());
                }
                if (requestBody != null) {
                    requestBodyClass = this.poClassMap.get(requestBody.get_class());
                    JVar requestBodyStrVar = methodTestBlk.decl(jSONObjectClass, "reqJsonRequestBody",
                            reqParamsJsonJVar.invoke("get").arg(JExpr.lit("requestBody")));

                    JVar requestBodyVar = methodTestBlk.decl(requestBodyClass, "requestBody",
                            cmTest.ref("com.fastjrun.utils.JacksonUtils").staticInvoke("readValue")
                                    .arg(requestBodyStrVar.invoke("toString")).arg(requestBodyClass.dotclass()));

                    JVar jRequestBodyVar = clientMethod.param(requestBodyClass, "requestBody");
                    jInvocation.arg(jRequestBodyVar);
                    jInvocationTest.arg(requestBodyVar);

                    ObjectNode jsonRequestParam = JacksonUtils.createObjectNode();

                    this.composeRequestBody(requestBody, jsonRequestParam);

                    methodParamInJsonObject.set("requestBody", jsonRequestParam);

                } else {
                    JDefinedClass jRequestBodyClass = cmTest.anonymousClass(
                            cmTest.ref("com.fastjrun.packet.BaseRequestBody").narrow(responseBodyClass));
                    JMethod jmethod =
                            jRequestBodyClass.method(JMod.PUBLIC, cmTest.ref("Class").narrow(responseBodyClass),
                                    "getResponseBodyClass");
                    jmethod.annotate(cmTest.ref("Override"));
                    jmethod.body()._return(JExpr.dotclass(responseBodyClass));
                    JVar requestBodyVar = methodBlk.decl(jRequestBodyClass, "requestBody",
                            JExpr._new(jRequestBodyClass));
                    jInvocation.arg(requestBodyVar);
                }

                if (jResponseClass != cm.VOID) {
                    methodBlk._return(jInvocation);
                    JVar responseBodyVar = methodTestBlk.decl(jResponseClass, "responseBody", jInvocationTest);
                    methodTestBlk.invoke(JExpr.ref("log"), "debug").arg(responseBodyVar);
                } else {
                    methodBlk.add(jInvocation);
                    methodTestBlk.add(jInvocationTest);
                }
                methodTestBlk.add(cm.ref("org.testng.Assert").staticInvoke("assertTrue").arg(JExpr.lit(true)));

                restProp.put(sbMethod.toString(), methodParamInJsonObject
                        .toString().replaceAll("\n", "").replaceAll("\r", "")
                        .trim());
            }

            restProp.store(resFw, "ok");
            resFw.close();
            return clientClass;

        } catch (JClassAlreadyExistsException e) {
            throw new CodeGException("CG505", commonController.getClientName() + " create failed:" + e.getMessage());
        } catch (IOException e) {
            throw new CodeGException("CG505", commonController.getClientName() + " properties file create failed:" + e
                    .getMessage());
        }

    }

    @Override
    public boolean generate() {
        Date begin = new Date();
        log.info("begin genreate at {}", begin);
        this.beforeGenerate();
        if (this.bundleFiles != null && this.bundleFiles.length > 0) {
            for (String bundleFile : bundleFiles) {
                this.generate(bundleFile);
            }

        }
        try {
            // 生成代码为UTF-8编码
            CodeWriter src = new FileCodeWriter(this.srcDir, "UTF-8");
            CodeWriter srcTest = new FileCodeWriter(this.testSrcDir, "UTF-8");
            // 自上而下地生成类、方法等
            cm.build(src);
            cmTest.build(srcTest);
        } catch (IOException e) {
            throw new CodeGException("CG502", "code generating failed:" + e.getMessage());
        }
        Date end = new Date();
        log.info("end genreate at {},cast {} ms", end, end.getTime() - begin.getTime());

        return true;
    }

    private boolean generate(String bundleFile) {
        this.packetMap = BundleXMLParser.processPacket(bundleFile);
        this.generatePO();
        this.serviceMap = BundleXMLParser.processService(bundleFile, this.packetMap);
        this.generateService();
        this.controllerMap = BundleXMLParser.processControllers(bundleFile, serviceMap);
        this.generateSDKORClient();
        return true;
    }

    private void generateSDKORClient() {
        clientClassMap = new HashMap<>();
        for (String key : this.controllerMap.keySet()) {
            CommonController commonController = this.controllerMap.get(key);
            Callable<JClass> task = new GeneratorTask(this, commonController);
            FutureTask<JClass> future = new FutureTask<>(task);
            new Thread(future).start();
            try {
                this.clientClassMap.put(key, future.get());
            } catch (Exception e) {
                log.error(commonController.getClientName() + " client class add to map error:" + e.getMessage());
            }
        }

        this.waitForCodeGFinished(this.clientClassMap);
    }

    void composeRequestBodyField(PacketObject requestBody, ObjectNode jsonRequestBody) {
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

    }

    void composeRequestBody(PacketObject requestBody, ObjectNode jsonRequestBody) {
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

    void composeRequestBodyList(PacketObject restObject, ArrayNode jsonAy) {

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

    private class GeneratorTask implements Callable<JClass> {

        private SDKGenerator codeGenerator;

        private CommonController commonController;

        public GeneratorTask(SDKGenerator codeGenerator, CommonController commonController) {
            this.codeGenerator = codeGenerator;
            this.commonController = commonController;
        }

        public JClass call() {
            return codeGenerator.processSDKORClient(commonController);
        }

    }

}