package com.fastjrun.codeg.generator.method;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestMethod;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fastjrun.codeg.common.CommonController;
import com.fastjrun.codeg.common.PacketField;
import com.fastjrun.codeg.common.PacketObject;
import com.fastjrun.codeg.generator.BaseCMGenerator;
import com.fastjrun.codeg.generator.BaseControllerGenerator;
import com.fastjrun.codeg.processer.ExchangeProcessor;
import com.fastjrun.helper.StringHelper;
import com.fastjrun.utils.JacksonUtils;
import com.sun.codemodel.JAnnotationArrayMember;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JCatchBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JConditional;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JForLoop;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JTryBlock;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;

public abstract class BaseControllerMethodGenerator extends BaseCMGenerator {

    protected JMethod jClientMethod;

    protected JMethod jClientTestMethod;

    protected JMethod jcontrollerMethod;

    protected ObjectNode methodParamInJsonObject;

    protected ServiceMethodGenerator serviceMethodGenerator;

    protected BaseControllerGenerator baseControllerGenerator;

    protected ExchangeProcessor exchangeProcessor;

    public ServiceMethodGenerator getServiceMethodGenerator() {
        return serviceMethodGenerator;
    }

    public void setServiceMethodGenerator(ServiceMethodGenerator serviceMethodGenerator) {
        this.serviceMethodGenerator = serviceMethodGenerator;
    }

    public BaseControllerGenerator getBaseControllerGenerator() {
        return baseControllerGenerator;
    }

    public void setBaseControllerGenerator(BaseControllerGenerator baseControllerGenerator) {
        this.baseControllerGenerator = baseControllerGenerator;
    }

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

    public JMethod getJcontrollerMethod() {
        return jcontrollerMethod;
    }

    public void setJcontrollerMethod(JMethod jcontrollerMethod) {
        this.jcontrollerMethod = jcontrollerMethod;
    }

    public void processClientTestMethod(JDefinedClass clientTestClass) {
        JMethod clientTestMethod = clientTestClass.method(JMod.PUBLIC, cmTest.VOID,
                "test" + StringHelper.toUpperCaseFirstOne(this.serviceMethodGenerator.methodName));

        JAnnotationUse methodTestAnnotationTest = clientTestMethod
                .annotate(cmTest.ref("org.testng.annotations.Test"));
        JBlock methodTestBlk = clientTestMethod.body();

        methodTestAnnotationTest.param("dataProvider", "loadParam");

        JVar reqParamsJsonStrAndAssertJVar = clientTestMethod.param(cmTest.ref("String"), "reqParamsJsonStrAndAssert");
        JVar reqParamsJsonStrAndAssertArrayJVar = methodTestBlk
                .decl(cmTest.ref("String").array(), "reqParamsJsonStrAndAssertArray",
                        reqParamsJsonStrAndAssertJVar.invoke("split").arg(",assert="));
        JVar reqParamsJsonStrJVar = methodTestBlk
                .decl(cm.ref("String"), "reqParamsJsonStr", reqParamsJsonStrAndAssertArrayJVar.component(JExpr.lit(0)));
        methodTestBlk.invoke(JExpr.ref("log"), "debug").arg(reqParamsJsonStrJVar);
        JVar reqParamsJsonJVar = methodTestBlk.decl(cm.ref(JSONObjectClassName), "reqParamsJson", cmTest
                .ref(JacksonUtilsClassName).staticInvoke("toJsonNode")
                .arg(reqParamsJsonStrJVar));

        JInvocation jInvocationTest =
                JExpr.invoke(JExpr.ref("baseApplicationClient"), this.serviceMethodGenerator.methodName);

        // headParams
        List<PacketField> headVariables = this.serviceMethodGenerator.getCommonMethod().getHeadVariables();

        if (headVariables != null && headVariables.size() > 0) {
            for (int index = 0; index < headVariables.size(); index++) {
                PacketField headVariable = headVariables.get(index);
                jInvocationTest.arg(JExpr.ref(headVariable.getNameAlias()));
                processMethodCommonVariables(methodTestBlk, reqParamsJsonJVar, headVariable);
            }

        }

        List<PacketField> pathVariables = this.serviceMethodGenerator.getCommonMethod().getPathVariables();
        if (pathVariables != null && pathVariables.size() > 0) {
            for (int index = 0; index < pathVariables.size(); index++) {
                PacketField pathVariable = pathVariables.get(index);
                jInvocationTest.arg(JExpr.ref(pathVariable.getName()));
                processMethodCommonVariables(methodTestBlk, reqParamsJsonJVar, pathVariable);
            }
        }

        List<PacketField> parameters = this.serviceMethodGenerator.getCommonMethod().getParameters();
        if (parameters != null && parameters.size() > 0) {
            for (int index = 0; index < parameters.size(); index++) {
                PacketField parameter = parameters.get(index);
                jInvocationTest.arg(JExpr.ref(parameter.getName()));
                processMethodCommonVariables(methodTestBlk, reqParamsJsonJVar, parameter);
            }
        }

        List<PacketField> cookies = this.serviceMethodGenerator.getCommonMethod().getCookieVariables();
        if (cookies != null && cookies.size() > 0) {
            for (int index = 0; index < cookies.size(); index++) {
                PacketField cookie = cookies.get(index);
                jInvocationTest.arg(JExpr.ref(cookie.getName()));
                processMethodCommonVariables(methodTestBlk, reqParamsJsonJVar, cookie);
            }
        }
        if (this.serviceMethodGenerator.getRequestBodyClass() != null) {
            JVar requestBodyVar =
                    methodTestBlk.decl(this.serviceMethodGenerator.getRequestBodyClass(), "requestBody", JExpr._null());
            JVar requestBodyStrVar = methodTestBlk.decl(cm.ref(JSONObjectClassName), "reqJsonRequestBody",
                    reqParamsJsonJVar.invoke("get").arg(JExpr.lit("requestBody")));
            JBlock jNotNullBlock =
                    methodTestBlk._if(requestBodyStrVar.ne(JExpr._null()))._then();
            jNotNullBlock.assign(requestBodyVar, cm.ref(JacksonUtilsClassName).staticInvoke("readValue")
                    .arg(requestBodyStrVar.invoke("toString"))
                    .arg(((JClass) this.serviceMethodGenerator.getRequestBodyClass()).dotclass()));
            jInvocationTest.arg(requestBodyVar);
        }
        JVar assertJsonJVar = methodTestBlk.decl(cm.ref(JSONObjectClassName), "assertJson", JExpr._null());
        methodTestBlk._if(reqParamsJsonStrAndAssertArrayJVar.ref("length").eq(JExpr.lit(2)))._then().block()
                .assign(assertJsonJVar, cm.ref(JacksonUtilsClassName).staticInvoke("toJsonNode")
                        .arg(reqParamsJsonStrAndAssertArrayJVar.component(JExpr.lit(1))));
        if (this.serviceMethodGenerator.getResponseBodyClass() != cm.VOID) {
            JVar responseBodyVar =
                    methodTestBlk
                            .decl(this.serviceMethodGenerator.getResponseBodyClass(), "responseBody", JExpr._null());

            JConditional jConditional1 = methodTestBlk._if(assertJsonJVar.ne(JExpr._null()));
            JBlock jConditional1Block = jConditional1._then();
            JVar codeNodeJVar =
                    jConditional1Block
                            .decl(cm.ref(JSONObjectClassName), "codeNode", assertJsonJVar.invoke("get").arg("code"));
            JConditional jConditional2 = jConditional1Block._if(codeNodeJVar.ne(JExpr._null()));
            JBlock jConditional2ThenBlock = jConditional2._then();
            JTryBlock jTry = jConditional2ThenBlock._try();
            jTry.body().assign(responseBodyVar, jInvocationTest);
            JCatchBlock jCatchBlock = jTry._catch(cmTest.ref("com.fastjrun.common.ClientException"));
            JVar jExceptionVar = jCatchBlock.param("e");
            JBlock jCatchBlockBody = jCatchBlock.body();
            jCatchBlockBody.staticInvoke(cmTest.ref("org.testng.Assert"), "assertEquals")
                    .arg(jExceptionVar.invoke("getCode")).arg(codeNodeJVar.invoke("asText"))
                    .arg(JExpr.lit("返回消息码不是指定消息码：").plus(codeNodeJVar.invoke("asText")));
            jConditional2._else().assign(responseBodyVar, jInvocationTest);
            jConditional1._else().assign(responseBodyVar, jInvocationTest);

            methodTestBlk.invoke(JExpr.refthis("log"), "debug").arg(cm.ref(JacksonUtilsClassName).staticInvoke("toJSon")
                    .arg(responseBodyVar));
            JBlock ifBlock1 = methodTestBlk._if(responseBodyVar.ne(JExpr._null()))._then();
            if (this.serviceMethodGenerator.getCommonMethod().isResponseIsArray()) {
                JForLoop forLoop = ifBlock1._for();
                JVar initIndexVar = forLoop.init(cm.INT, "index", JExpr.lit(0));
                forLoop.test(initIndexVar.lt(responseBodyVar.invoke("size")));
                forLoop.update(initIndexVar.incr());
                JBlock forBlock1 = forLoop.body();
                JVar responseBodyIndexVar =
                        forBlock1.decl(this.serviceMethodGenerator.getElementClass(),
                                "item" + this.serviceMethodGenerator.getElementClass().name(),
                                responseBodyVar.invoke("get").arg(initIndexVar));

                this.logResponseBody(1, this.serviceMethodGenerator.getCommonMethod().getResponse(),
                        responseBodyIndexVar, forBlock1);

            } else {
                this.logResponseBody(1, this.serviceMethodGenerator.getCommonMethod().getResponse(), responseBodyVar,
                        ifBlock1);
            }
            ifBlock1.invoke(JExpr._this(), "processAssertion").arg(assertJsonJVar).arg(responseBodyVar)
                    .arg(JExpr.dotclass((JClass) this.serviceMethodGenerator.getResponseBodyClass()));

        } else {
            JConditional jConditional1 = methodTestBlk._if(assertJsonJVar.ne(JExpr._null()));
            JBlock jConditional1Block = jConditional1._then();
            JVar codeNodeJVar =
                    jConditional1Block
                            .decl(cm.ref(JSONObjectClassName), "codeNode", assertJsonJVar.invoke("get").arg("code"));
            JConditional jConditional2 = jConditional1Block._if(codeNodeJVar.ne(JExpr._null()));
            JBlock jConditional2ThenBlock = jConditional2._then();
            JTryBlock jTry = jConditional2ThenBlock._try();
            jTry.body().add(jInvocationTest);
            JCatchBlock jCatchBlock = jTry._catch(cmTest.ref("com.fastjrun.common.ClientException"));
            JVar jExceptionVar = jCatchBlock.param("e");
            JBlock jCatchBlockBody = jCatchBlock.body();
            jCatchBlockBody.staticInvoke(cmTest.ref("org.testng.Assert"), "assertEquals")
                    .arg(jExceptionVar.invoke("getCode")).arg(codeNodeJVar.invoke("asText"))
                    .arg(JExpr.lit("返回消息码不是指定消息码：").plus(codeNodeJVar.invoke("asText")));
            jConditional2._else().add(jInvocationTest);
            jConditional1._else().add(jInvocationTest);
        }
    }

    private void processMethodCommonVariables(JBlock methodTestBlk, JVar reqParamsJsonJVar, PacketField parameter) {
        JClass jType = cmTest.ref(parameter.getDatatype());
        String paramterName = parameter.getName();
        if (parameter.getNameAlias() != null && !parameter.getNameAlias().equals("")) {
            paramterName = parameter.getNameAlias();
        }
        JVar jVar = methodTestBlk.decl(jType, paramterName, JExpr._null());
        JVar jJsonVar = methodTestBlk.decl(cm.ref(JSONObjectClassName), paramterName + "jSon",
                reqParamsJsonJVar.invoke("get").arg(JExpr.lit(paramterName)));
        JBlock jNotNullBlock =
                methodTestBlk._if(jJsonVar.ne(JExpr._null()))
                        ._then();
        String jsonInvokeMethodName = "asText";
        switch (jType.name()) {
            case "Boolean":
                jsonInvokeMethodName = "asBoolean";
                break;
            case "Integer":
                jsonInvokeMethodName = "asInt";
                break;
            case "Long":
                jsonInvokeMethodName = "asLong";
                break;
            case "Double":
                jsonInvokeMethodName = "asDouble";
                break;
            default:
                break;
        }
        jNotNullBlock.assign(jVar, jJsonVar.invoke(jsonInvokeMethodName));
    }

    private void logResponseBodyField(int loopSeq, PacketObject responseBody, JVar responseBodyVar,
                                      JBlock methodTestBlk) {
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
                        log.error(fieldName + "'s length is assigned a wrong value", e);
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
                        .decl(jType, StringHelper.toLowerCaseFirstOne(responseBodyVar.type().name()) + loopSeq
                                        + tterMethodName,
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

    private void logResponseBody(int loopSeq, PacketObject responseBody, JVar responseBodyVar, JBlock methodTestBlk) {
        logResponseBodyField(loopSeq, responseBody, responseBodyVar, methodTestBlk);
        int start = 1;
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
                                responseBody.getName() + tterMethodName + loopSeq,
                                responseBodyVar.invoke("get" + tterMethodName));
                this.logResponseBody(loopSeq + start++, ro, reNameVar, methodTestBlk);
            }
        }
        Map<String, PacketObject> roLists = responseBody.getLists();
        if (roLists != null && roLists.size() > 0) {
            int index = 0;
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
                                responseBody.getName() + tterMethodName + "List" + loopSeq + index,
                                responseBodyVar.invoke("get" + tterMethodName));
                JBlock ifBlock1 = methodTestBlk._if(roListVar.ne(JExpr._null()))._then();
                JForLoop forLoop = ifBlock1._for();
                JVar initIndexVar =
                        forLoop.init(cmTest.INT,
                                responseBody.getName() + tterMethodName + "ListIndex" + loopSeq + index, JExpr.lit(0));
                forLoop.test(initIndexVar.lt(roListVar.invoke("size")));
                forLoop.update(initIndexVar.incr());
                JBlock forBlock1 = forLoop.body();
                JVar responseBodyIndexVar =
                        forBlock1.decl(roClass, roName + roClass
                                        .name(),
                                roListVar.invoke("get").arg(initIndexVar));

                this.logResponseBody(loopSeq + start++, ro, responseBodyIndexVar, forBlock1);
                index++;
            }
        }
    }

    public void processClientTestPraram() {

        this.methodParamInJsonObject = JacksonUtils.createObjectNode();

        // headParams
        List<PacketField> headVariables = this.serviceMethodGenerator.getCommonMethod().getHeadVariables();
        if (headVariables != null && headVariables.size() > 0) {
            for (int index = 0; index < headVariables.size(); index++) {
                PacketField headVariable = headVariables.get(index);
                methodParamInJsonObject.put(headVariable.getNameAlias(), headVariable.getDatatype());

            }

        }
        List<PacketField> pathVariables = this.serviceMethodGenerator.getCommonMethod().getPathVariables();
        if (pathVariables != null && pathVariables.size() > 0) {
            for (int index = 0; index < pathVariables.size(); index++) {
                PacketField pathVariable = pathVariables.get(index);
                methodParamInJsonObject.put(pathVariable.getName(), pathVariable.getDatatype());

            }
        }

        List<PacketField> parameters = this.serviceMethodGenerator.getCommonMethod().getParameters();
        if (parameters != null && parameters.size() > 0) {
            for (int index = 0; index < parameters.size(); index++) {
                PacketField parameter = parameters.get(index);
                methodParamInJsonObject.put(parameter.getName(), parameter.getDatatype());

            }
        }
        List<PacketField> cookies = this.serviceMethodGenerator.getCommonMethod().getCookieVariables();
        if (cookies != null && cookies.size() > 0) {
            for (int index = 0; index < cookies.size(); index++) {
                PacketField cookie = cookies.get(index);
                methodParamInJsonObject.put(cookie.getName(), cookie.getDatatype());

            }
        }

        if (this.serviceMethodGenerator.getRequestBodyClass() != null) {
            ObjectNode jsonRequestParam =
                    this.composeRequestBody(this.serviceMethodGenerator.getCommonMethod().getRequest());
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
        switch (this.serviceMethodGenerator.getCommonMethod().getHttpMethod().toUpperCase()) {
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
        this.jcontrollerMethod = controllerClass.method(JMod.PUBLIC, this.exchangeProcessor.getResponseClass(), this
                .serviceMethodGenerator.getMethodName());
        String methodRemark = this.serviceMethodGenerator.getCommonMethod().getRemark();
        this.jcontrollerMethod.javadoc().append(methodRemark);
        String methodPath = this.serviceMethodGenerator.getCommonMethod().getPath();
        if (methodPath == null || methodPath.equals("")) {
            methodPath = "/" + this.serviceMethodGenerator.getCommonMethod().getName();
        }
        String methodVersion = this.serviceMethodGenerator.getCommonMethod().getVersion();
        if (methodVersion != null && !methodVersion.equals("")) {
            methodPath = methodPath + "/" + methodVersion;
        }

        JBlock controllerMethodBlk = this.jcontrollerMethod.body();

        JInvocation jInvocation = JExpr.invoke(JExpr.refthis(commonController.getServiceName()), this
                .serviceMethodGenerator.getMethodName());

        methodPath = methodPath + this.exchangeProcessor.processHTTPRequest(jcontrollerMethod, jInvocation,
                mockModel, this.cm);

        if (this.getMockModel() == MockModel.MockModel_Swagger) {
            this.jcontrollerMethod.annotate(cm.ref("io.swagger.annotations.ApiOperation"))
                    .param("value", methodRemark).param("notes", methodRemark);
        }
        List<PacketField> headVariables = this.serviceMethodGenerator.getCommonMethod().getHeadVariables();
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
        List<PacketField> pathVariables = this.serviceMethodGenerator.getCommonMethod().getPathVariables();
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
        List<PacketField> parameters = this.serviceMethodGenerator.getCommonMethod().getParameters();
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

        String[] resTypes = this.serviceMethodGenerator.getCommonMethod().getResType().split(",");
        if (resTypes.length == 1) {
            jAnnotationUse.param("produces", resTypes[0]);
        } else {
            JAnnotationArrayMember jAnnotationArrayMember = jAnnotationUse.paramArray("produces");
            for (int i = 0; i < resTypes.length; i++) {
                jAnnotationArrayMember.param(resTypes[i]);
            }
        }

        List<PacketField> cookieVariables = this.serviceMethodGenerator.getCommonMethod().getCookieVariables();
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

        if (this.serviceMethodGenerator.getRequestBodyClass() != null) {
            JVar requestParam =
                    this.jcontrollerMethod.param(this.serviceMethodGenerator.getRequestBodyClass(), "requestBody");
            requestParam.annotate(cm.ref("org.springframework.web.bind.annotation.RequestBody"));
            requestParam.annotate(cm.ref("javax.validation.Valid"));
            jInvocation.arg(JExpr.ref("requestBody"));
        }

        this.processExtraParameters(jInvocation);

        this.exchangeProcessor.processResponse(controllerMethodBlk, jInvocation, this.cm);

    }

    abstract protected void processExtraParameters(JInvocation jInvocation);
}