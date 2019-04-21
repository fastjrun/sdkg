/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.codeg.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.springframework.http.MediaType;

import com.fastjrun.codeg.common.CodeGConstants;
import com.fastjrun.codeg.common.CodeGException;
import com.fastjrun.codeg.common.CodeGMsgContants;
import com.fastjrun.codeg.common.CommonController;
import com.fastjrun.codeg.common.CommonMethod;
import com.fastjrun.codeg.common.CommonService;
import com.fastjrun.codeg.common.PacketField;
import com.fastjrun.codeg.common.PacketObject;

public class BundleXMLParser implements CodeGConstants {

    private Map<String, ControllerType> controllerTypeMap = new HashMap<>();

    private Map<String, String> contentType = new HashMap<>();

    private String bundleFile;

    private Element bundleRoot;

    private Map<String, PacketObject> packetMap = new HashMap<>();

    private Map<String, CommonService> serviceMap = new HashMap<>();

    private Map<String, CommonController> controllerMap = new HashMap<>();

    public Map<String, ControllerType> getControllerTypeMap() {
        return controllerTypeMap;
    }

    private List<PacketField> parsePacketFields(Element elePacketFieldsRoot, String elePacketFieldName) {
        List<PacketField> packetFields = new ArrayList<>();
        List<Element> eleCookieVariables = elePacketFieldsRoot.elements(elePacketFieldName);
        for (int index = 0; index < eleCookieVariables.size(); index++) {
            Element element = eleCookieVariables.get(index);
            String fieldName = element.attributeValue("name");
            String fieldNameAlias = element.attributeValue("nameAlias");
            String datatype = element.attributeValue("dataType");
            String length = element.attributeValue("length");
            String canBeNull = element.attributeValue("canBeNull");
            String parameterRemark = element.attributeValue("remark");
            PacketField field = new PacketField();
            field.setName(fieldName);
            if (fieldNameAlias != null && !fieldNameAlias.equals("")) {
                field.setNameAlias(fieldNameAlias);
            } else {
                field.setNameAlias(fieldName);
            }
            field.setDatatype(datatype);
            field.setLength(length);
            if (canBeNull != null && !canBeNull.equals("")) {
                field.setCanBeNull(Boolean.parseBoolean(canBeNull));
            }
            field.setRemark(parameterRemark);
            field.setIndex(index);
            packetFields.add(field);
        }
        return packetFields;
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

    public void setControllerMap(
            Map<String, CommonController> controllerMap) {
        this.controllerMap = controllerMap;
    }

    public void setBundleFile(String bundleFile) {
        this.bundleFile = bundleFile;
    }

    public void init() {
        this.contentType.put("json", MediaType.APPLICATION_JSON_UTF8_VALUE);
        this.contentType.put("xml", MediaType.APPLICATION_XML_VALUE);
        this.contentType.put("json,xml", MediaType.APPLICATION_XML_VALUE + "," + MediaType.APPLICATION_XML_VALUE);
        this.controllerTypeMap.put(CodeGConstants.ControllerType_APP.name, CodeGConstants.ControllerType_APP);
        this.controllerTypeMap.put(CodeGConstants.ControllerType_API.name, CodeGConstants.ControllerType_API);
        this.controllerTypeMap.put(CodeGConstants.ControllerType_GENERIC.name, CodeGConstants.ControllerType_GENERIC);
        this.controllerTypeMap.put(CodeGConstants.ControllerType_DUBBO.name, CodeGConstants.ControllerType_DUBBO);
        this.processExtControllerType();
    }

    public void processExtControllerType() {
    }

    private void initBundleRoot() {
        SAXReader reader = new SAXReader();
        Document document;
        try {
            document = reader.read(this.bundleFile);
        } catch (DocumentException e) {
            throw new CodeGException(CodeGMsgContants.CODEG_BUNDLEFILE_INVALID, "bundleFile is wrong");
        }
        this.bundleRoot = document.getRootElement();
    }

    public Map<String, Element> checkClassNameRepeat() {
        Map<String, Element> classMap = new HashMap<>();
        List<Node> nodePackets = this.bundleRoot.selectNodes("packets/packet");
        for (Node nodePacket : nodePackets) {
            Map<String, Element> childClassMap = checkClassNameRepeatInPO((Element) nodePacket);
            Iterator<String> itera = childClassMap.keySet().iterator();
            while (itera.hasNext()) {
                String childClassName = itera.next();
                if (classMap.keySet().contains(childClassName)) {
                    throw new CodeGException(CodeGMsgContants.CODEG_CLASS_DUPLICATED,
                            childClassName + " is duplicated");
                }
                classMap.put(childClassName, childClassMap.get(childClassName));
            }
        }
        List<Node> nodeServices = this.bundleRoot.selectNodes("services/service");
        for (Node nodeService : nodeServices) {
            String classP = ((Element) nodeService).attributeValue("class");
            if (classMap.keySet().contains(classP)) {
                throw new CodeGException(CodeGMsgContants.CODEG_CLASS_DUPLICATED,
                        "service." + classP + " is duplicated");
            }
            classMap.put(classP, (Element) nodeService);

        }
        List<Node> nodeControllers = this.bundleRoot.selectNodes("*/controller");
        for (Node nodeController : nodeControllers) {
            String classP = ((Element) nodeController).attributeValue("name");
            if (classMap.keySet().contains(classP)) {
                throw new CodeGException(CodeGMsgContants.CODEG_CLASS_DUPLICATED,
                        "web.controller." + classP + " is duplicated");
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
                        throw new CodeGException(CodeGMsgContants.CODEG_CLASS_DUPLICATED,
                                childClassName + " is duplicated");
                    }
                    classMap.put(childClassName, childClassMap.get(childClassName));
                }
            }
        }
        return classMap;
    }

    private void processControllers() {
        List<Node> nodeControllers = this.bundleRoot.selectNodes("controllers/controller");
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
            commonController.setTags(tags);
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
            List<CommonController> commonControllers = service.getCommonControllers();
            commonControllers.add(commonController);
            this.controllerMap.put(name, commonController);
        }
    }

    private void processPacket() {
        List<Node> nodePackets = this.bundleRoot.selectNodes("packets/packet");
        for (Node nodePacket : nodePackets) {
            Element elePacket = (Element) nodePacket;
            processPO(elePacket);
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

    private void processService() {
        List<Node> nodeServices = this.bundleRoot.selectNodes("services/service");
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
                    CommonMethod method = processControllerMethod(nodeMethod);
                    methods.add(method);
                }
                service.setMethods(methods);
            }
            List<CommonController> commonControllers = new ArrayList<>();
            service.setCommonControllers(commonControllers);
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
                String fieldName = element.attributeValue("name");
                String dataType = element.attributeValue("dataType");
                String length = element.attributeValue("length");
                String canBeNull = element.attributeValue("canBeNull");
                String remark = element.attributeValue("remark");
                String getter = element.attributeValue("getter");
                String setter = element.attributeValue("setter");
                PacketField field = new PacketField();
                field.setName(fieldName);
                field.setDatatype(dataType);
                field.setLength(length);
                field.setCanBeNull(Boolean.parseBoolean(canBeNull));
                field.setRemark(remark);
                field.setSetter(setter);
                field.setGetter(getter);
                fields.put(fieldName, field);
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

    private CommonMethod processControllerMethod(Node nodeMethod) {
        CommonMethod method = new CommonMethod();

        Element eleMethod = (Element) nodeMethod;
        String methodName = eleMethod.attributeValue("name");
        String version = eleMethod.attributeValue("version");
        String path = eleMethod.attributeValue("path");
        String remark = eleMethod.attributeValue("remark");
        String httpMethod = eleMethod.attributeValue("method");
        String reqType = eleMethod.attributeValue("reqType");
        String resType = eleMethod.attributeValue("resType");
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
            PacketObject po = this.packetMap.get(requestBodyClass);
            method.setRequest(po);
        }
        Element eleResponse = eleMethod.element("response");
        if (eleResponse != null) {
            String responseBodyClass = eleResponse.attributeValue("class");
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
            method.setReqType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        }
        if (resType != null && !resType.equals("")) {
            method.setResType(contentType.get(resType));
        } else {
            method.setResType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        }
        Element eleParametersRoot = eleMethod.element("parameters");
        if (eleParametersRoot != null) {
            List<PacketField> parameters = parsePacketFields(eleParametersRoot, "parameter");
            method.setParameters(parameters);
        }
        Element elePathVariablesRoot = eleMethod.element("pathVariables");
        if (elePathVariablesRoot != null) {
            List<PacketField> pathVariables = parsePacketFields(eleParametersRoot, "pathVariable");
            method.setPathVariables(pathVariables);
        }
        Element eleHeadVariablesRoot = eleMethod.element("headVariables");
        if (eleHeadVariablesRoot != null) {
            List<PacketField> headVariables = parsePacketFields(eleHeadVariablesRoot, "headVariable");
            method.setHeadVariables(headVariables);
        }

        Element eleCookieVariablesRoot = eleMethod.element("cookieVariables");
        if (eleCookieVariablesRoot != null) {
            List<PacketField> cookieVariables = parsePacketFields(eleCookieVariablesRoot, "cookieVariable");
            method.setCookieVariables(cookieVariables);
        }

        Element eleExtraParametersRoot = eleMethod.element("extraParameters");
        if (eleExtraParametersRoot != null) {
            List<PacketField> extraParameters = parsePacketFields(eleExtraParametersRoot, "parameter");
            method.setExtraParameters(extraParameters);
        }

        return method;
    }

    public void doParse() {
        this.initBundleRoot();
        this.processPacket();
        this.processService();
        this.processControllers();
    }

}
