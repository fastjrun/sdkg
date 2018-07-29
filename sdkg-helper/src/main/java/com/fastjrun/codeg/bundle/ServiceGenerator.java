package com.fastjrun.codeg.bundle;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import com.fastjrun.codeg.CodeGException;
import com.fastjrun.codeg.bundle.common.CommonMethod;
import com.fastjrun.codeg.bundle.common.CommonService;
import com.fastjrun.codeg.bundle.common.PacketField;
import com.fastjrun.codeg.bundle.common.PacketObject;
import com.fastjrun.codeg.helper.StringHelper;
import com.sun.codemodel.ClassType;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JForLoop;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;

public abstract class ServiceGenerator extends PacketGenerator {

    protected boolean supportDubbo = true;
    Map<String, CommonService> serviceMap;
    Map<String, JClass> serviceClassMap;
    JClass mockHelperClass = cm.ref("com.fastjrun.helper.MockHelper");

    private JClass processService(CommonService service) {

        try {

            JDefinedClass dcService = cm._class(this.packageNamePrefix + "service." + service.get_class(),
                    ClassType.INTERFACE);
            this.addClassDeclaration(dcService);

            JDefinedClass dcServiceMock = null;
            if (this.mock) {
                dcServiceMock = cm._class("com.fastjrun.mock." + service.get_class() + "Mock");
                dcServiceMock._implements(dcService);
                dcServiceMock.annotate(cm.ref("org.springframework.stereotype.Service")).param("value",
                        service.getName());

                this.addClassDeclaration(dcServiceMock);
            }

            List<? extends CommonMethod> methods = service.getMethods();
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
                    String _class = response.get_class();
                    response = packetMap.get(_class);
                    responseBodyClass = poClassMap.get(_class);
                }
                JMethod serviceMethod = dcService.method(JMod.NONE, responseBodyClass, methodName);
                serviceMethod.javadoc().append(methodRemark);
                JMethod serviceMockMethod = null;
                if (this.mock) {
                    serviceMockMethod = dcServiceMock.method(JMod.PUBLIC, responseBodyClass, methodName);
                }

                List<PacketField> headVariables = method.getHeadVariables();
                if (headVariables != null && headVariables.size() > 0) {
                    for (int index = 0; index < headVariables.size(); index++) {
                        PacketField headVariable = headVariables.get(index);
                        JType jType = cm.ref(headVariable.getDatatype());
                        serviceMethod.param(jType, headVariable.getName());
                        if (this.mock) {
                            serviceMockMethod.param(jType, headVariable.getName());
                        }
                    }
                }
                List<PacketField> pathVariables = method.getPathVariables();
                if (pathVariables != null && pathVariables.size() > 0) {
                    for (int index = 0; index < pathVariables.size(); index++) {
                        PacketField pathVariable = pathVariables.get(index);
                        JType jType = cm.ref(pathVariable.getDatatype());
                        serviceMethod.param(jType, pathVariable.getName());
                        if (this.mock) {
                            serviceMockMethod.param(jType, pathVariable.getName());
                        }
                    }
                }
                List<PacketField> parameters = method.getParameters();
                if (parameters != null && parameters.size() > 0) {
                    for (int index = 0; index < parameters.size(); index++) {
                        PacketField parameter = parameters.get(index);
                        JClass jClass = cm.ref(parameter.getDatatype());
                        serviceMethod.param(jClass, parameter.getName());
                        if (this.mock) {
                            serviceMockMethod.param(jClass, parameter.getName());
                        }
                    }
                }
                PacketObject request = method.getRequest();

                if (request != null) {
                    JClass requestBodyClass = this.poClassMap.get(request.get_class());
                    String paramName = "request";
                    serviceMethod.param(requestBodyClass, paramName);
                    if (this.mock) {
                        serviceMockMethod.param(requestBodyClass, paramName);
                    }
                }
                if (this.mock) {
                    JBlock serviceMockMethodBlock = serviceMockMethod.body();
                    if (response != null) {
                        JVar reponseBodyVar = this.composeResponseBody(serviceMockMethodBlock, response,
                                responseBodyClass);
                        serviceMockMethodBlock._return(reponseBodyVar);

                    } else {
                        if (responseBodyClass != null && responseBodyClass != cm.VOID) {
                            JVar reponseVar = serviceMockMethodBlock.decl(responseBodyClass, "reponse",
                                    JExpr._new(responseBodyClass));
                            if (responseBodyClass.name().endsWith("String")) {
                                serviceMockMethodBlock.assign(reponseVar,
                                        mockHelperClass.staticInvoke("geStringListWithAscii").arg(JExpr.lit(10)));
                            } else if (responseBodyClass.name().endsWith("Boolean")) {
                                serviceMockMethodBlock.assign(reponseVar,
                                        mockHelperClass.staticInvoke("geBoolean").arg(JExpr.lit(10)));
                            } else if (responseBodyClass.name().endsWith("Integer")) {
                                serviceMockMethodBlock.assign(reponseVar,
                                        mockHelperClass.staticInvoke("geInteger").arg(JExpr.lit(10)));
                            } else if (responseBodyClass.name().endsWith("Long")) {
                                serviceMockMethodBlock.assign(reponseVar,
                                        mockHelperClass.staticInvoke("geLong").arg(JExpr.lit(10)));
                            } else if (responseBodyClass.name().endsWith("Float")) {
                                serviceMockMethodBlock.assign(reponseVar,
                                        mockHelperClass.staticInvoke("geFloat").arg(JExpr.lit(10)));
                            } else if (responseBodyClass.name().endsWith("Double")) {
                                serviceMockMethodBlock.assign(reponseVar,
                                        mockHelperClass.staticInvoke("geDouble").arg(JExpr.lit(10)));
                            }
                            serviceMockMethodBlock._return(reponseVar);
                        }

                    }
                }
            }
            return dcService;
        } catch (JClassAlreadyExistsException e) {
            throw new CodeGException("CG504", service.getName() + " create failed:" + e.getMessage());
        }

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

    protected void generateService() {
        serviceClassMap = new HashMap<>();
        for (String key : this.serviceMap.keySet()) {
            CommonService service = this.serviceMap.get(key);
            Callable<JClass> task = new GeneratorServiceTask(this, service);
            FutureTask<JClass> future = new FutureTask<JClass>(task);
            new Thread(future).start();
            try {
                serviceClassMap.put(key, future.get());
            } catch (Exception e) {
                log.error(service.getName() + " service class add to map error:" + e.getMessage());
            }
        }

        this.waitForCodeGFinished(serviceClassMap);
    }

    private class GeneratorServiceTask implements Callable<JClass> {

        private ServiceGenerator codeGenerator;

        private CommonService service;

        public GeneratorServiceTask(ServiceGenerator codeGenerator, CommonService service) {
            this.codeGenerator = codeGenerator;
            this.service = service;
        }

        public JClass call() {
            return codeGenerator.processService(service);
        }

    }
}