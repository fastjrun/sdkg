package com.fastjrun.codeg.bundle;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import com.fastjrun.codeg.CodeGException;
import com.fastjrun.codeg.bundle.common.CommonController;
import com.fastjrun.codeg.bundle.common.CommonController.ControllerType;
import com.fastjrun.codeg.bundle.common.CommonMethod;
import com.fastjrun.codeg.bundle.common.CommonService;
import com.fastjrun.codeg.bundle.common.PacketField;
import com.fastjrun.codeg.bundle.common.PacketObject;
import com.fastjrun.codeg.helper.BundleXMLParser;
import com.fastjrun.codeg.helper.StringHelper;
import com.sun.codemodel.CodeWriter;
import com.sun.codemodel.JAnnotationArrayMember;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JCatchBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JForEach;
import com.sun.codemodel.JForLoop;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JTryBlock;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;
import com.sun.codemodel.writer.FileCodeWriter;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class SDKGenerator extends BundleGenerator {

    JClass jSONObjectClass = cmTest.ref("net.sf.json.JSONObject");
    JClass jSONArrayClass = cmTest.ref("net.sf.json.JSONArray");

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

            JClass StringClass = cm.ref("String");

            Properties clientProp = new Properties();
            ControllerType controllerType = commonController.getControllerType();
            String clientParent = commonController.getClientParent() == null ? "" : commonController.getClientParent();

            CommonService service = commonController.getService();
            JClass jParentClass;
            if (clientParent.equals("")) {
                if (controllerType == ControllerType.ControllerType_APP) {
                    jParentClass = cm.ref("com.fastjrun.client.BaseAppClient");
                } else if (controllerType == ControllerType.ControllerType_API) {
                    jParentClass = cm.ref("com.fastjrun.client.BaseApiClient");
                } else {
                    jParentClass = cm.ref("com.fastjrun.client.BaseGenericClient");
                }
            } else {
                jParentClass = cm.ref(commonController.getClientParent());
            }

            this.addClassDeclaration(clientClass);
            clientClass._extends(jParentClass);
            JClass dcService = serviceClassMap.get(service.getName());
            clientClass._implements(dcService);

            JDefinedClass clientTestClass = cmTest
                    ._class(this.packageNamePrefix + "client." + commonController.getClientName() + "Test");

            this.addClassDeclaration(clientTestClass);

            JFieldVar logVar = clientTestClass.field(JMod.NONE + JMod.FINAL,
                    cmTest.ref("org.apache.logging.log4j.Logger"), "log",
                    cmTest.ref("org.apache.logging.log4j.LogManager").staticInvoke("getLogger")
                            .arg(JExpr._this().invoke("getClass")));

            JFieldVar clientVar = clientTestClass.field(JMod.NONE, clientClass,
                    StringHelper.toLowerCaseFirstOne(commonController.getClientName()), JExpr._new(clientClass));
            JFieldVar propParamsVar = clientTestClass.field(JMod.NONE, cmTest.ref("java.util.Properties"), "propParams",
                    JExpr._new(cmTest.ref("java.util.Properties")));

            JMethod methodInit = clientTestClass.method(JMod.PUBLIC, cmTest.VOID, "init");
            methodInit.annotate(cmTest.ref("org.testng.annotations.BeforeTest"));
            methodInit.annotate(cmTest.ref("org.testng.annotations.Parameters")).paramArray("value").param("envName");

            JVar envNameVar = methodInit.param(cm.ref("String"), "envName");
            JBlock methodInitBlk = methodInit.body();
            methodInitBlk.invoke(clientVar, "initSDKConfig").arg(JExpr.lit(this.appName));
            JTryBlock firstTryBlock = methodInitBlk._try();
            JVar inParamVar = firstTryBlock.body().decl(cmTest.ref("java.io.InputStream"), "inParam",
                    JExpr.dotclass(clientClass).invoke("getResourceAsStream")
                            .arg(JExpr.lit("/testdata/").plus(envNameVar).plus(JExpr.lit(".properties"))));
            firstTryBlock.body().invoke(propParamsVar, "load").arg(inParamVar);
            JCatchBlock jcatchBlock = firstTryBlock._catch(cm.ref("java.io.IOException"));

            jcatchBlock.body().invoke(jcatchBlock.param("_x"), "printStackTrace");

            JType arrType = cmTest.ref("Object[][]");
            JMethod methodLoadParamTest = clientTestClass.method(JMod.PUBLIC, arrType, "loadParam");
            methodLoadParamTest.annotate(cmTest.ref("org.testng.annotations.DataProvider")).param("name", "loadParam");
            JBlock loadParamBlk = methodLoadParamTest.body();

            JVar methodTestVar = methodLoadParamTest.param(cmTest.ref("java.lang.reflect.Method"), "method");
            JVar keysVar = loadParamBlk.decl(cmTest.ref("java.util.Set").narrow(cmTest.ref("String")), "keys",
                    propParamsVar.invoke("stringPropertyNames"));
            JVar parametersVar = loadParamBlk.decl(cmTest.ref("java.util.List").narrow(cmTest.ref("String[]")),
                    "parameters", JExpr._new(cmTest.ref("java.util.ArrayList").narrow(cmTest.ref("String[]"))));
            JForEach forEach = loadParamBlk.forEach(cmTest.ref("String"), "key", keysVar);

            JVar keyVar = forEach.var();
            JBlock forIfBlkKeys = forEach.body()
                    ._if(keyVar.invoke("startsWith").arg(JExpr.lit(commonController.getClientName())
                            .plus(JExpr.lit(".")).plus(methodTestVar.invoke("getName").plus(JExpr.lit(".")))))
                    ._then();

            JVar valueVar = forIfBlkKeys.decl(cmTest.ref("String"), "value",
                    propParamsVar.invoke("getProperty").arg(keyVar));
            forIfBlkKeys.invoke(parametersVar, "add").arg(JExpr.newArray(cmTest.ref("String")).add(valueVar));

            JVar objectVar = loadParamBlk.decl(arrType, "object",
                    JExpr.newArray(cmTest.ref("Object").array(), parametersVar.invoke("size")));

            JForLoop forLoopI = loadParamBlk._for();
            JVar iVar = forLoopI.init(cmTest.INT, "i", JExpr.lit(0));
            forLoopI.test(iVar.lt(objectVar.ref("length")));
            forLoopI.update(iVar.incr());
            JBlock forBodyI = forLoopI.body();
            JVar strVar = forBodyI.decl(cmTest.ref("String").array(), "str", parametersVar.invoke("get").arg(iVar));
            forBodyI.assign(objectVar.component(iVar), JExpr.newArray(cmTest.ref("String"), strVar.ref("length")));
            JForLoop forLoopJ = forBodyI._for();
            JVar jVar = forLoopJ.init(cmTest.INT, "j", JExpr.lit(0));
            forLoopJ.test(jVar.lt(strVar.ref("length")));
            forLoopJ.update(jVar.incr());
            JBlock forBodyJ = forLoopJ.body();
            forBodyJ.assign(((JExpression) objectVar.component(iVar)).component(jVar), strVar.component(jVar));
            loadParamBlk._return(objectVar);

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

                JInvocation jInvocation = JExpr.invoke(clientVar, methodName);
                clientMethod.javadoc().append(methodRemark);

                String urlPre = "genericUrlPre";

                if (controllerType == ControllerType.ControllerType_API) {
                    urlPre = "apiUrlPre";
                } else if (controllerType == ControllerType.ControllerType_APP) {
                    urlPre = "appUrlPre";
                } else if (controllerType == ControllerType.ControllerType_RPC) {
                    urlPre = "genericUrlPre";
                }

                JBlock methodBlk = clientMethod.body();
                JClass stringBuilderClass = cm.ref("StringBuilder");

                JVar sbUrlReqVar = methodBlk.decl(stringBuilderClass, "sbUrlReq",
                        JExpr._new(stringBuilderClass).arg(JExpr.refthis(urlPre)));
                methodBlk.invoke(sbUrlReqVar, "append").arg(JExpr.lit(commonController.getPath()));

                methodBlk.invoke(sbUrlReqVar, "append").arg(JExpr.lit(methodPath));

                JMethod methodTest = clientTestClass.method(JMod.PUBLIC, cmTest.VOID,
                        "test" + StringHelper.toUpperCaseFirstOne(methodName));

                sbMethod.append(".test").append(StringHelper.toUpperCaseFirstOne(methodName)).append(".n");
                JAnnotationUse methodTestAnnotationTest = methodTest
                        .annotate(cmTest.ref("org.testng.annotations.Test"));
                JBlock methodTestBlk = methodTest.body();

                methodTestAnnotationTest.param("dataProvider", "loadParam");

                JAnnotationUse methodTestAnnotationParameters = methodTest
                        .annotate(cmTest.ref("org.testng.annotations.Parameters"));
                JAnnotationArrayMember parametersArrayMember = methodTestAnnotationParameters.paramArray("value");
                parametersArrayMember.param("reqParamsJsonStr");
                JVar reqParamsJsonStrJVar = methodTest.param(cm.ref("String"), "reqParamsJsonStr");
                methodTestBlk.invoke(logVar, "info").arg(reqParamsJsonStrJVar);
                JVar reqParamsJsonJVar = methodTestBlk.decl(jSONObjectClass, "reqParamsJson",
                        jSONObjectClass.staticInvoke("fromObject").arg(reqParamsJsonStrJVar));

                JSONObject methodParamInJsonObject = new JSONObject();

                methodBlk.invoke(sbUrlReqVar, "append").arg(JExpr._this().invoke("generateUrlSuffix"));
                JVar requestPropertiesJvar = methodBlk.decl(
                        cm.ref("java.util.Map").narrow(StringClass).narrow(StringClass), "requestProperties",
                        JExpr._new(cm.ref("java.util.HashMap").narrow(StringClass).narrow(StringClass)));
                methodBlk.invoke(requestPropertiesJvar, "put").arg("Content-Type").arg(method.getReqType());
                methodBlk.invoke(requestPropertiesJvar, "put").arg("User-Agent").arg(
                        "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) "
                                + "Chrome/56.0.2924.87 Safari/537.36");

                methodBlk.invoke(requestPropertiesJvar, "put").arg("Accept").arg("*/*");

                List<PacketField> headVariables = method.getHeadVariables();
                if (headVariables != null && headVariables.size() > 0) {
                    for (int index = 0; index < headVariables.size(); index++) {
                        PacketField headVariable = headVariables.get(index);
                        JClass jClass = cm.ref(headVariable.getDatatype());
                        clientMethod.param(jClass, headVariable.getNameAlias());
                        methodBlk.invoke(requestPropertiesJvar, "put").arg(JExpr.lit(headVariable.getName()))
                                .arg(JExpr.ref(headVariable.getNameAlias()));

                        jInvocation.arg(JExpr.ref(headVariable.getNameAlias()));
                        methodParamInJsonObject.put(headVariable.getNameAlias(), headVariable.getDatatype());
                        JClass jType = cmTest.ref(headVariable.getDatatype());
                        if (jType.name().endsWith("Boolean")) {
                            methodTestBlk
                                    .decl(jType, headVariable.getNameAlias(), reqParamsJsonJVar.invoke("getBoolean")
                                            .arg(JExpr.lit(headVariable.getNameAlias())));
                        } else if (jType.name().endsWith("Integer")) {
                            methodTestBlk.decl(jType, headVariable.getNameAlias(), reqParamsJsonJVar.invoke("getInt")
                                    .arg(JExpr.lit(headVariable.getNameAlias())));
                        } else if (jType.name().endsWith("Long")) {
                            methodTestBlk.decl(jType, headVariable.getNameAlias(), reqParamsJsonJVar.invoke("getLong")
                                    .arg(JExpr.lit(headVariable.getNameAlias())));
                        } else if (jType.name().endsWith("Double")) {
                            methodTestBlk.decl(jType, headVariable.getNameAlias(), reqParamsJsonJVar.invoke("getDouble")
                                    .arg(JExpr.lit(headVariable.getNameAlias())));
                        } else {
                            methodTestBlk.decl(jType, headVariable.getNameAlias(), reqParamsJsonJVar.invoke("getString")
                                    .arg(JExpr.lit(headVariable.getNameAlias())));
                        }

                    }
                }
                List<PacketField> pathVariables = method.getPathVariables();
                if (pathVariables != null && pathVariables.size() > 0) {
                    for (int index = 0; index < pathVariables.size(); index++) {
                        PacketField pathVariable = pathVariables.get(index);
                        JClass jClass = cm.ref(pathVariable.getDatatype());
                        clientMethod.param(jClass, pathVariable.getName());
                        methodBlk.invoke(sbUrlReqVar, "append").arg(JExpr.lit("/"));
                        methodBlk.invoke(sbUrlReqVar, "append").arg(JExpr.ref(pathVariable.getName()));

                        jInvocation.arg(JExpr.ref(pathVariable.getName()));
                        methodParamInJsonObject.put(pathVariable.getName(), pathVariable.getDatatype());
                        JClass jType = cmTest.ref(pathVariable.getDatatype());
                        if (jType.name().endsWith("Boolean")) {
                            methodTestBlk.decl(jType, pathVariable.getName(),
                                    reqParamsJsonJVar.invoke("getBoolean").arg(JExpr.lit(pathVariable.getName())));
                        } else if (jType.name().endsWith("Integer")) {
                            methodTestBlk.decl(jType, pathVariable.getName(),
                                    reqParamsJsonJVar.invoke("getInt").arg(JExpr.lit(pathVariable.getName())));
                        } else if (jType.name().endsWith("Long")) {
                            methodTestBlk.decl(jType, pathVariable.getName(),
                                    reqParamsJsonJVar.invoke("getLong").arg(JExpr.lit(pathVariable.getName())));
                        } else if (jType.name().endsWith("Double")) {
                            methodTestBlk.decl(jType, pathVariable.getName(),
                                    reqParamsJsonJVar.invoke("getDouble").arg(JExpr.lit(pathVariable.getName())));
                        } else {
                            methodTestBlk.decl(jType, pathVariable.getName(),
                                    reqParamsJsonJVar.invoke("getString").arg(JExpr.lit(pathVariable.getName())));
                        }

                    }
                }
                List<PacketField> parameters = method.getParameters();
                if (parameters != null && parameters.size() > 0) {
                    methodBlk.invoke(sbUrlReqVar, "append").arg(JExpr.lit("?"));
                    for (int index = 0; index < parameters.size(); index++) {
                        PacketField parameter = parameters.get(index);
                        JClass jClass = cm.ref(parameter.getDatatype());
                        clientMethod.param(jClass, parameter.getName());
                        if (index > 0) {
                            methodBlk.invoke(sbUrlReqVar, "append").arg(JExpr.lit("&"));
                        }
                        methodBlk.invoke(sbUrlReqVar, "append").arg(JExpr.lit(parameter.getName()));
                        methodBlk.invoke(sbUrlReqVar, "append").arg(JExpr.lit("="));
                        methodBlk.invoke(sbUrlReqVar, "append").arg(JExpr.ref(parameter.getName()));

                        jInvocation.arg(JExpr.ref(parameter.getName()));
                        methodParamInJsonObject.put(parameter.getName(), parameter.getDatatype());
                        JClass jType = cmTest.ref(parameter.getDatatype());
                        if (jType.name().endsWith("Boolean")) {
                            methodTestBlk.decl(jType, parameter.getName(),
                                    reqParamsJsonJVar.invoke("getBoolean").arg(JExpr.lit(parameter.getName())));
                        } else if (jType.name().endsWith("Integer")) {
                            methodTestBlk.decl(jType, parameter.getName(),
                                    reqParamsJsonJVar.invoke("getInt").arg(JExpr.lit(parameter.getName())));
                        } else if (jType.name().endsWith("Long")) {
                            methodTestBlk.decl(jType, parameter.getName(),
                                    reqParamsJsonJVar.invoke("getLong").arg(JExpr.lit(parameter.getName())));
                        } else if (jType.name().endsWith("Double")) {
                            methodTestBlk.decl(jType, parameter.getName(),
                                    reqParamsJsonJVar.invoke("getDouble").arg(JExpr.lit(parameter.getName())));
                        } else {
                            methodTestBlk.decl(jType, parameter.getName(),
                                    reqParamsJsonJVar.invoke("getString").arg(JExpr.lit(parameter.getName())));
                        }
                    }
                }

                PacketObject requestBody = method.getRequest();
                if (requestBody != null) {
                    JClass requestBodyClass = this.poClassMap.get(requestBody.get_class());
                    clientMethod.param(requestBodyClass, "requestBody");
                    JVar requestBodyStrVar = methodBlk.decl(cm.ref("String"), "requestBodyStr", jSONObjectClass
                            .staticInvoke("fromObject").arg(JExpr.ref("requestBody")).invoke("toString"));
                    methodBlk.invoke(logVar, "info").arg(requestBodyStrVar);
                    if (jResponseClass != cm.VOID) {
                        JVar responseJsonBodyVar = methodBlk.decl(jSONObjectClass, "responseBody",
                                JExpr.invoke(JExpr._this(), "parseResponseBody").arg(requestBodyStrVar)
                                        .arg(JExpr.invoke(sbUrlReqVar, "toString"))
                                        .arg(method.getHttpMethod().toUpperCase()).arg(requestPropertiesJvar));
                        JVar bodyVar = this.composeResponseBody(responseJsonBodyVar, methodBlk, response,
                                jResponseClass);
                        methodBlk._return(bodyVar);
                    } else {
                        methodBlk.invoke(JExpr._this(), "parseResponseBody").arg(requestBodyStrVar)
                                .arg(JExpr.invoke(sbUrlReqVar, "toString")).arg(method.getHttpMethod().toUpperCase())
                                .arg(requestPropertiesJvar);
                    }
                    JVar reqJsonRequestJVar = methodTestBlk.decl(jSONObjectClass, "reqJsonRequest",
                            reqParamsJsonJVar.invoke("getJSONObject").arg(JExpr.lit("requestBody")));

                    JSONObject jsonRequestParam = new JSONObject();
                    JVar reqeustBodyTestVar = this.composeRequestBody(reqJsonRequestJVar, methodTestBlk, requestBody,
                            requestBodyClass, jsonRequestParam);

                    jInvocation.arg(reqeustBodyTestVar);
                    methodParamInJsonObject.put("requestBody", jsonRequestParam);
                } else {
                    if (jResponseClass != cm.VOID) {
                        JVar responseJsonBodyVar = methodBlk.decl(jSONObjectClass, "responseBody",
                                JExpr.invoke(JExpr._this(), "parseResponseBody").arg(JExpr.lit(""))
                                        .arg(JExpr.invoke(sbUrlReqVar, "toString"))
                                        .arg(method.getHttpMethod().toUpperCase()).arg(requestPropertiesJvar));
                        JVar bodyVar = this.composeResponseBody(responseJsonBodyVar, methodBlk, response,
                                jResponseClass);
                        methodBlk._return(bodyVar);
                    } else {
                        methodBlk.invoke(JExpr._this(), "parseResponseBody").arg(JExpr.lit(""))
                                .arg(JExpr.invoke(sbUrlReqVar, "toString")).arg(method.getHttpMethod().toUpperCase())
                                .arg(requestPropertiesJvar);
                    }

                }

                JTryBlock secondTryBlock = methodTestBlk._try();
                if (jResponseClass != cm.VOID) {
                    secondTryBlock.body().decl(jResponseClass, "responseBody", jInvocation);
                    secondTryBlock.body().invoke(logVar, "info").arg(JExpr.ref("responseBody"));

                } else {
                    secondTryBlock.body().add(jInvocation);
                }
                JCatchBlock jsecondCatchBlock = secondTryBlock._catch(cm.ref("Exception"));
                jsecondCatchBlock.body().invoke(jsecondCatchBlock.param("_x"), "printStackTrace");
                clientProp.put(sbMethod.toString(),
                        methodParamInJsonObject.toString().replaceAll("\n", "").replaceAll("\r", "").trim());
            }
            try {
                FileWriter resFw = new FileWriter(outFile);
                clientProp.store(resFw, "ok");
                resFw.close();
            } catch (IOException e) {
                throw new CodeGException("CG505", commonController.getClientName() + " 测试数据模板文件生成失败:" + e.getMessage());
            }

            return clientClass;

        } catch (JClassAlreadyExistsException e) {
            throw new CodeGException("CG505", commonController.getClientName() + " create failed:" + e.getMessage());
        }

    }

    @Override
    public boolean generate() {
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
                log.error(commonController.getName() + " client class add to map error:" + e.getMessage());
            }
        }

        this.waitForCodeGFinished(this.clientClassMap);
    }

    JVar composeRequestBodyField(JVar reqJsonJVar, JBlock methodTestBlk, PacketObject requestBody,
                                 JClass requestBodyClass, JSONObject jsonRequestBody) {
        String varNamePrefix = StringHelper
                .toLowerCaseFirstOne(requestBody.get_class().substring(requestBody.get_class().lastIndexOf(".") + 1));
        JVar reqeustBodyTestVar = methodTestBlk.decl(requestBodyClass, varNamePrefix, JExpr._new(requestBodyClass));
        Map<String, PacketField> fields = requestBody.getFields();
        if (fields != null) {
            for (String fieldName : fields.keySet()) {
                PacketField restField = fields.get(fieldName);
                String dataType = restField.getDatatype();
                JClass jType = null;
                JClass primitiveType = null;
                if (dataType.endsWith(":List")) {
                    primitiveType = cmTest.ref(dataType.split(":")[0]);
                    jType = cmTest.ref("java.util.List").narrow(primitiveType);
                } else {
                    // Integer、Double、Long、Boolean、Character、Float
                    jType = cmTest.ref(dataType);
                }
                jsonRequestBody.put(fieldName, jType.name());

                String tterMethodName = fieldName;
                if (fieldName.length() > 1) {
                    String char2 = String.valueOf(fieldName.charAt(1));
                    if (!char2.equals(char2.toUpperCase())) {
                        tterMethodName = StringHelper.toUpperCaseFirstOne(fieldName);
                    }
                }

                if (primitiveType != null) {
                    JVar fieldNameJOVar = methodTestBlk.decl(jSONArrayClass, varNamePrefix + fieldName + "JA",
                            reqJsonJVar.invoke("getJSONArray").arg(fieldName));
                    JVar fieldNameVar = methodTestBlk.decl(jType, varNamePrefix + fieldName,
                            JExpr._new(cmTest.ref("java.util.ArrayList").narrow(primitiveType)));

                    JForLoop forLoopI = methodTestBlk._for();
                    JVar iVar = forLoopI.init(cmTest.INT, "i", JExpr.lit(0));
                    forLoopI.test(iVar.lt(fieldNameJOVar.invoke("size")));
                    forLoopI.update(iVar.incr());
                    JBlock forBodyI = forLoopI.body();
                    JVar valueVar;
                    if (primitiveType.name().endsWith("Boolean")) {
                        valueVar = forBodyI.decl(primitiveType, "value", fieldNameJOVar.invoke("getBoolean").arg(iVar));
                    } else if (primitiveType.name().endsWith("Integer")) {
                        valueVar = forBodyI.decl(primitiveType, "value", fieldNameJOVar.invoke("getInt").arg(iVar));
                    } else if (primitiveType.name().endsWith("Long")) {
                        valueVar = forBodyI.decl(primitiveType, "value", fieldNameJOVar.invoke("getLong").arg(iVar));
                    } else if (primitiveType.name().endsWith("Double")) {
                        valueVar = forBodyI.decl(primitiveType, "value", fieldNameJOVar.invoke("getDouble").arg(iVar));
                    } else {
                        valueVar = forBodyI.decl(primitiveType, "value", fieldNameJOVar.invoke("getString").arg(iVar));
                    }
                    forBodyI.invoke(fieldNameVar, "add").arg(primitiveType.staticInvoke("valueOf").arg(valueVar));

                    methodTestBlk
                            ._if(fieldNameVar.eq(JExpr._null()).not()
                                    .cand(fieldNameVar.invoke("size").eq(JExpr.lit(0)).not()))
                            ._then().block().invoke(reqeustBodyTestVar, "set" + tterMethodName).arg(fieldNameVar);
                } else {
                    JVar fieldValueVar = null;
                    if (jType.name().endsWith("Boolean")) {
                        fieldValueVar = methodTestBlk.decl(jType, varNamePrefix + fieldName,
                                reqJsonJVar.invoke("getBoolean").arg(fieldName));
                    } else if (jType.name().endsWith("Integer")) {
                        fieldValueVar = methodTestBlk
                                .decl(jType, varNamePrefix + fieldName, reqJsonJVar.invoke("getInt").arg(fieldName));
                    } else if (jType.name().endsWith("Long")) {
                        fieldValueVar = methodTestBlk
                                .decl(jType, varNamePrefix + fieldName, reqJsonJVar.invoke("getLong").arg(fieldName));
                    } else if (jType.name().endsWith("Double")) {
                        fieldValueVar = methodTestBlk
                                .decl(jType, varNamePrefix + fieldName, reqJsonJVar.invoke("getDouble").arg(fieldName));
                    } else {
                        fieldValueVar = methodTestBlk.decl(jType, varNamePrefix + fieldName,
                                reqJsonJVar.invoke("getString").arg(fieldName));
                    }

                    methodTestBlk
                            ._if(fieldValueVar.eq(JExpr._null()).not()
                                    .cand(fieldValueVar.invoke("equals").arg(JExpr.lit("")).not()))
                            ._then().block().invoke(reqeustBodyTestVar, "set" + tterMethodName).arg(fieldValueVar);
                }

            }
        }
        return reqeustBodyTestVar;

    }

    JVar composeRequestBody(JVar reqJsonVar, JBlock methodTestBlk, PacketObject requestBody, JClass requestBodyClass,
                            JSONObject jsonRequestBody) {
        JVar reqeustBodyTestVar = composeRequestBodyField(reqJsonVar, methodTestBlk, requestBody, requestBodyClass,
                jsonRequestBody);
        String varNamePrefix = StringHelper
                .toLowerCaseFirstOne(requestBodyClass.name().substring(requestBodyClass.name().lastIndexOf(".") + 1));
        Map<String, PacketObject> robjects = requestBody.getObjects();
        if (robjects != null && robjects.size() > 0) {
            for (String reName : robjects.keySet()) {
                PacketObject ro = robjects.get(reName);
                JClass roClass = cmTest.ref(this.packageNamePrefix + ro.get_class());
                JVar jObjectVar = methodTestBlk.decl(jSONObjectClass, varNamePrefix + roClass.name() + "RO",
                        reqJsonVar.invoke("getJSONObject").arg(reName));
                JSONObject jsonRe = new JSONObject();
                JVar roVar = this.composeRequestBody(jObjectVar, methodTestBlk, ro, roClass, jsonRe);

                String tterMethodName = reName;
                if (reName.length() > 1) {
                    String char2 = String.valueOf(reName.charAt(1));
                    if (!char2.equals(char2.toUpperCase())) {
                        tterMethodName = StringHelper.toUpperCaseFirstOne(reName);
                    }
                }
                methodTestBlk.invoke(reqeustBodyTestVar, "set" + tterMethodName).arg(roVar);
                jsonRequestBody.put(reName, jsonRe);
            }
        }
        Map<String, PacketObject> roList = requestBody.getLists();
        if (roList != null && roList.size() > 0) {
            for (String listName : roList.keySet()) {
                int index = 0;
                PacketObject ro = roList.get(listName);
                JClass roListEntityClass = cm.ref(this.packageNamePrefix + ro.get_class());
                JVar listJAVar = methodTestBlk.decl(jSONArrayClass,

                        varNamePrefix + roListEntityClass.name() + "JA",
                        reqJsonVar.invoke("getJSONArray").arg(listName));
                JVar listsVar = methodTestBlk.decl(cmTest.ref("java.util.List").narrow(roListEntityClass),
                        varNamePrefix + roListEntityClass.name() + "List",
                        JExpr._new(cmTest.ref("java.util.ArrayList").narrow(roListEntityClass)));

                JSONArray jsonAy = new JSONArray();
                JForLoop forLoop = methodTestBlk._for();
                JVar iVar = forLoop.init(cmTest.INT, varNamePrefix + "I" + String.valueOf(index++), JExpr.lit(0));
                forLoop.test(iVar.lt(listJAVar.invoke("size")));
                forLoop.update(iVar.incr());
                JBlock forBody = forLoop.body();
                JVar joVar = forBody.decl(jSONObjectClass, varNamePrefix + roListEntityClass.name() + "jo",
                        jSONObjectClass.staticInvoke("fromObject").arg(listJAVar.invoke("get").arg(iVar)));
                JVar roVar = composeRequestBodyList(joVar, forBody, ro, listName, roListEntityClass, jsonAy);
                forBody.invoke(listsVar, "add").arg(roVar);

                String tterMethodName = listName;
                if (listName.length() > 1) {
                    String char2 = String.valueOf(listName.charAt(1));
                    if (!char2.equals(char2.toUpperCase())) {
                        tterMethodName = StringHelper.toUpperCaseFirstOne(listName);
                    }
                }
                methodTestBlk.invoke(reqeustBodyTestVar, "set" + tterMethodName).arg(listsVar);
                jsonRequestBody.put(listName, jsonAy);
            }
        }
        return reqeustBodyTestVar;
    }

    JVar composeRequestBodyList(JVar joVar, JBlock methodBlk, PacketObject restObject, String listName, JClass roClass,
                                JSONArray jsonAy) {

        JSONObject jsonObject = new JSONObject();
        JVar roVar = composeRequestBodyField(joVar, methodBlk, restObject, roClass, jsonObject);
        String varNamePrefix = StringHelper
                .toLowerCaseFirstOne(roClass.name().substring(roClass.name().lastIndexOf(".") + 1));
        Map<String, PacketObject> robjects = restObject.getObjects();
        if (robjects != null && robjects.size() > 0) {
            for (String reName : robjects.keySet()) {
                PacketObject ro2 = robjects.get(reName);
                JClass ro2Class = cm.ref(this.packageNamePrefix + ro2.get_class());
                JVar jObject2Var = methodBlk.decl(jSONObjectClass, varNamePrefix + reName + "JO",
                        joVar.invoke("getJSONObject").arg(reName));
                JSONObject jsonRe = new JSONObject();

                JVar ro2Var = this.composeRequestBody(jObject2Var, methodBlk, ro2, ro2Class, jsonRe);
                String tterMethodName = reName;
                if (reName.length() > 1) {
                    String char2 = String.valueOf(reName.charAt(1));
                    if (!char2.equals(char2.toUpperCase())) {
                        tterMethodName = StringHelper.toUpperCaseFirstOne(reName);
                    }
                }
                methodBlk.invoke(roVar, "set" + tterMethodName).arg(ro2Var);
                jsonObject.put(reName, jsonRe);
            }
        }
        Map<String, PacketObject> roList = restObject.getLists();
        if (roList != null && roList.size() > 0) {
            for (String indexName : roList.keySet()) {
                PacketObject roEntity = roList.get(indexName);
                JClass roListEntityClass = cm.ref(this.packageNamePrefix + roEntity.get_class());
                JVar listJAVar = methodBlk.decl(jSONArrayClass,

                        varNamePrefix + roListEntityClass.name() + "JA", joVar.invoke("getJSONArray").arg(indexName));
                JVar listsVar = methodBlk.decl(cm.ref("java.util.List").narrow(roListEntityClass),
                        varNamePrefix + roListEntityClass.name() + "List",
                        JExpr._new(cm.ref("java.util.ArrayList").narrow(roListEntityClass)));
                JSONArray jsonAy2 = new JSONArray();
                JForLoop forLoop = methodBlk._for();
                JVar iVar = forLoop.init(cm.INT, varNamePrefix + roListEntityClass.name() + "I", JExpr.lit(0));
                forLoop.test(iVar.lt(listJAVar.invoke("size")));
                forLoop.update(iVar.incr());
                JBlock forBody = forLoop.body();
                JVar jo2Var = forBody.decl(jSONObjectClass, varNamePrefix + roListEntityClass.name() + "Jo",
                        jSONObjectClass.staticInvoke("fromObject").arg(listJAVar.invoke("get").arg(iVar)));
                JVar ro2Var = composeRequestBodyList(jo2Var, forBody, roEntity, indexName, roListEntityClass, jsonAy2);
                forBody.invoke(listsVar, "add").arg(ro2Var);

                String tterMethodName = indexName;
                if (indexName.length() > 1) {
                    String char2 = String.valueOf(indexName.charAt(1));
                    if (!char2.equals(char2.toUpperCase())) {
                        tterMethodName = StringHelper.toUpperCaseFirstOne(indexName);
                    }
                }

                methodBlk.invoke(roVar, "set" + tterMethodName).arg(listsVar);

                jsonObject.put(indexName, jsonAy2);
            }

        }

        jsonAy.add(jsonObject);
        return roVar;

    }

    JVar composeResponseBody(JVar resJsonVar, JBlock methodBlk, PacketObject responseBody, JType responseBodyClass) {
        JVar responseBodyVar = composeResponseBodyField(resJsonVar, methodBlk, responseBody, responseBodyClass);
        String varNamePrefix = StringHelper
                .toLowerCaseFirstOne(responseBodyClass.name().substring(responseBodyClass.name().lastIndexOf(".") + 1));
        Map<String, PacketObject> robjects = responseBody.getObjects();
        if (robjects != null && robjects.size() > 0) {
            for (String reName : robjects.keySet()) {
                PacketObject ro = robjects.get(reName);
                JClass roClass = cm.ref(this.packageNamePrefix + ro.get_class());
                JVar jObjectVar = methodBlk.decl(jSONObjectClass, varNamePrefix + roClass.name() + "JO",
                        resJsonVar.invoke("getJSONObject").arg(reName));
                JVar roVar = this.composeResponseBody(jObjectVar, methodBlk, ro, roClass);

                String tterMethodName = reName;
                if (reName.length() > 1) {
                    String char2 = String.valueOf(reName.charAt(1));
                    if (!char2.equals(char2.toUpperCase())) {
                        tterMethodName = StringHelper.toUpperCaseFirstOne(reName);
                    }
                }
                methodBlk.invoke(responseBodyVar, "set" + tterMethodName).arg(roVar);
            }
        }
        Map<String, PacketObject> roList = responseBody.getLists();
        if (roList != null && roList.size() > 0) {
            for (String listName : roList.keySet()) {
                int index = 0;
                PacketObject ro = roList.get(listName);
                JClass roListEntityClass = cm.ref(this.packageNamePrefix + ro.get_class());
                JVar listJAVar = methodBlk.decl(jSONArrayClass, varNamePrefix + roListEntityClass.name() + "JA",
                        resJsonVar.invoke("getJSONArray").arg(listName));
                JVar listsVar = methodBlk.decl(cm.ref("java.util.List").narrow(roListEntityClass),
                        varNamePrefix + roListEntityClass.name() + "list",
                        JExpr._new(cm.ref("java.util.ArrayList").narrow(roListEntityClass)));
                JForLoop forLoop = methodBlk._for();
                JVar iVar = forLoop.init(cm.INT, varNamePrefix + "I" + String.valueOf(index++), JExpr.lit(0));
                forLoop.test(iVar.lt(listJAVar.invoke("size")));
                forLoop.update(iVar.incr());
                JBlock forBody = forLoop.body();
                JVar joVar = forBody.decl(jSONObjectClass, varNamePrefix + roListEntityClass.name() + "jo",
                        jSONObjectClass.staticInvoke("fromObject").arg(listJAVar.invoke("get").arg(iVar)));
                JVar roVar = composeResponseList(1, joVar, forBody, ro, listName, roListEntityClass);
                forBody.invoke(listsVar, "add").arg(roVar);
                String tterMethodName = listName;
                if (listName.length() > 1) {
                    String char2 = String.valueOf(listName.charAt(1));
                    if (!char2.equals(char2.toUpperCase())) {
                        tterMethodName = StringHelper.toUpperCaseFirstOne(listName);
                    }
                }
                methodBlk.invoke(responseBodyVar, "set" + tterMethodName).arg(listsVar);
            }
        }
        return responseBodyVar;
    }

    JVar composeResponseList(int loopSeq, JVar joVar, JBlock methodBlk, PacketObject restObject, String listName,
                             JClass roClass) {

        JVar roVar = composeResponseBodyField(joVar, methodBlk, restObject, roClass);
        String varNamePrefix = StringHelper
                .toLowerCaseFirstOne(roClass.name().substring(roClass.name().lastIndexOf(".") + 1));
        Map<String, PacketObject> robjects = restObject.getObjects();
        if (robjects != null && robjects.size() > 0) {
            for (String reName : robjects.keySet()) {
                PacketObject ro2 = robjects.get(reName);
                JClass ro2Class = cm.ref(this.packageNamePrefix + ro2.get_class());
                JVar jObject2Var = methodBlk.decl(jSONObjectClass, varNamePrefix + reName + "JO",
                        joVar.invoke("getJSONObject").arg(reName));
                JVar ro2Var = this.composeResponseBody(jObject2Var, methodBlk, ro2, ro2Class);
                String tterMethodName = reName;
                if (reName.length() > 1) {
                    String char2 = String.valueOf(reName.charAt(1));
                    if (!char2.equals(char2.toUpperCase())) {
                        tterMethodName = StringHelper.toUpperCaseFirstOne(reName);
                    }
                }
                methodBlk.invoke(roVar, "set" + tterMethodName).arg(ro2Var);
            }
        }
        Map<String, PacketObject> roList = restObject.getLists();
        if (roList != null && roList.size() > 0) {
            for (String indexName : roList.keySet()) {
                PacketObject roEntity = roList.get(indexName);
                JClass roListEntityClass = cm.ref(this.packageNamePrefix + roEntity.get_class());
                JVar listJAVar = methodBlk.decl(jSONArrayClass,

                        varNamePrefix + roListEntityClass.name() + "JA", joVar.invoke("getJSONArray").arg(indexName));
                JVar listsVar = methodBlk.decl(cm.ref("java.util.List").narrow(roListEntityClass),
                        varNamePrefix + "List" + loopSeq,
                        JExpr._new(cm.ref("java.util.ArrayList").narrow(roListEntityClass)));
                JForLoop forLoop = methodBlk._for();
                JVar iVar = forLoop.init(cmTest.INT, varNamePrefix + roListEntityClass.name() + "I", JExpr.lit(0));
                forLoop.test(iVar.lt(listJAVar.invoke("size")));
                forLoop.update(iVar.incr());
                JBlock forBody = forLoop.body();
                JVar jo2Var = forBody.decl(jSONObjectClass, varNamePrefix + roListEntityClass.name() + "Jo",
                        jSONObjectClass.staticInvoke("fromObject").arg(listJAVar.invoke("get").arg(iVar)));
                JVar ro2Var = composeResponseList(loopSeq++, jo2Var, forBody, roEntity, indexName, roListEntityClass);
                forBody.invoke(listsVar, "add").arg(ro2Var);

                String tterMethodName = indexName;
                if (indexName.length() > 1) {
                    String char2 = String.valueOf(indexName.charAt(1));
                    if (!char2.equals(char2.toUpperCase())) {
                        tterMethodName = StringHelper.toUpperCaseFirstOne(indexName);
                    }
                }

                methodBlk.invoke(roVar, "set" + tterMethodName).arg(listsVar);
            }

        }
        return roVar;

    }

    JVar composeResponseBodyField(JVar joVar, JBlock methodBlk, PacketObject responseBody, JType responseBodyClass) {
        String varNamePrefix = StringHelper
                .toLowerCaseFirstOne(responseBody.get_class().substring(responseBody.get_class().lastIndexOf(".") + 1));
        JVar responseBodyVar = methodBlk.decl(responseBodyClass, varNamePrefix, JExpr._new(responseBodyClass));
        Map<String, PacketField> restFields = responseBody.getFields();
        if (restFields != null) {
            for (String fieldName : restFields.keySet()) {
                PacketField restField = restFields.get(fieldName);
                String dataType = restField.getDatatype();
                JType jType;
                JClass primitiveType = null;
                if (dataType.endsWith(":List")) {
                    primitiveType = cmTest.ref(dataType.split(":")[0]);
                    jType = cmTest.ref("java.util.List").narrow(primitiveType);
                } else {
                    // Integer、Double、Long、Boolean、Character、Float
                    jType = cmTest.ref(dataType);
                }

                String tterMethodName = fieldName;
                if (fieldName.length() > 1) {
                    String char2 = String.valueOf(fieldName.charAt(1));
                    if (!char2.equals(char2.toUpperCase())) {
                        tterMethodName = StringHelper.toUpperCaseFirstOne(fieldName);
                    }
                }
                JVar fieldValueVar = null;
                if (primitiveType != null) {
                    JVar fieldNameJAVar = methodBlk.decl(jSONArrayClass, varNamePrefix + fieldName + "JA",
                            joVar.invoke("getJSONArray").arg(fieldName));
                    JVar fieldNameVar = methodBlk.decl(cm.ref("java.util.List").narrow(primitiveType), fieldName,
                            JExpr._new(cm.ref("java.util.ArrayList").narrow(primitiveType)));

                    JForLoop forLoopI = methodBlk._for();
                    JVar iVar = forLoopI.init(cmTest.INT, "i", JExpr.lit(0));
                    forLoopI.test(iVar.lt(fieldNameJAVar.invoke("size")));
                    forLoopI.update(iVar.incr());
                    JBlock forBodyI = forLoopI.body();
                    JVar valueVar;
                    if (primitiveType.name().endsWith("Boolean")) {
                        valueVar = forBodyI.decl(primitiveType, "value", fieldNameJAVar.invoke("getBoolean").arg(iVar));
                    } else if (primitiveType.name().endsWith("Integer")) {
                        valueVar = forBodyI.decl(primitiveType, "value", fieldNameJAVar.invoke("getInt").arg(iVar));
                    } else if (primitiveType.name().endsWith("Long")) {
                        valueVar = forBodyI.decl(primitiveType, "value", fieldNameJAVar.invoke("getLong").arg(iVar));
                    } else if (primitiveType.name().endsWith("Double")) {
                        valueVar = forBodyI.decl(primitiveType, "value", fieldNameJAVar.invoke("getDouble").arg(iVar));
                    } else {
                        valueVar = forBodyI.decl(primitiveType, "value", fieldNameJAVar.invoke("getString").arg(iVar));
                    }
                    forBodyI.invoke(fieldNameVar, "add").arg(primitiveType.staticInvoke("valueOf").arg(valueVar));
                    methodBlk
                            ._if(fieldNameVar.eq(JExpr._null()).not()
                                    .cand(fieldNameVar.invoke("size").eq(JExpr.lit(0)).not()))
                            ._then().block().invoke(responseBodyVar, "set" + tterMethodName).arg(fieldNameVar);
                } else {

                    if (jType.name().endsWith("Boolean")) {
                        fieldValueVar = methodBlk
                                .decl(jType, varNamePrefix + fieldName, joVar.invoke("getBoolean").arg(fieldName));
                    } else if (jType.name().endsWith("Integer")) {
                        fieldValueVar =
                                methodBlk.decl(jType, varNamePrefix + fieldName, joVar.invoke("getInt").arg(fieldName));
                    } else if (jType.name().endsWith("Long")) {
                        fieldValueVar = methodBlk
                                .decl(jType, varNamePrefix + fieldName, joVar.invoke("getLong").arg(fieldName));
                    } else if (jType.name().endsWith("Double")) {
                        fieldValueVar = methodBlk
                                .decl(jType, varNamePrefix + fieldName, joVar.invoke("getDouble").arg(fieldName));
                    } else {
                        fieldValueVar = methodBlk.decl(jType, varNamePrefix + fieldName,
                                joVar.invoke("getString").arg(fieldName));
                    }

                    methodBlk
                            ._if(fieldValueVar.eq(JExpr._null()).not()
                                    .cand(fieldValueVar.invoke("equals").arg(JExpr.lit("")).not()))
                            ._then().block().invoke(responseBodyVar, "set" + tterMethodName).arg(fieldValueVar);
                }

            }
        }
        return responseBodyVar;

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