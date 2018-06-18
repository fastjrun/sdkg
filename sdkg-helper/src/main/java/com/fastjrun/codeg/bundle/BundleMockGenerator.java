package com.fastjrun.codeg.bundle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import org.springframework.web.bind.annotation.RequestMethod;

import com.fastjrun.codeg.CodeGException;
import com.fastjrun.codeg.bundle.common.RestController;
import com.fastjrun.codeg.bundle.common.RestField;
import com.fastjrun.codeg.bundle.common.RestObject;
import com.fastjrun.codeg.bundle.common.RestService;
import com.fastjrun.codeg.bundle.common.RestServiceMethod;
import com.fastjrun.codeg.helper.BundleXMLParser;
import com.fastjrun.codeg.helper.StringHelper;
import com.sun.codemodel.ClassType;
import com.sun.codemodel.CodeWriter;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JForLoop;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;
import com.sun.codemodel.writer.FileCodeWriter;

public class BundleMockGenerator extends PacketGenerator {

	JClass mockHelperClass = cm.ref("com.fastjrun.helper.MockHelper");

	/**
	 * @param rest
	 * @param restPacketMap
	 * @param prefix
	 *            App:为App提供接口;Api:为其他系统提供接口;Generic：普通接口
	 */
	private void processRest(RestController rest, Map<String, RestObject> restPacketMap, String prefix) {
		RestService restService = rest.getRestService();
		try {
			JDefinedClass dcService = cm._class(this.packageNamePrefix + "service." + restService.get_class(),
					ClassType.INTERFACE);

			this.addClassDeclaration(dcService);

			JDefinedClass dcServiceMock = cm._class("com.fastjrun.mock." + restService.get_class() + "Mock");
			dcServiceMock._implements(dcService);
			dcServiceMock.annotate(cm.ref("org.springframework.stereotype.Service"));

			this.addClassDeclaration(dcServiceMock);

			JDefinedClass dcController = cm._class("com.fastjrun.mock.web.controller." + rest.getName());

			JClass baseControllerClass = cm.ref("com.fastjrun.web.controller.BaseController");
			if (!prefix.equals("Generic")) {
				baseControllerClass = cm.ref("com.fastjrun.web.controller.Base" + prefix + "Controller");
			}

			dcController._extends(baseControllerClass);
			dcController.annotate(cm.ref("org.springframework.web.bind.annotation.RestController"));
			dcController.annotate(cm.ref("org.springframework.web.bind.annotation.RequestMapping")).param("value",
					rest.getPath());
			dcController.annotate(cm.ref("io.swagger.annotations.Api")).param("value", rest.getRemark()).param("tags",
					rest.getTags());

			this.addClassDeclaration(dcController);

			String serviceName = restService.getName();
			JFieldVar fieldVar = dcController.field(JMod.PRIVATE, dcService, serviceName);
			fieldVar.annotate(cm.ref("org.springframework.beans.factory.annotation.Autowired"));
			List<RestServiceMethod> restServiceMethods = restService.getMethods();
			for (RestServiceMethod restServiceMethod : restServiceMethods) {
				RestObject request = restServiceMethod.getRequest();
				JClass requestClass = null;
				JClass requestBodyClass = null;
				if (request != null) {
					if (prefix.equals("Generic")) {
						requestClass = cm.ref(this.packageNamePrefix + request.get_class());
					} else {
						RestObject body = request.getObjects().get("body");
						if (body == null) {
							requestBodyClass = cm.ref("com.fastjrun.packet.BaseDefaultRequestBody");
						} else {
							requestBodyClass = cm.ref(this.packageNamePrefix + body.get_class());
						}

						requestClass = cm.ref("com.fastjrun.packet.Base" + prefix + "Request").narrow(requestBodyClass);
					}

				}

				RestObject response = restServiceMethod.getResponse();
				RestObject body = null;
				JClass responseClass = null;
				JClass responseHeadClass = cm.ref("com.fastjrun.packet.BaseResponseHead");
				JClass responseBodyClass = null;
				if (response != null) {
					if (prefix.equals("Generic")) {
						responseClass = cm.ref(this.packageNamePrefix + response.get_class());
					} else {
						body = response.getObjects().get("body");
						if (body == null) {
							responseBodyClass = cm.ref("com.fastjrun.packet.BaseDefaultResponseBody");
						} else {
							responseBodyClass = cm.ref(this.packageNamePrefix + body.get_class());
						}
						responseClass = cm.ref("com.fastjrun.packet.BaseResponse").narrow(responseBodyClass);
					}

				} else {
					responseBodyClass = cm.ref("com.fastjrun.packet.BaseDefaultResponseBody");
					responseClass = cm.ref("com.fastjrun.packet.BaseResponse").narrow(responseBodyClass);
				}
				JMethod serviceMethod = dcService.method(JMod.PUBLIC, responseClass,
						restServiceMethod.getName() + restServiceMethod.getVersion());
				JMethod serviceMockMethod = dcServiceMock.method(JMod.PUBLIC, responseClass,
						restServiceMethod.getName() + restServiceMethod.getVersion());
				if (requestClass != null) {
					serviceMethod.param(requestClass, "request");
					serviceMockMethod.param(requestClass, "request");
				}
				serviceMockMethod.annotate(cm.ref("Override"));
				JBlock serviceMockMethodBlock = serviceMockMethod.body();
				if (response != null) {
					if (response.get_class() != null && !response.get_class().equals("")) {
						JVar reponseVar = this.composeResponseBody(serviceMockMethodBlock,
								restPacketMap.get(response.get_class()), responseClass);
						serviceMockMethodBlock._return(reponseVar);
					} else {
						JVar reponseServiceVar = serviceMockMethodBlock.decl(responseClass, "response",
								JExpr._new(responseClass));

						JVar reponseHeadVar = serviceMockMethodBlock.decl(responseHeadClass, "responseHead",
								JExpr._new(responseHeadClass));
						serviceMockMethodBlock.invoke(reponseHeadVar, "setCode").arg(JExpr.lit("0000"));
						serviceMockMethodBlock.invoke(reponseHeadVar, "setMsg").arg(JExpr.lit("Mock."));
						serviceMockMethodBlock.invoke(reponseServiceVar, "setHead").arg(reponseHeadVar);

						JVar reponseBodyVar = this.composeResponseBody(serviceMockMethodBlock,
								restPacketMap.get(body.get_class()), responseBodyClass);

						serviceMockMethodBlock.invoke(reponseServiceVar, "setBody").arg(reponseBodyVar);
						serviceMockMethodBlock._return(reponseServiceVar);
					}
				} else {
					JVar reponseServiceVar = serviceMockMethodBlock.decl(responseClass, "response",
							cm.ref("com.fastjrun.helper.BaseResponseHelper").staticInvoke("getSuccessResult"));
					serviceMockMethodBlock._return(reponseServiceVar);

				}

				JMethod controllerMethod = dcController.method(JMod.PUBLIC, responseClass, serviceMethod.name());
				String versionInMethodValue = restServiceMethod.getVersion();
				JClass longClass = cm.ref("Long");
				if (!versionInMethodValue.equals("")) {
					versionInMethodValue = "/" + restServiceMethod.getVersion();
				}
				String methodValue = restServiceMethod.getPath() + versionInMethodValue;
				if (prefix.equals("Api")) {
					methodValue = restServiceMethod.getPath() + versionInMethodValue
							+ "/{accessKey}/{txTime}/{md5Hash}";
				} else if (prefix.equals("App")) {
					methodValue = restServiceMethod.getPath() + versionInMethodValue
							+ "/{appKey}/{appVersion}/{appSource}/{deviceId}/{txTime}";
				} else {
					List<RestField> headVariables = restServiceMethod.getHeadVariables();
					if (headVariables != null && headVariables.size() > 0) {
						for (int index = 0; index < headVariables.size(); index++) {
							RestField headVariable = headVariables.get(index);
							JClass jClass = cm.ref(headVariable.getDatatype());
							serviceMethod.param(jClass, headVariable.getName());
							serviceMockMethod.param(jClass, headVariable.getName());
						}
					}
					List<RestField> pathVariables = restServiceMethod.getPathVariables();
					if (pathVariables != null && pathVariables.size() > 0) {
						for (int index = 0; index < pathVariables.size(); index++) {
							RestField pathVariable = pathVariables.get(index);
							methodValue += "/{" + pathVariable.getName() + "}";
							JClass jClass = cm.ref(pathVariable.getDatatype());
							serviceMethod.param(jClass, pathVariable.getName());
							serviceMockMethod.param(jClass, pathVariable.getName());
						}
					}
					Map<String, RestField> parameters = restServiceMethod.getParameters();
					if (parameters != null && parameters.size() > 0) {
						for (String key : parameters.keySet()) {
							RestField parameter = parameters.get(key);
							JClass jClass = cm.ref(parameter.getDatatype());
							serviceMethod.param(jClass, parameter.getName());
							serviceMockMethod.param(jClass, parameter.getName());
						}
					}
				}
				RequestMethod requestMethod = RequestMethod.POST;
				switch (restServiceMethod.getHttpMethod().toUpperCase()) {
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
				controllerMethod.annotate(cm.ref("org.springframework.web.bind.annotation.RequestMapping"))
						.param("value", methodValue).param("method", requestMethod)
						.param("produces", "application/json;charset=UTF-8");
				controllerMethod.annotate(cm.ref("io.swagger.annotations.ApiOperation"))
						.param("value", restServiceMethod.getRemark()).param("notes", restServiceMethod.getRemark());
				if (prefix.equals("Generic")) {
					List<RestField> headVariables = restServiceMethod.getHeadVariables();
					if (headVariables != null && headVariables.size() > 0) {
						for (int index = 0; index < headVariables.size(); index++) {
							RestField headVariable = headVariables.get(index);
							JClass jClass = cm.ref(headVariable.getDatatype());
							JVar headVariableJVar = controllerMethod.param(jClass, headVariable.getName());
							headVariableJVar.annotate(cm.ref("org.springframework.web.bind.annotation.RequestHeader"))
									.param("value", headVariable.getName());
							headVariableJVar.annotate(cm.ref("io.swagger.annotations.ApiParam"))
									.param("name", headVariable.getName()).param("value", headVariable.getRemark())
									.param("required", true);
						}
					}
					List<RestField> pathVariables = restServiceMethod.getPathVariables();
					if (pathVariables != null && pathVariables.size() > 0) {
						for (int index = 0; index < pathVariables.size(); index++) {
							RestField pathVariable = pathVariables.get(index);
							JClass jClass = cm.ref(pathVariable.getDatatype());
							JVar pathVariableJVar = controllerMethod.param(jClass, pathVariable.getName());
							pathVariableJVar.annotate(cm.ref("org.springframework.web.bind.annotation.PathVariable"))
									.param("value", pathVariable.getName());
							pathVariableJVar.annotate(cm.ref("io.swagger.annotations.ApiParam"))
									.param("name", pathVariable.getName()).param("value", pathVariable.getRemark())
									.param("required", true);
						}
					}
					Map<String, RestField> parameters = restServiceMethod.getParameters();
					if (parameters != null && parameters.size() > 0) {
						for (String key : parameters.keySet()) {
							RestField parameter = parameters.get(key);
							JClass jClass = cm.ref(parameter.getDatatype());
							JVar parameterJVar = controllerMethod.param(jClass, parameter.getName());
							parameterJVar.annotate(cm.ref("org.springframework.web.bind.annotation.RequestParam"))
									.param("name", parameter.getName()).param("required", true);
							parameterJVar.annotate(cm.ref("io.swagger.annotations.ApiParam"))
									.param("name", parameter.getName()).param("value", parameter.getRemark())
									.param("required", true);
						}
					}
				} else {
					if (prefix.equals("Api")) {
						JVar accessKeyJVar = controllerMethod.param(cm.ref("String"), "accessKey");
						accessKeyJVar.annotate(cm.ref("org.springframework.web.bind.annotation.PathVariable"))
								.param("value", "accessKey");
						accessKeyJVar.annotate(cm.ref("io.swagger.annotations.ApiParam")).param("name", "accessKey")
								.param("value", "接入客户端的accessKey").param("required", true);
						JVar txTimeJVar = controllerMethod.param(longClass, "txTime");
						txTimeJVar.annotate(cm.ref("org.springframework.web.bind.annotation.PathVariable"))
								.param("value", "txTime");
						txTimeJVar.annotate(cm.ref("io.swagger.annotations.ApiParam")).param("name", "txTime")
								.param("value", "接口请求时间戳").param("required", true);
						JVar md5HashJVar = controllerMethod.param(cm.ref("String"), "md5Hash");
						md5HashJVar.annotate(cm.ref("org.springframework.web.bind.annotation.PathVariable"))
								.param("value", "md5Hash");
						md5HashJVar.annotate(cm.ref("io.swagger.annotations.ApiParam")).param("name", "md5Hash")
								.param("value", "md5Hash").param("required", true);
					} else if (prefix.equals("App")) {
						JVar appKeyJVar = controllerMethod.param(cm.ref("String"), "appKey");
						appKeyJVar.annotate(cm.ref("org.springframework.web.bind.annotation.PathVariable"))
								.param("value", "appKey");
						appKeyJVar.annotate(cm.ref("io.swagger.annotations.ApiParam")).param("name", "appKey")
								.param("value", "app分配的key").param("required", true);
						JVar appVersionJVar = controllerMethod.param(cm.ref("String"), "appVersion");
						appVersionJVar.annotate(cm.ref("org.springframework.web.bind.annotation.PathVariable"))
								.param("value", "appVersion");
						appVersionJVar.annotate(cm.ref("io.swagger.annotations.ApiParam")).param("name", "appVersion")
								.param("value", "当前app版本号").param("required", true);
						JVar appSourceJVar = controllerMethod.param(cm.ref("String"), "appSource");
						appSourceJVar.annotate(cm.ref("org.springframework.web.bind.annotation.PathVariable"))
								.param("value", "appSource");
						appSourceJVar.annotate(cm.ref("io.swagger.annotations.ApiParam")).param("name", "appSource")
								.param("value", "当前app渠道：ios,android").param("required", true);
						JVar deviceIdJVar = controllerMethod.param(cm.ref("String"), "deviceId");
						deviceIdJVar.annotate(cm.ref("org.springframework.web.bind.annotation.PathVariable"))
								.param("value", "deviceId");
						deviceIdJVar.annotate(cm.ref("io.swagger.annotations.ApiParam")).param("name", "deviceId")
								.param("value", "设备Id").param("required", true);

						JVar txTimeJVar = controllerMethod.param(longClass, "txTime");
						txTimeJVar.annotate(cm.ref("org.springframework.web.bind.annotation.PathVariable"))
								.param("value", "txTime");
						txTimeJVar.annotate(cm.ref("io.swagger.annotations.ApiParam")).param("name", "txTime")
								.param("value", "接口请求时间戳").param("required", true);
					}
				}

				JBlock controllerMethodBlk = controllerMethod.body();
				if (prefix.equals("Generic")) {
					JInvocation jInvocation = JExpr.invoke(JExpr.refthis(restService.getName()), serviceMethod.name());
					if (requestClass != null) {
						JVar requestParam = controllerMethod.param(requestClass, "request");
						requestParam.annotate(cm.ref("org.springframework.web.bind.annotation.RequestBody"));
						requestParam.annotate(cm.ref("javax.validation.Valid"));
						jInvocation.arg(JExpr.ref("request"));

					}
					List<RestField> headVariables = restServiceMethod.getHeadVariables();
					if (headVariables != null && headVariables.size() > 0) {
						for (int index = 0; index < headVariables.size(); index++) {
							RestField headVariable = headVariables.get(index);
							jInvocation.arg(JExpr.ref(headVariable.getName()));
						}
					}
					List<RestField> pathVariables = restServiceMethod.getPathVariables();
					if (pathVariables != null && pathVariables.size() > 0) {
						for (int index = 0; index < pathVariables.size(); index++) {
							RestField pathVariable = pathVariables.get(index);
							jInvocation.arg(JExpr.ref(pathVariable.getName()));
						}
					}
					Map<String, RestField> parameters = restServiceMethod.getParameters();
					if (parameters != null && parameters.size() > 0) {
						for (String key : parameters.keySet()) {
							RestField parameter = parameters.get(key);
							jInvocation.arg(JExpr.ref(parameter.getName()));
						}
					}
					controllerMethodBlk.decl(responseClass, "response", jInvocation);
				} else {
					if (requestClass != null) {
						JVar requestBodyParam = controllerMethod.param(requestBodyClass, "requestBody");
						requestBodyParam.annotate(cm.ref("org.springframework.web.bind.annotation.RequestBody"));
						requestBodyParam.annotate(cm.ref("javax.validation.Valid"));

						controllerMethodBlk.decl(requestClass, "request", JExpr._new(requestClass));
					}

					JClass requestHeadClass = cm.ref("com.fastjrun.packet.Base" + prefix + "RequestHead");
					JVar requestHeadVar = controllerMethodBlk.decl(requestHeadClass, "requestHead",
							JExpr._new(requestHeadClass));

					if (prefix.equals("Api")) {
						controllerMethodBlk.invoke(JExpr.ref("requestHead"), "setAccessKey")
								.arg(JExpr.ref("accessKey"));
						controllerMethodBlk.invoke(JExpr.ref("requestHead"), "setTxTime").arg(JExpr.ref("txTime"));
						controllerMethodBlk.invoke(JExpr.ref("requestHead"), "setMd5Hash").arg(JExpr.ref("md5Hash"));
					} else if (prefix.equals("App")) {
						controllerMethodBlk.invoke(JExpr.ref("requestHead"), "setAppKey").arg(JExpr.ref("appKey"));
						controllerMethodBlk.invoke(JExpr.ref("requestHead"), "setAppVersion")
								.arg(JExpr.ref("appVersion"));
						controllerMethodBlk.invoke(JExpr.ref("requestHead"), "setAppSource")
								.arg(JExpr.ref("appSource"));
						controllerMethodBlk.invoke(JExpr.ref("requestHead"), "setDeviceId").arg(JExpr.ref("deviceId"));
						controllerMethodBlk.invoke(JExpr.ref("requestHead"), "setTxTime").arg(JExpr.ref("txTime"));

					}

					controllerMethodBlk.invoke(JExpr._this(), "processHead").arg(requestHeadVar);
					if (requestClass != null) {
						controllerMethodBlk.invoke(JExpr.ref("request"), "setHead").arg(requestHeadVar);
						controllerMethodBlk.invoke(JExpr.ref("request"), "setBody").arg(JExpr.ref("requestBody"));
						controllerMethodBlk.decl(responseClass, "response",
								JExpr.invoke(JExpr.refthis(restService.getName()), serviceMethod.name())
										.arg(JExpr.ref("request")));
					} else {
						controllerMethodBlk.decl(responseClass, "response",
								JExpr.invoke(JExpr.refthis(restService.getName()), serviceMethod.name()));
					}

				}

				controllerMethodBlk.invoke(JExpr.ref("log"), "debug").arg(JExpr.ref("response"));
				controllerMethodBlk._return(JExpr.ref("response"));
			}
		} catch (JClassAlreadyExistsException e) {
			throw new CodeGException("CG504", rest.getName() + " create failed:" + e.getMessage());
		}
	}

	JVar composeResponseBody(JBlock methodBlk, RestObject responseBody, JClass responseBodyClass) {
		JVar responseVar = composeResponseBodyField(methodBlk, responseBody, responseBodyClass);
		Map<String, RestObject> robjects = responseBody.getObjects();
		if (robjects != null && robjects.size() > 0) {
			for (String reName : robjects.keySet()) {
				RestObject ro = robjects.get(reName);
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
		Map<String, RestObject> roList = responseBody.getLists();
		if (roList != null && roList.size() > 0) {
			for (String listName : roList.keySet()) {
				int index = 0;
				RestObject ro = roList.get(listName);
				JClass roListEntityClass = cm.ref(this.packageNamePrefix + ro.get_class());
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

	JVar composeResponseBodyField(JBlock methodBlk, RestObject responseBody, JClass responseBodyClass) {
		String varNamePrefix = StringHelper
				.toLowerCaseFirstOne(responseBody.get_class().substring(responseBody.get_class().lastIndexOf(".") + 1));
		JVar reponseBodyVar = methodBlk.decl(responseBodyClass, varNamePrefix, JExpr._new(responseBodyClass));
		Map<String, RestField> restFields = responseBody.getFields();
		for (String fieldName : restFields.keySet()) {
			RestField restField = restFields.get(fieldName);
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

	JVar composeResponseBodyList(int loopSeq, JBlock methodBlk, RestObject responseBody, String listName,
			JClass responseBodyClass) {
		JVar reponseBodyVar = composeResponseBodyField(methodBlk, responseBody, responseBodyClass);
		Map<String, RestObject> robjects = responseBody.getObjects();
		if (robjects != null && robjects.size() > 0) {
			for (String reName : robjects.keySet()) {
				RestObject ro2 = robjects.get(reName);
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
		Map<String, RestObject> roList = responseBody.getLists();
		if (roList != null && roList.size() > 0) {
			int index = 0;
			for (String indexName : roList.keySet()) {
				index++;
				RestObject roEntity = roList.get(indexName);
				JClass roListEntityClass = cm.ref(this.packageNamePrefix + roEntity.get_class());
				String varNamePrefix = listName + StringHelper.toUpperCaseFirstOne(
						roListEntityClass.name().substring(roListEntityClass.name().lastIndexOf(".") + 1));
				JVar listsVar = methodBlk.decl(cm.ref("java.util.List").narrow(roListEntityClass),
						varNamePrefix + "List" + loopSeq,
						JExpr._new(cm.ref("java.util.ArrayList").narrow(roListEntityClass)));
				JVar iSizeVar = methodBlk.decl(cm.INT, "iSize" + String.valueOf(loopSeq) + String.valueOf(index),
						mockHelperClass.staticInvoke("geInteger").arg(JExpr.lit(10)).invoke("intValue"));
				JForLoop forLoop = methodBlk._for();
				JVar iVar = forLoop.init(cm.INT, "i" + String.valueOf(loopSeq) + String.valueOf(index),
						JExpr.lit(0));
				forLoop.test(iVar.lt(iSizeVar));
				forLoop.update(iVar.incr());
				JBlock forBody = forLoop.body();
				JVar ro2Var = composeResponseBodyList(loopSeq+1, forBody, roEntity, indexName, roListEntityClass);
				forBody.invoke(listsVar, "add").arg(ro2Var);

				methodBlk.invoke(reponseBodyVar, "set" + StringHelper.toUpperCaseFirstOne(indexName)).arg(listsVar);
			}

		}
		return reponseBodyVar;

	}

	private class GeneratorPacketTask implements Callable<Boolean> {

		private BundleMockGenerator codeGenerator;

		private RestObject body;

		public GeneratorPacketTask(BundleMockGenerator codeGenerator, RestObject body) {
			this.codeGenerator = codeGenerator;
			this.body = body;
		}

		public Boolean call() {
			JClass jclass = codeGenerator.processBody(body, cm.ref(body.getParent()), true);
			if (jclass != null) {
				return Boolean.TRUE;
			} else {
				return Boolean.FALSE;
			}
		}

	}

	private class GeneratorRestTask implements Callable<Boolean> {

		private BundleMockGenerator codeGenerator;

		private RestController rest;

		private Map<String, RestObject> restPacketMap;

		private String prefix;

		public GeneratorRestTask(BundleMockGenerator codeGenerator, RestController rest,
				Map<String, RestObject> restPacketMap, String prefix) {
			this.codeGenerator = codeGenerator;
			this.rest = rest;
			this.restPacketMap = restPacketMap;
			this.prefix = prefix;
		}

		public Boolean call() {
			codeGenerator.processRest(rest, restPacketMap, prefix);
			return true;
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
		Map<String, RestController> genericRestMap = BundleXMLParser.processBase(bundleFile, classMap);
		for (RestController rest : genericRestMap.values()) {
			Callable<Boolean> task = new GeneratorRestTask(this, rest, restPacketMap, "Generic");
			FutureTask<Boolean> future = new FutureTask<Boolean>(task);
			new Thread(future).start();
			try {
				resList.add(future.get());
			} catch (Exception e) {
				log.error(rest.getClientName() + " create failed:" + e.getMessage());
			}
		}
		Map<String, RestController> apiRestMap = BundleXMLParser.processApi(bundleFile, classMap);
		for (RestController rest : apiRestMap.values()) {
			Callable<Boolean> task = new GeneratorRestTask(this, rest, restPacketMap, "Api");
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
			Callable<Boolean> task = new GeneratorRestTask(this, rest, restPacketMap, "App");
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
		try {
			CodeWriter src = new FileCodeWriter(this.srcDir, "UTF-8");
			// 自上而下地生成类、方法等
			cm.build(src);
		} catch (IOException e) {
			throw new CodeGException("CG502", "code generating failed:" + e.getMessage());
		}
		return true;
	}

	public static void main(String[] args) {
		String moduleName = args[0];
		String bundleFiles = args[1];
		String packageNamePrefix = args[2];
		BundleMockGenerator apiMockGenerator = new BundleMockGenerator();
		apiMockGenerator.setModuleName(moduleName);
		apiMockGenerator.setBundleFiles(bundleFiles.split(","));
		apiMockGenerator.setPackageNamePrefix(packageNamePrefix);
		apiMockGenerator.generate();
	}

}