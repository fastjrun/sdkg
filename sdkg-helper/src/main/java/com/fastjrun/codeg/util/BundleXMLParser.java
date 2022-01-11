/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.codeg.util;

import com.fastjrun.codeg.common.*;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import java.util.*;

public class BundleXMLParser implements CodeGConstants {

    private Map<String, ControllerType> controllerTypeMap = new HashMap<>();

    private Map<String, String> contentType = new HashMap<>();

    private String[] bundleFiles;

    private Element[] bundleRoots;

    private Map<String, PacketObject> packetMap = new HashMap<>();

    private Map<String, CommonService> serviceMap = new HashMap<>();

    private Map<String, CommonController> controllerMap = new HashMap<>();

    public Map<String, ControllerType> getControllerTypeMap() {
        return controllerTypeMap;
    }

    private static PacketField parsePacketField(Element elePacketField) {
        String name = elePacketField.attributeValue("name");
        String fieldName = parseFieldName(name);
        String fieldNameAlias = elePacketField.attributeValue("nameAlias");
        String datatype = elePacketField.attributeValue("dataType");
        String length = elePacketField.attributeValue("length");
        String canBeNull = elePacketField.attributeValue("canBeNull");
        String parameterRemark = elePacketField.attributeValue("remark");
        String defaultValue = elePacketField.attributeValue("defaultValue");
        String _new = elePacketField.attributeValue("new");
        PacketField field = new PacketField();
        field.setName(name);
        field.setNameAlias(fieldNameAlias);
        field.setFieldName(fieldName);
        field.setDatatype(datatype);
        field.setLength(length);
        if (canBeNull != null && !canBeNull.equals("")) {
            field.setCanBeNull(Boolean.parseBoolean(canBeNull));
        }
        if (defaultValue != null && !defaultValue.equals("")) {
            field.setDefaultValue(defaultValue);
        }
        if (_new != null && !_new.equals("")) {
            field.set_new(Boolean.parseBoolean(_new));
        }
        field.setRemark(parameterRemark);
        return field;
    }

    public Map<String, PacketObject> getPacketMap() {
        return packetMap;
    }

    public Map<String, CommonService> getServiceMap() {
        return serviceMap;
    }

    public Map<String, CommonController> getControllerMap() {
        return controllerMap;
    }

    public void setControllerMap(Map<String, CommonController> controllerMap) {
        this.controllerMap = controllerMap;
    }

    public void setBundleFiles(String[] bundleFiles) {
        this.bundleFiles = bundleFiles;
    }

    public void init() {
        this.contentType.put("json", MediaTypes.JSON_UTF_8);
        this.contentType.put("xml", MediaTypes.APPLICATION_XML_UTF_8);
        this.contentType.put("text", "");
        this.contentType.put(
                "json,xml", MediaTypes.JSON_UTF_8 + "," + MediaTypes.APPLICATION_XML_UTF_8);
        this.controllerTypeMap.put(
                CodeGConstants.ControllerType_GENERIC.name, CodeGConstants.ControllerType_GENERIC);
        try {
            this.processExtControllerType();
        } catch (Exception e) {

        }
    }

    public void processExtControllerType() {
        ResourceBundle rb = ResourceBundle.getBundle("ext-generator");
        if (rb != null) {
            rb.keySet().stream()
                    .forEach(
                            key -> {
                                String[] values = rb.getString(key).split(",");
                                ControllerType controllerType =
                                        new ControllerType(
                                                key,
                                                values[1],
                                                values[2],
                                                values[3],
                                                values[4],
                                                values[5],
                                                values[6]);
                                this.controllerTypeMap.put(key, controllerType);
                            });
        }
    }

    private void initBundleRoots() {
        this.bundleRoots = new Element[bundleFiles.length];
        for (int i = 0; i < this.bundleFiles.length; i++) {
            SAXReader reader = new SAXReader();
            Document document;
            try {
                document = reader.read(this.bundleFiles[i]);
            } catch (DocumentException e) {
                throw new CodeGException(CodeGMsgContants.CODEG_BUNDLEFILE_INVALID, "bundleFile is wrong" + e.getMessage());
            }
            this.bundleRoots[i] = document.getRootElement();
        }
    }

    public Map<String, Element> checkClassNameRepeat(Element bundleRoot) {
        Map<String, Element> classMap = new HashMap<>();
        List<Node> nodePackets = bundleRoot.selectNodes("packets/packet");
        for (Node nodePacket : nodePackets) {
            Map<String, Element> childClassMap = checkClassNameRepeatInPO((Element) nodePacket);
            Iterator<String> itera = childClassMap.keySet().iterator();
            while (itera.hasNext()) {
                String childClassName = itera.next();
                if (classMap.keySet().contains(childClassName)) {
                    throw new CodeGException(
                            CodeGMsgContants.CODEG_CLASS_DUPLICATED, childClassName + " is duplicated");
                }
                classMap.put(childClassName, childClassMap.get(childClassName));
            }
        }
        List<Node> nodeServices = bundleRoot.selectNodes("services/service");
        for (Node nodeService : nodeServices) {
            String classP = ((Element) nodeService).attributeValue("class");
            if (classMap.keySet().contains(classP)) {
                throw new CodeGException(
                        CodeGMsgContants.CODEG_CLASS_DUPLICATED, "service." + classP + " is duplicated");
            }
            classMap.put(classP, (Element) nodeService);
        }
        List<Node> nodeControllers = bundleRoot.selectNodes("*/controller");
        for (Node nodeController : nodeControllers) {
            String classP = ((Element) nodeController).attributeValue("name");
            if (classMap.keySet().contains(classP)) {
                throw new CodeGException(
                        CodeGMsgContants.CODEG_CLASS_DUPLICATED, "web.controller." + classP + " is duplicated");
            }
            classMap.put(classP, (Element) nodeController);
        }
        return classMap;
    }

    private Map<String, Element> checkClassNameRepeatInPO(Element po) {
        List<Element> elements = po.elements();
        Map<String, Element> classMap = new HashMap<>();
        String classP = po.attributeValue("class");
        if (classMap.keySet().contains(classP)) {
            throw new CodeGException(CodeGMsgContants.CODEG_CLASS_DUPLICATED, classP + " is duplicated");
        }
        classMap.put(classP, po);
        for (Element element : elements) {
            if (element == null) {
                continue;
            }
            String tagName = element.getName();
            if (tagName == null || tagName.equals("")) {
                continue;
            }
            String eleName = element.attributeValue("name");
            if (eleName == null || eleName.equals("")) {
                continue;
            }
            if (tagName.equals("list") || tagName.equals("object")) {
                Map<String, Element> childClassMap = checkClassNameRepeatInPO(element);
                Iterator<String> itera = childClassMap.keySet().iterator();
                while (itera.hasNext()) {
                    String childClassName = itera.next();
                    if (classMap.keySet().contains(childClassName)) {
                        throw new CodeGException(
                                CodeGMsgContants.CODEG_CLASS_DUPLICATED, childClassName + " is duplicated");
                    }
                    classMap.put(childClassName, childClassMap.get(childClassName));
                }
            }
        }
        return classMap;
    }

    private void processControllers(Element bundleRoot) {
        List<Node> nodeControllers = bundleRoot.selectNodes("controllers/controller");
        for (Node controllerNode : nodeControllers) {
            Element eleController = (Element) controllerNode;
            CommonController commonController = new CommonController();
            String type = eleController.attributeValue("type");
            ControllerType controllerType = this.controllerTypeMap.get(type);
            commonController.setControllerType(controllerType);
            String name = eleController.attributeValue("name");
            String path = eleController.attributeValue("path");
            String version = eleController.attributeValue("version");
            String remark = eleController.attributeValue("remark");
            String clientName = eleController.attributeValue("clientName");
            String tags = eleController.attributeValue("tags");
            String _new = eleController.attributeValue("new");
            commonController.setName(name);
            commonController.setPath(path);
            commonController.setVersion(version);
            commonController.setRemark(remark);
            commonController.setClientName(clientName);
            commonController.setTags(tags.split(","));
            if (_new != null && !_new.equals("")) {
                commonController.set_new(Boolean.parseBoolean(_new));
            }
            Element eleService = eleController.element("service");
            String serviceName = eleService.attributeValue("name");
            commonController.setServiceName(serviceName);
            String serviceRef = eleService.attributeValue("ref");
            commonController.setServiceRef(serviceRef);
            CommonService service = serviceMap.get(serviceRef);
            commonController.setService(service);
            this.controllerMap.put(name, commonController);
        }
    }

    private void processPacket() {
        for (int i = 0; i < this.bundleRoots.length; i++) {
            List<Node> nodePackets = this.bundleRoots[i].selectNodes("packets/packet");
            for (Node nodePacket : nodePackets) {
                Element elePacket = (Element) nodePacket;
                processPO(elePacket);
            }
        }
        for (String key : packetMap.keySet()) {
            PacketObject po = packetMap.get(key);
            refineRef(po);
        }

        PacketObject integerPO = new PacketObject("java.lang.Integer", false, "java.lang.Integer");
        PacketObject stringPO = new PacketObject("java.lang.String", false, "java.lang.String");
        PacketObject booleanPO = new PacketObject("java.lang.Boolean", false, "java.lang.Boolean");
        PacketObject doublePO = new PacketObject("java.lang.Double", false, "java.lang.Double");
        PacketObject longPO = new PacketObject("java.lang.Long", false, "java.lang.Long");
        PacketObject datePO = new PacketObject("java.util.Date", false, "java.util.Date");
        this.packetMap.put(integerPO.getName(), integerPO);
        this.packetMap.put(stringPO.getName(), stringPO);
        this.packetMap.put(booleanPO.getName(), booleanPO);
        this.packetMap.put(doublePO.getName(), doublePO);
        this.packetMap.put(longPO.getName(), longPO);
        this.packetMap.put(datePO.getName(), datePO);
    }

    private void processService(Element bundleRoot) {
        List<Node> nodeServices = bundleRoot.selectNodes("services/service");
        for (Node nodeService : nodeServices) {
            Element eleService = (Element) nodeService;
            String name = eleService.attributeValue("name");
            String _class = eleService.attributeValue("class");
            CommonService service = new CommonService();

            service.set_class(_class);
            service.setName(name);
            List<Node> nodeMethods = eleService.selectNodes("method");
            if (nodeMethods != null && nodeMethods.size() > 0) {
                List<CommonMethod> methods = new ArrayList<>();
                for (Node nodeMethod : nodeMethods) {
                    CommonMethod method = processCommonMethod(nodeMethod);
                    methods.add(method);
                }
                service.setMethods(methods);
            }
            this.serviceMap.put(name, service);
        }
    }

    private PacketObject processPO(Element elePacket) {
        PacketObject restObject = new PacketObject();
        Map<String, PacketField> fields = new HashMap<>();
        Map<String, PacketObject> lists = new HashMap<>();
        Map<String, PacketObject> objects = new HashMap<>();
        restObject.setName(elePacket.getName());
        restObject.set_class(elePacket.attributeValue("class"));
        restObject.setParent(elePacket.attributeValue("parent"));
        String _new = elePacket.attributeValue("new");
        if (_new != null && !_new.equals("")) {
            restObject.set_new(Boolean.parseBoolean(_new));
        }
        String ref = elePacket.attributeValue("ref");
        if (ref != null && !ref.equals("")) {
            restObject.setRef(Boolean.parseBoolean(ref));
        }
        String remark = elePacket.attributeValue("remark");
        if (remark != null && !remark.equals("")) {
            restObject.setRemark(remark);
        }
        if (!restObject.isRef()) {
            List<Element> elements = elePacket.elements();
            for (Element element : elements) {
                if (element == null) {
                    continue;
                }
                String tagName = element.getName();
                if (tagName == null || tagName.equals("")) {
                    continue;
                }
                String eleName = element.attributeValue("name");
                if (eleName == null || eleName.equals("")) {
                    continue;
                }
                if (tagName.equals("list")) {
                    PacketObject list = processPO(element);
                    lists.put(eleName, list);
                } else if (tagName.equals("object")) {
                    PacketObject object = processPO(element);
                    objects.put(eleName, object);
                } else {
                    PacketField field = parsePacketField(element);
                    fields.put(field.getName(), field);
                }
            }
        }

        restObject.setFields(fields);
        restObject.setLists(lists);
        restObject.setObjects(objects);

        if (!restObject.isRef()) {
            packetMap.put(restObject.get_class(), restObject);
        }

        return restObject;
    }

    /**
     * @param name
     * @return 根据name得到属性名 规则：去掉"_"或者"-"；第一个字母小写，其他单词首字母大写
     */
    private static String parseFieldName(String name) {
        StringBuilder sb = new StringBuilder();

        if ((name != null) && (name.length() > 0)) {
            String[] cName = name.toLowerCase().split("_|-");
            if (cName.length > 1) {
                for (int i = 0; i < cName.length; i++) {
                    if ((cName[i] != null) && (cName[i].length() > 0)) {
                        if (i != 0) {
                            sb.append(cName[i].substring(0, 1).toUpperCase() + cName[i].substring(1));
                        } else {
                            sb.append(cName[i]);
                        }
                    }
                }
            } else {
                sb.append(name);
            }
        }

        return sb.toString();
    }

    private void refineRef(PacketObject po) {
        Map<String, PacketObject> objects = po.getObjects();
        for (String obKey : objects.keySet()) {
            PacketObject object = objects.get(obKey);
            if (object.isRef()) {
                objects.put(obKey, this.packetMap.get(object.get_class()));
            } else {
                refineRef(object);
            }
        }
        Map<String, PacketObject> lists = po.getLists();
        for (String listKey : lists.keySet()) {
            PacketObject list = lists.get(listKey);
            if (list.isRef()) {
                lists.put(listKey, this.packetMap.get(list.get_class()));
            } else {
                refineRef(list);
            }
        }
    }

    private CommonMethod processCommonMethod(Node nodeMethod) {
        CommonMethod method = new CommonMethod();

        Element eleMethod = (Element) nodeMethod;
        String methodName = eleMethod.attributeValue("name");
        String version = eleMethod.attributeValue("version");
        String path = eleMethod.attributeValue("path");
        String remark = eleMethod.attributeValue("remark");
        String httpMethod = eleMethod.attributeValue("method");
        String reqType = eleMethod.attributeValue("reqType");
        String resType = eleMethod.attributeValue("resType");
        String needApi = eleMethod.attributeValue("needApi");
        method.setName(methodName);
        method.setVersion(version);
        method.setPath(path);
        method.setRemark(remark);
        if (httpMethod != null && !httpMethod.equals("")) {
            method.setHttpMethod(httpMethod);
        } else {
            method.setHttpMethod("POST");
        }
        Element eleRequest = eleMethod.element("request");
        if (eleRequest != null) {
            String requestBodyClass = eleRequest.attributeValue("class");
            if (requestBodyClass.endsWith(":List")) {
                requestBodyClass = requestBodyClass.split(":")[0];
                method.setRequestIsList(true);
            } else {
                method.setRequestIsList(false);
            }
            if (requestBodyClass.endsWith(":Array")) {
                requestBodyClass = requestBodyClass.split(":")[0];
                method.setRequestIsArray(true);
            } else {
                method.setRequestIsArray(false);
            }
            PacketObject po = this.packetMap.get(requestBodyClass);
            method.setRequest(po);
        }
        Element eleResponse = eleMethod.element("response");
        if (eleResponse != null) {
            String responseBodyClass = eleResponse.attributeValue("class");
            if (responseBodyClass.endsWith(":Page")) {
                responseBodyClass = responseBodyClass.split(":")[0];
                method.setResponseIsPage(true);
            } else {
                method.setResponseIsPage(false);
            }
            if (responseBodyClass.endsWith(":List")) {
                responseBodyClass = responseBodyClass.split(":")[0];
                method.setResponseIsArray(true);
            } else {
                method.setResponseIsArray(false);
            }
            PacketObject po = this.packetMap.get(responseBodyClass);
            method.setResponse(po);
        }

        if (httpMethod != null && !httpMethod.equals("")) {
            method.setHttpMethod(httpMethod);
        } else {
            method.setHttpMethod("POST");
        }
        if (reqType != null && !reqType.equals("")) {

            method.setReqType(contentType.get(reqType));
        } else {
            method.setReqType(MediaTypes.JSON_UTF_8);
        }
        if (resType != null && !resType.equals("")) {
            method.setResType(contentType.get(resType));
        } else {
            method.setResType(MediaTypes.JSON_UTF_8);
        }
        if (needApi != null && !needApi.equals("")) {
            method.setNeedApi(Boolean.parseBoolean(needApi));
        }
        Element eleParametersRoot = eleMethod.element("parameters");
        if (eleParametersRoot != null) {
            List<PacketField> packetFields = parsePacketFields(eleParametersRoot, "parameter");
            method.setParameters(packetFields);
        }
        Element elePathVariablesRoot = eleMethod.element("pathVariables");
        if (elePathVariablesRoot != null) {
            List<PacketField> packetFields = parsePacketFields(elePathVariablesRoot, "pathVariable");
            method.setPathVariables(packetFields);
        }
        Element eleHeadVariablesRoot = eleMethod.element("headVariables");
        if (eleHeadVariablesRoot != null) {
            List<PacketField> packetFields = parsePacketFields(eleHeadVariablesRoot, "headVariable");
            method.setHeadVariables(packetFields);
        }

        Element eleCookieVariablesRoot = eleMethod.element("cookieVariables");
        if (eleCookieVariablesRoot != null) {
            List<PacketField> packetFields = parsePacketFields(eleCookieVariablesRoot, "cookieVariable");
            method.setCookieVariables(packetFields);
        }

        Element eleWebParametersRoot = eleMethod.element("webParameters");
        if (eleWebParametersRoot != null) {
            List<PacketField> packetFields = parsePacketFields(eleWebParametersRoot, "webParameter");
            method.setWebParameters(packetFields);
        }

        return method;
    }

    private static List<PacketField> parsePacketFields(Element eleParametersRoot, String parameter) {
        List<PacketField> packetFields = new ArrayList<>();
        List<Element> eleVariables = eleParametersRoot.elements(parameter);
        for (int index = 0; index < eleVariables.size(); index++) {
            PacketField packetField = parsePacketField(eleVariables.get(index));
            packetField.setIndex(index);
            packetFields.add(packetField);
        }
        return packetFields;
    }

    public void doParse() {
        this.initBundleRoots();
        this.processPacket();
        for (int i = 0; i < this.bundleRoots.length; i++) {
            this.processService(this.bundleRoots[i]);
        }
        for (int i = 0; i < this.bundleRoots.length; i++) {
            this.processControllers(this.bundleRoots[i]);
        }
    }
}
