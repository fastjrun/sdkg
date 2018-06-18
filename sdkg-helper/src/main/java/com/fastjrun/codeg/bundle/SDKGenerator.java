package com.fastjrun.codeg.bundle;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import com.fastjrun.codeg.CodeGException;
import com.fastjrun.codeg.bundle.common.RestController;
import com.fastjrun.codeg.bundle.common.RestField;
import com.fastjrun.codeg.bundle.common.RestObject;
import com.fastjrun.codeg.bundle.common.RestService;
import com.fastjrun.codeg.bundle.common.RestServiceMethod;
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

public class SDKGenerator extends PacketGenerator {

	JClass jSONObjectClass = cmTest.ref("net.sf.json.JSONObject");
	JClass jSONArrayClass = cmTest.ref("net.sf.json.JSONArray");

	private String appName;

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	/**
	 * @param rest
	 * @param restPacketMap
	 * @param prefix
	 *            App:为App提供接口;Api:为其他系统提供接口;Generic：普通接口
	 */
	private boolean processClient(RestController rest, Map<String, RestObject> restPacketMap, String prefix) {
		try {
			File outFile = new File(
					this.moduleName + TESTDATANAME + File.separator + rest.getClientName() + ".properties");
			outFile.getParentFile().mkdirs();
			if (outFile.exists()) {
				outFile.delete();
			}
			
			Properties restProp = new Properties();
			JDefinedClass clientClass = cm._class(this.packageNamePrefix + "client." + rest.getClientName());

			this.addClassDeclaration(clientClass);
			clientClass._extends(cm.ref(rest.getClientParent()));

			JDefinedClass clientTestClass = cmTest
					._class(this.packageNamePrefix + "client." + rest.getClientName() + "Test");

			this.addClassDeclaration(clientTestClass);

			JFieldVar logVar = clientTestClass.field(JMod.NONE + JMod.FINAL,
					cmTest.ref("org.apache.logging.log4j.Logger"), "log",
					cmTest.ref("org.apache.logging.log4j.LogManager").staticInvoke("getLogger")
							.arg(JExpr._this().invoke("getClass")));

			JFieldVar clientVar = clientTestClass.field(JMod.NONE, clientClass,
					StringHelper.toLowerCaseFirstOne(rest.getClientName()), JExpr._new(clientClass));
			JFieldVar propParamsVar = clientTestClass.field(JMod.NONE, cmTest.ref("java.util.Properties"), "propParams",
					JExpr._new(cmTest.ref("java.util.Properties")));

			JMethod methodInit = clientTestClass.method(JMod.PUBLIC, cmTest.VOID, "init");
			methodInit.annotate(cmTest.ref("org.testng.annotations.BeforeTest"));
			methodInit.annotate(cmTest.ref("org.testng.annotations.Parameters")).paramArray("value").param("envName");
			;

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

			// 测试类方法
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
			JBlock forIfBlkKeys = forEach.body()._if(keyVar.invoke("startsWith").arg(JExpr.lit(rest.getClientName())
					.plus(JExpr.lit(".")).plus(methodTestVar.invoke("getName").plus(JExpr.lit(".")))))._then();

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

			RestService restService = rest.getRestService();
			List<RestServiceMethod> restServiceMethods = restService.getMethods();
			for (RestServiceMethod restServiceMethod : restServiceMethods) {
				StringBuilder sbMethod = new StringBuilder("#").append(rest.getClientName());
				sbMethod.append(".").append(restServiceMethod.getName()).append(restServiceMethod.getVersion())
						.append(".n");
				RestObject request = restServiceMethod.getRequest();
				RestObject requestBody = request;
				JClass requestBodyClass = null;
				if (request != null) {
					if (prefix.equals("Generic")) {
						requestBodyClass = cm.ref(this.packageNamePrefix + request.get_class());
					} else {
						requestBody = request.getObjects().get("body");
						if (requestBody != null) {
							requestBodyClass = cm.ref(this.packageNamePrefix + requestBody.get_class());

						}
					}
				}

				RestObject response = restServiceMethod.getResponse();
				RestObject responseBody = response;
				JType responseBodyClass = null;
				if (response != null) {
					if (prefix.equals("Generic")) {
						responseBodyClass = cm.ref(this.packageNamePrefix + response.get_class());
					} else {
						responseBody = response.getObjects().get("body");
						if (responseBody == null) {
							responseBodyClass = cm.VOID;
						} else {
							responseBodyClass = cm.ref(this.packageNamePrefix + responseBody.get_class());
						}
					}
				} else {
					responseBodyClass = cm.VOID;
				}
				JMethod method = clientClass.method(JMod.PUBLIC, responseBodyClass,
						restServiceMethod.getName() + restServiceMethod.getVersion());
				String urlPre = "genericUrlPre";
				String argRequest = "requestBody";
				String argResponse = "responseBody";
				String requestBodyClassName = "";
				String responseBodyClassName = "";
				if (prefix.equals("Api")) {
					urlPre = "apiUrlPre";
					if (request != null) {
						requestBodyClassName = requestBody.get_class();
						method.param(requestBodyClass, argRequest);
					}

					method.param(cm.ref("String"), "accessKey");
					if (response != null) {
						responseBodyClassName = responseBody.get_class();
					}
				} else if (prefix.equals("App")) {
					urlPre = "appUrlPre";
					if (request != null) {
						requestBodyClassName = requestBody.get_class();
						method.param(requestBodyClass, argRequest);
					}
					method.param(cm.ref("String"), "appKey");
					method.param(cm.ref("String"), "appVersion");
					method.param(cm.ref("String"), "appSource");
					method.param(cm.ref("String"), "deviceId");
					if (response != null) {
						responseBodyClassName = responseBody.get_class();
					}
				} else {
					argRequest = "request";
					argResponse = "response";
					if (request != null) {
						requestBodyClassName = request.get_class();
						method.param(requestBodyClass, argRequest);
					}

					List<RestField> headVariables = restServiceMethod.getHeadVariables();
					if (headVariables != null && headVariables.size() > 0) {
						for (int index = 0; index < headVariables.size(); index++) {
							RestField headVariable = headVariables.get(index);
							JClass jClass = cm.ref(headVariable.getDatatype());
							method.param(jClass, headVariable.getNameAlias());
						}
					}
					List<RestField> pathVariables = restServiceMethod.getPathVariables();
					if (pathVariables != null && pathVariables.size() > 0) {
						for (int index = 0; index < pathVariables.size(); index++) {
							RestField pathVariable = pathVariables.get(index);
							JClass jClass = cm.ref(pathVariable.getDatatype());
							method.param(jClass, pathVariable.getName());
						}
					}
					Map<String, RestField> parameters = restServiceMethod.getParameters();
					if (parameters != null && parameters.size() > 0) {
						for (String key : parameters.keySet()) {
							RestField parameter = parameters.get(key);
							JClass jClass = cm.ref(parameter.getDatatype());
							method.param(jClass, parameter.getName());
						}
					}
					if (response != null) {
						responseBodyClassName = response.get_class();
					}
				}

				JBlock methodBlk = method.body();
				JClass stringBuilderClass = cm.ref("StringBuilder");

				JVar sbUrlReqVar = methodBlk.decl(stringBuilderClass, "sbUrlReq",
						JExpr._new(stringBuilderClass).arg(JExpr.refthis(urlPre)));
				methodBlk.invoke(sbUrlReqVar, "append").arg(JExpr.lit(rest.getPath()));
				String versionInMethodValue = restServiceMethod.getVersion();
				if (!versionInMethodValue.equals("")) {
					versionInMethodValue = "/" + restServiceMethod.getVersion();
				}
				methodBlk.invoke(sbUrlReqVar, "append")
						.arg(JExpr.lit(restServiceMethod.getPath() + versionInMethodValue));
				if (prefix.equals("Api")) {
					methodBlk.invoke(sbUrlReqVar, "append").arg(JExpr.lit("/"));
					methodBlk.invoke(sbUrlReqVar, "append").arg(JExpr.ref("accessKey"));
					methodBlk.invoke(sbUrlReqVar, "append").arg(JExpr.lit("/"));
					JVar txTimeVar = methodBlk.decl(cm.LONG, "txTime",
							cm.ref("System").staticInvoke("currentTimeMillis"));
					methodBlk.invoke(sbUrlReqVar, "append").arg(txTimeVar);
					methodBlk.invoke(sbUrlReqVar, "append").arg(JExpr.lit("/"));
					JTryBlock jtry1 = methodBlk._try();
					JVar md5HashVar = jtry1.body().decl(cm.ref("String"), "md5Hash",
							cm.ref("com.fastjrun.helper.EncryptHelper").staticInvoke("md5Digest")
									.arg(JExpr._this().invoke("getAccessKeySn").plus(txTimeVar)));
					jtry1.body().invoke(sbUrlReqVar, "append").arg(md5HashVar);
					JCatchBlock jTryCatchBlock = jtry1._catch(cm.ref("Exception"));
					JVar eVar = jTryCatchBlock.param("e");
					jTryCatchBlock.body().invoke(JExpr.refthis("log"), "warn").arg("").arg(eVar);

				} else if (prefix.equals("App")) {
					methodBlk.invoke(sbUrlReqVar, "append").arg(JExpr.lit("/"));
					methodBlk.invoke(sbUrlReqVar, "append").arg(JExpr.ref("appKey"));
					methodBlk.invoke(sbUrlReqVar, "append").arg(JExpr.lit("/"));
					methodBlk.invoke(sbUrlReqVar, "append").arg(JExpr.ref("appVersion"));
					methodBlk.invoke(sbUrlReqVar, "append").arg(JExpr.lit("/"));
					methodBlk.invoke(sbUrlReqVar, "append").arg(JExpr.ref("appSource"));
					methodBlk.invoke(sbUrlReqVar, "append").arg(JExpr.lit("/"));
					methodBlk.invoke(sbUrlReqVar, "append").arg(JExpr.ref("deviceId"));
					methodBlk.invoke(sbUrlReqVar, "append").arg(JExpr.lit("/"));
					JVar txTimeVar = methodBlk.decl(cm.LONG, "txTime",
							cm.ref("System").staticInvoke("currentTimeMillis"));
					methodBlk.invoke(sbUrlReqVar, "append").arg(txTimeVar);
				} else {
					List<RestField> pathVariables = restServiceMethod.getPathVariables();
					if (pathVariables != null && pathVariables.size() > 0) {
						for (int index = 0; index < pathVariables.size(); index++) {
							RestField pathVariable = pathVariables.get(index);
							methodBlk.invoke(sbUrlReqVar, "append").arg(JExpr.lit("/"));
							methodBlk.invoke(sbUrlReqVar, "append").arg(JExpr.ref(pathVariable.getName()));
						}
					}
					Map<String, RestField> parameters = restServiceMethod.getParameters();
					if (parameters != null && parameters.size() > 0) {
						methodBlk.invoke(sbUrlReqVar, "append").arg(JExpr.lit("?"));
						int index = 0;
						for (String key : parameters.keySet()) {
							RestField parameter = parameters.get(key);
							if (index > 0) {
								methodBlk.invoke(sbUrlReqVar, "append").arg(JExpr.lit("&"));
							}
							index++;
							methodBlk.invoke(sbUrlReqVar, "append").arg(JExpr.lit(parameter.getName()));
							methodBlk.invoke(sbUrlReqVar, "append").arg(JExpr.lit("="));
							methodBlk.invoke(sbUrlReqVar, "append").arg(JExpr.ref(parameter.getName()));

						}
					}
				}

				JVar requestPropertiesJvar = methodBlk.decl(
						cm.ref("java.util.Map").narrow(String.class).narrow(String.class), "requestProperties",
						JExpr._new(cm.ref("java.util.HashMap").narrow(String.class).narrow(String.class)));
				methodBlk.invoke(requestPropertiesJvar, "put").arg("Content-Type").arg("application/json");
				methodBlk.invoke(requestPropertiesJvar, "put").arg("User-Agent").arg(
						"Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36");

				methodBlk.invoke(requestPropertiesJvar, "put").arg("Accept").arg("*/*");
				List<RestField> headVariables = restServiceMethod.getHeadVariables();
				if (headVariables != null && headVariables.size() > 0) {
					for (int index = 0; index < headVariables.size(); index++) {
						RestField pathVariable = headVariables.get(index);
						methodBlk.invoke(requestPropertiesJvar, "put").arg(JExpr.lit(pathVariable.getName()))
								.arg(JExpr.ref(pathVariable.getNameAlias()));
					}
				}
				if (requestBodyClassName != null && !requestBodyClassName.equals("")) {
					JVar requestStrVar = methodBlk.decl(cm.ref("String"), "requestStr",
							jSONObjectClass.staticInvoke("fromObject").arg(JExpr.ref(argRequest)).invoke("toString"));
					methodBlk.invoke(logVar, "info").arg(requestStrVar);
					if (responseBodyClass != cm.VOID) {
						JVar responseJsonBodyVar = methodBlk.decl(jSONObjectClass, argResponse,
								JExpr.invoke(JExpr._this(), "process").arg(requestStrVar)
										.arg(JExpr.invoke(sbUrlReqVar, "toString"))
										.arg(restServiceMethod.getHttpMethod().toUpperCase())
										.arg(requestPropertiesJvar));
						JVar bodyVar = this.composeResponseBody(responseJsonBodyVar, methodBlk,
								restPacketMap.get(responseBodyClassName), responseBodyClass);
						methodBlk._return(bodyVar);
					} else {
						methodBlk.invoke(JExpr._this(), "process").arg(requestStrVar)
								.arg(JExpr.invoke(sbUrlReqVar, "toString"))
								.arg(restServiceMethod.getHttpMethod().toUpperCase()).arg(requestPropertiesJvar);
					}

				} else {
					if (responseBodyClass != cm.VOID) {
						JVar responseJsonBodyVar = methodBlk.decl(jSONObjectClass, argResponse,
								JExpr.invoke(JExpr._this(), "process").arg(JExpr.lit(""))
										.arg(JExpr.invoke(sbUrlReqVar, "toString"))
										.arg(restServiceMethod.getHttpMethod().toUpperCase())
										.arg(requestPropertiesJvar));
						JVar bodyVar = this.composeResponseBody(responseJsonBodyVar, methodBlk,
								restPacketMap.get(responseBodyClassName), responseBodyClass);
						methodBlk._return(bodyVar);
					} else {
						methodBlk.invoke(JExpr._this(), "process").arg(JExpr.lit(""))
								.arg(JExpr.invoke(sbUrlReqVar, "toString"))
								.arg(restServiceMethod.getHttpMethod().toUpperCase()).arg(requestPropertiesJvar);
					}
				}

				JMethod methodTest = clientTestClass.method(JMod.PUBLIC, cmTest.VOID,
						restServiceMethod.getName() + restServiceMethod.getVersion());

				JInvocation jInvocation = JExpr.invoke(clientVar,
						restServiceMethod.getName() + restServiceMethod.getVersion());

				JAnnotationUse methodTestAnnotationTest = methodTest
						.annotate(cmTest.ref("org.testng.annotations.Test"));
				JBlock methodTestBlk = methodTest.body();
				JSONObject methodParamInJsonObject = new JSONObject();
				methodTestAnnotationTest.param("dataProvider", "loadParam");

				JAnnotationUse methodTestAnnotationParameters = methodTest
						.annotate(cmTest.ref("org.testng.annotations.Parameters"));
				JAnnotationArrayMember parametersArrayMember = methodTestAnnotationParameters.paramArray("value");
				parametersArrayMember.param("reqParamsJsonStr");
				JVar reqParamsJsonStrJVar = methodTest.param(cm.ref("String"), "reqParamsJsonStr");
				JVar reqParamsJsonJVar = methodTestBlk.decl(jSONObjectClass, "reqParamsJson",
						jSONObjectClass.staticInvoke("fromObject").arg(reqParamsJsonStrJVar));
				if (requestBodyClassName != null && !requestBodyClassName.equals("")) {
					requestBody = restPacketMap.get(requestBodyClassName);
					JVar reqJsonRequestJVar = methodTestBlk.decl(jSONObjectClass, "reqJsonRequest",
							reqParamsJsonJVar.invoke("getJSONObject").arg(JExpr.lit("request")));

					JSONObject jsonRequestBody = new JSONObject();
					JVar reqeustBodyTestVar = this.composeRequestBody(reqJsonRequestJVar, methodTestBlk, requestBody,
							requestBodyClass, jsonRequestBody, restPacketMap);

					jInvocation.arg(reqeustBodyTestVar);
					methodParamInJsonObject.put("request", jsonRequestBody);
				}

				if (prefix.equals("Api")) {
					jInvocation.arg(JExpr.ref("accessKey"));
					methodParamInJsonObject.put("accessKey", "String");
					methodTestBlk.decl(cmTest.ref("String"), "accessKey",
							reqParamsJsonJVar.invoke("optString").arg(JExpr.lit("accessKey")));
				} else if (prefix.equals("App")) {
					jInvocation.arg(JExpr.ref("appKey"));
					methodParamInJsonObject.put("appKey", "String");
					methodTestBlk.decl(cmTest.ref("String"), "appKey",
							reqParamsJsonJVar.invoke("optString").arg(JExpr.lit("appKey")));
					jInvocation.arg(JExpr.ref("appVersion"));
					methodParamInJsonObject.put("appVersion", "String");
					methodTestBlk.decl(cmTest.ref("String"), "appVersion",
							reqParamsJsonJVar.invoke("optString").arg(JExpr.lit("appVersion")));
					jInvocation.arg(JExpr.ref("appSource"));
					methodParamInJsonObject.put("appSource", "String");
					methodTestBlk.decl(cmTest.ref("String"), "appSource",
							reqParamsJsonJVar.invoke("optString").arg(JExpr.lit("appSource")));
					jInvocation.arg(JExpr.ref("deviceId"));
					methodParamInJsonObject.put("deviceId", "String");
					methodTestBlk.decl(cmTest.ref("String"), "deviceId",
							reqParamsJsonJVar.invoke("optString").arg(JExpr.lit("deviceId")));
				} else {
					if (headVariables != null && headVariables.size() > 0) {
						for (int index = 0; index < headVariables.size(); index++) {
							RestField headVariable = headVariables.get(index);
							jInvocation.arg(JExpr.ref(headVariable.getNameAlias()));
							methodParamInJsonObject.put(headVariable.getNameAlias(), headVariable.getDatatype());
							JClass jType = cmTest.ref(headVariable.getDatatype());
							if (jType.name().endsWith("Boolean")) {
								methodTestBlk.decl(jType, headVariable.getNameAlias(),
										jType.staticInvoke("valueOf").arg(reqParamsJsonJVar.invoke("getBoolean")
												.arg(JExpr.lit(headVariable.getNameAlias()))));
							} else if (jType.name().endsWith("Integer")) {
								methodTestBlk.decl(jType, headVariable.getNameAlias(),
										jType.staticInvoke("valueOf").arg(reqParamsJsonJVar.invoke("getInt")
												.arg(JExpr.lit(headVariable.getNameAlias()))));
							} else if (jType.name().endsWith("Long")) {
								methodTestBlk.decl(jType, headVariable.getNameAlias(),
										jType.staticInvoke("valueOf").arg(reqParamsJsonJVar.invoke("getLong")
												.arg(JExpr.lit(headVariable.getNameAlias()))));
							} else if (jType.name().endsWith("Double")) {
								methodTestBlk.decl(jType, headVariable.getNameAlias(),
										jType.staticInvoke("valueOf").arg(reqParamsJsonJVar.invoke("getDouble")
												.arg(JExpr.lit(headVariable.getNameAlias()))));
							} else {
								methodTestBlk.decl(jType, headVariable.getNameAlias(),
										jType.staticInvoke("valueOf").arg(reqParamsJsonJVar.invoke("getString")
												.arg(JExpr.lit(headVariable.getNameAlias()))));
							}

						}
					}
					List<RestField> pathVariables = restServiceMethod.getPathVariables();
					if (pathVariables != null && pathVariables.size() > 0) {
						for (int index = 0; index < pathVariables.size(); index++) {
							RestField pathVariable = pathVariables.get(index);
							jInvocation.arg(JExpr.ref(pathVariable.getName()));
							methodParamInJsonObject.put(pathVariable.getName(), pathVariable.getDatatype());
							JClass jType = cmTest.ref(pathVariable.getDatatype());
							if (jType.name().endsWith("Boolean")) {
								methodTestBlk.decl(jType, pathVariable.getName(), jType.staticInvoke("valueOf").arg(
										reqParamsJsonJVar.invoke("getBoolean").arg(JExpr.lit(pathVariable.getName()))));
							} else if (jType.name().endsWith("Integer")) {
								methodTestBlk.decl(jType, pathVariable.getName(), jType.staticInvoke("valueOf").arg(
										reqParamsJsonJVar.invoke("getInt").arg(JExpr.lit(pathVariable.getName()))));
							} else if (jType.name().endsWith("Long")) {
								methodTestBlk.decl(jType, pathVariable.getName(), jType.staticInvoke("valueOf").arg(
										reqParamsJsonJVar.invoke("getLong").arg(JExpr.lit(pathVariable.getName()))));
							} else if (jType.name().endsWith("Double")) {
								methodTestBlk.decl(jType, pathVariable.getName(), jType.staticInvoke("valueOf").arg(
										reqParamsJsonJVar.invoke("getDouble").arg(JExpr.lit(pathVariable.getName()))));
							} else {
								methodTestBlk.decl(jType, pathVariable.getName(), jType.staticInvoke("valueOf").arg(
										reqParamsJsonJVar.invoke("getString").arg(JExpr.lit(pathVariable.getName()))));
							}

						}
					}
					Map<String, RestField> parameters = restServiceMethod.getParameters();
					if (parameters != null && parameters.size() > 0) {
						for (String key : parameters.keySet()) {
							RestField parameter = parameters.get(key);

							jInvocation.arg(JExpr.ref(parameter.getName()));
							methodParamInJsonObject.put(parameter.getName(), parameter.getDatatype());
							JClass jType = cmTest.ref(parameter.getDatatype());
							if (jType.name().endsWith("Boolean")) {
								methodTestBlk.decl(jType, parameter.getName(), jType.staticInvoke("valueOf").arg(
										reqParamsJsonJVar.invoke("getBoolean").arg(JExpr.lit(parameter.getName()))));
							} else if (jType.name().endsWith("Integer")) {
								methodTestBlk.decl(jType, parameter.getName(), jType.staticInvoke("valueOf")
										.arg(reqParamsJsonJVar.invoke("getInt").arg(JExpr.lit(parameter.getName()))));
							} else if (jType.name().endsWith("Long")) {
								methodTestBlk.decl(jType, parameter.getName(), jType.staticInvoke("valueOf")
										.arg(reqParamsJsonJVar.invoke("getLong").arg(JExpr.lit(parameter.getName()))));
							} else if (jType.name().endsWith("Double")) {
								methodTestBlk.decl(jType, parameter.getName(), jType.staticInvoke("valueOf").arg(
										reqParamsJsonJVar.invoke("getDouble").arg(JExpr.lit(parameter.getName()))));
							} else {
								methodTestBlk.decl(jType, parameter.getName(), jType.staticInvoke("valueOf").arg(
										reqParamsJsonJVar.invoke("getString").arg(JExpr.lit(parameter.getName()))));
							}

						}
					}
				}

				JTryBlock secondTryBlock = methodTestBlk._try();
				if (responseBodyClass != cm.VOID) {
					secondTryBlock.body().decl(responseBodyClass, argResponse, jInvocation);
					secondTryBlock.body().invoke(JExpr.ref("log"), "info").arg(JExpr.ref(argResponse));

				} else {
					secondTryBlock.body().add(jInvocation);
				}
				JCatchBlock jsecondCatchBlock = secondTryBlock._catch(cm.ref("Exception"));
				jsecondCatchBlock.body().invoke(jsecondCatchBlock.param("_x"), "printStackTrace");
				restProp.put(sbMethod.toString(),
						methodParamInJsonObject.toString().replaceAll("\n", "").replaceAll("\r", "").trim());
			}
			try {
				FileWriter resFw = new FileWriter(outFile);
				restProp.store(resFw, "ok");
				resFw.close();
			}catch (IOException e) {
				throw new CodeGException("CG505", rest.getClientName() + " 测试数据模板文件生成失败:" + e.getMessage());
			} 
			

			return true;

		} catch (JClassAlreadyExistsException e) {
			throw new CodeGException("CG505", rest.getClientName() + " create failed:" + e.getMessage());
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
			CodeWriter src;
			src = new FileCodeWriter(this.srcDir, "UTF-8");
			// 自上而下地生成类、方法等
			cm.build(src);

			// 生成代码为UTF-8编码
			CodeWriter srcTest;
			srcTest = new FileCodeWriter(this.testSrcDir, "UTF-8");
			// 自上而下地生成类、方法等
			cmTest.build(srcTest);
		} catch (IOException e) {
			throw new CodeGException("CG502", "code generating failed:" + e.getMessage());
		}
		return true;
	}

	private boolean generate(String bundleFile) {
		Set<String> classMap = new HashSet<String>();
		Map<String, RestObject> restPacketMap = BundleXMLParser.processRestPacket(bundleFile, classMap);
		List<Boolean> resList = new ArrayList<Boolean>();
		for (RestObject restPacket : restPacketMap.values()) {
			Callable<Boolean> task = new GeneratorPacketTask(this, restPacket);
			FutureTask<Boolean> future = new FutureTask<Boolean>(task);
			new Thread(future).start();
			try {
				resList.add(future.get());
			} catch (Exception e) {
				log.error(restPacket.getName() + " create failed:" + e.getMessage());
			}
		}
		Map<String, RestController> apiRestMap = BundleXMLParser.processApi(bundleFile, classMap);
		for (RestController rest : apiRestMap.values()) {
			Callable<Boolean> task = new GeneratorTask(this, rest, restPacketMap, "Api");
			FutureTask<Boolean> future = new FutureTask<Boolean>(task);
			new Thread(future).start();
			try {
				resList.add(future.get());
			} catch (Exception e) {
				log.error(rest.getClientName() + " create failed:" + e.getMessage());
			}
		}
		Map<String, RestController> appRestMap = BundleXMLParser.processApp(bundleFile, classMap);
		for (RestController rest : appRestMap.values()) {
			Callable<Boolean> task = new GeneratorTask(this, rest, restPacketMap, "App");
			FutureTask<Boolean> future = new FutureTask<Boolean>(task);
			new Thread(future).start();
			try {
				resList.add(future.get());
			} catch (Exception e) {
				log.error(rest.getClientName() + " create failed:" + e.getMessage());
			}
		}
		Map<String, RestController> genericRestMap = BundleXMLParser.processBase(bundleFile, classMap);
		for (RestController rest : genericRestMap.values()) {
			Callable<Boolean> task = new GeneratorTask(this, rest, restPacketMap, "Generic");
			FutureTask<Boolean> future = new FutureTask<Boolean>(task);
			new Thread(future).start();
			try {
				resList.add(future.get());
			} catch (Exception e) {
				log.error(rest.getClientName() + " create failed:" + e.getMessage());
			}
		}
		// 遍历任务的结果
		boolean isFinished = false;
		while (!isFinished) {
			isFinished = true;
			for (Boolean fs : resList) {
				log.debug(fs);
				if (!fs) {
					isFinished = false;
					break;
				}
			}
		}
		return true;
	}

	private class GeneratorPacketTask implements Callable<Boolean> {

		private SDKGenerator codeGenerator;

		private RestObject body;

		public GeneratorPacketTask(SDKGenerator codeGenerator, RestObject body) {
			this.codeGenerator = codeGenerator;
			this.body = body;
		}

		public Boolean call() {
			JClass jclass = codeGenerator.processBody(body, cm.ref(body.getParent()),false);
			if (jclass != null) {
				return Boolean.TRUE;
			} else {
				return Boolean.FALSE;
			}
		}

	}

	private class GeneratorTask implements Callable<Boolean> {

		private SDKGenerator codeGenerator;

		private RestController rest;

		private Map<String, RestObject> restPacketMap;

		private String prefix;

		public GeneratorTask(SDKGenerator codeGenerator, RestController rest, Map<String, RestObject> restPacketMap,
				String prefix) {
			this.codeGenerator = codeGenerator;
			this.rest = rest;
			this.prefix = prefix;
			this.restPacketMap = restPacketMap;
		}

		public Boolean call() {
			boolean res = codeGenerator.processClient(rest, restPacketMap, prefix);
			return Boolean.valueOf(res);
		}

	}

	JVar composeRequestBodyField(JVar reqJsonJVar, JBlock methodTestBlk, RestObject requestBody,
			JClass requestBodyClass, JSONObject jsonRequestBody) {
		String varNamePrefix = StringHelper
				.toLowerCaseFirstOne(requestBody.get_class().substring(requestBody.get_class().lastIndexOf(".") + 1));
		JVar reqeustBodyTestVar = methodTestBlk.decl(requestBodyClass, varNamePrefix, JExpr._new(requestBodyClass));
		Map<String, RestField> fields = requestBody.getFields();
		if (fields != null) {
			for (String fieldName : fields.keySet()) {
				RestField restField = fields.get(fieldName);
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
					JVar valueVar = null;
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
						fieldValueVar = methodTestBlk.decl(jType, varNamePrefix + fieldName, cmTest.ref("Boolean")
								.staticInvoke("valueOf").arg(reqJsonJVar.invoke("getBoolean").arg(fieldName)));
					} else if (jType.name().endsWith("Integer")) {
						fieldValueVar = methodTestBlk.decl(jType, varNamePrefix + fieldName, cmTest.ref("Integer")
								.staticInvoke("valueOf").arg(reqJsonJVar.invoke("getInt").arg(fieldName)));
					} else if (jType.name().endsWith("Long")) {
						fieldValueVar = methodTestBlk.decl(jType, varNamePrefix + fieldName, cmTest.ref("Long")
								.staticInvoke("valueOf").arg(reqJsonJVar.invoke("getLong").arg(fieldName)));
					} else if (jType.name().endsWith("Double")) {
						fieldValueVar = methodTestBlk.decl(jType, varNamePrefix + fieldName, cmTest.ref("Double")
								.staticInvoke("valueOf").arg(reqJsonJVar.invoke("getDouble").arg(fieldName)));
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

	JVar composeRequestBody(JVar reqJsonVar, JBlock methodTestBlk, RestObject requestBody, JClass requestBodyClass,
			JSONObject jsonRequestBody, Map<String, RestObject> restPacketMap) {
		JVar reqeustBodyTestVar = composeRequestBodyField(reqJsonVar, methodTestBlk, requestBody, requestBodyClass,
				jsonRequestBody);
		String varNamePrefix = StringHelper
				.toLowerCaseFirstOne(requestBodyClass.name().substring(requestBodyClass.name().lastIndexOf(".") + 1));
		Map<String, RestObject> robjects = requestBody.getObjects();
		if (robjects != null && robjects.size() > 0) {
			for (String reName : robjects.keySet()) {
				RestObject ro = robjects.get(reName);
				JClass roClass = cmTest.ref(this.packageNamePrefix + ro.get_class());
				JVar jObjectVar = methodTestBlk.decl(jSONObjectClass, varNamePrefix + roClass.name() + "RO",
						reqJsonVar.invoke("getJSONObject").arg(reName));
				JSONObject jsonRe = new JSONObject();
				JVar roVar = this.composeRequestBody(jObjectVar, methodTestBlk, ro, roClass, jsonRe, restPacketMap);

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
		Map<String, RestObject> roList = requestBody.getLists();
		if (roList != null && roList.size() > 0) {
			for (String listName : roList.keySet()) {
				int index = 0;
				RestObject ro = roList.get(listName);
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
				JVar roVar = composeRequestBodyList(joVar, forBody, ro, listName, roListEntityClass, jsonAy,
						restPacketMap);
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

	JVar composeRequestBodyList(JVar joVar, JBlock methodBlk, RestObject restObject, String listName, JClass roClass,
			JSONArray jsonAy, Map<String, RestObject> restPacketMap) {

		JSONObject jsonObject = new JSONObject();
		JVar roVar = composeRequestBodyField(joVar, methodBlk, restObject, roClass, jsonObject);
		String varNamePrefix = StringHelper
				.toLowerCaseFirstOne(roClass.name().substring(roClass.name().lastIndexOf(".") + 1));
		Map<String, RestObject> robjects = restObject.getObjects();
		if (robjects != null && robjects.size() > 0) {
			for (String reName : robjects.keySet()) {
				RestObject ro2 = robjects.get(reName);
				JClass ro2Class = cm.ref(this.packageNamePrefix + ro2.get_class());
				JVar jObject2Var = methodBlk.decl(jSONObjectClass, varNamePrefix + reName + "JO",
						joVar.invoke("getJSONObject").arg(reName));
				JSONObject jsonRe = new JSONObject();

				JVar ro2Var = this.composeRequestBody(jObject2Var, methodBlk, ro2, ro2Class, jsonRe, restPacketMap);
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
		Map<String, RestObject> roList = restObject.getLists();
		if (roList != null && roList.size() > 0) {
			for (String indexName : roList.keySet()) {
				RestObject roEntity = roList.get(indexName);
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
				JVar ro2Var = composeRequestBodyList(jo2Var, forBody, roEntity, indexName, roListEntityClass, jsonAy2,
						restPacketMap);
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

	JVar composeResponseBody(JVar resJsonVar, JBlock methodBlk, RestObject responseBody, JType responseBodyClass) {
		JVar responseBodyVar = composeResponseBodyField(resJsonVar, methodBlk, responseBody, responseBodyClass);
		String varNamePrefix = StringHelper
				.toLowerCaseFirstOne(responseBodyClass.name().substring(responseBodyClass.name().lastIndexOf(".") + 1));
		Map<String, RestObject> robjects = responseBody.getObjects();
		if (robjects != null && robjects.size() > 0) {
			for (String reName : robjects.keySet()) {
				RestObject ro = robjects.get(reName);
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
		Map<String, RestObject> roList = responseBody.getLists();
		if (roList != null && roList.size() > 0) {
			for (String listName : roList.keySet()) {
				int index = 0;
				RestObject ro = roList.get(listName);
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

	JVar composeResponseList(int loopSeq, JVar joVar, JBlock methodBlk, RestObject restObject, String listName,
			JClass roClass) {

		JVar roVar = composeResponseBodyField(joVar, methodBlk, restObject, roClass);
		String varNamePrefix = StringHelper
				.toLowerCaseFirstOne(roClass.name().substring(roClass.name().lastIndexOf(".") + 1));
		Map<String, RestObject> robjects = restObject.getObjects();
		if (robjects != null && robjects.size() > 0) {
			for (String reName : robjects.keySet()) {
				RestObject ro2 = robjects.get(reName);
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
		Map<String, RestObject> roList = restObject.getLists();
		if (roList != null && roList.size() > 0) {
			for (String indexName : roList.keySet()) {
				RestObject roEntity = roList.get(indexName);
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

	JVar composeResponseBodyField(JVar joVar, JBlock methodBlk, RestObject responseBody, JType responseBodyClass) {
		String varNamePrefix = StringHelper
				.toLowerCaseFirstOne(responseBody.get_class().substring(responseBody.get_class().lastIndexOf(".") + 1));
		JVar responseBodyVar = methodBlk.decl(responseBodyClass, varNamePrefix, JExpr._new(responseBodyClass));
		Map<String, RestField> restFields = responseBody.getFields();
		if (restFields != null) {
			for (String fieldName : restFields.keySet()) {
				RestField restField = restFields.get(fieldName);
				String dataType = restField.getDatatype();
				JType jType = cm.INT;
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
					JVar valueVar = null;
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
						fieldValueVar = methodBlk.decl(jType, varNamePrefix + fieldName, cmTest.ref("Boolean")
								.staticInvoke("valueOf").arg(joVar.invoke("getBoolean").arg(fieldName)));
					} else if (jType.name().endsWith("Integer")) {
						fieldValueVar = methodBlk.decl(jType, varNamePrefix + fieldName, cmTest.ref("Integer")
								.staticInvoke("valueOf").arg(joVar.invoke("getInt").arg(fieldName)));
					} else if (jType.name().endsWith("Long")) {
						fieldValueVar = methodBlk.decl(jType, varNamePrefix + fieldName,
								cmTest.ref("Long").staticInvoke("valueOf").arg(joVar.invoke("getLong").arg(fieldName)));
					} else if (jType.name().endsWith("Double")) {
						fieldValueVar = methodBlk.decl(jType, varNamePrefix + fieldName, cmTest.ref("Double")
								.staticInvoke("valueOf").arg(joVar.invoke("getDouble").arg(fieldName)));
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

}
