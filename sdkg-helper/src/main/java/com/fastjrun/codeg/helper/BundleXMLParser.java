package com.fastjrun.codeg.helper;

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

public class BundleXMLParser {

    static Map<String, String> contentType = new HashMap<>();

    static {
        contentType.put("json", MediaType.APPLICATION_JSON_UTF8_VALUE);
        contentType.put("xml", MediaType.APPLICATION_XML_VALUE);
        contentType.put("json,xml", MediaType.APPLICATION_XML_VALUE + "," + MediaType.APPLICATION_XML_VALUE);
    }

    private static Element getbundleRoot(String bundleFile) {
        SAXReader reader = new SAXReader();
        Document document;
        try {
            document = reader.read(bundleFile);
        } catch (DocumentException e) {
            throw new CodeGException(CodeGMsgContants.CODEG_BUNDLEFILE_INVALID, "bundleFile is wrong");
        }
        return document.getRootElement();
    }

    public static Map<String, Element> checkClassNameRepeat(String[] bundleFiles) {
        Map<String, Element> classMap = new HashMap<>();
        for (String bundleFile : bundleFiles) {
            Map<String, Element> indexMap = checkClassNameRepeat(bundleFile);
            Iterator<String> itera = indexMap.keySet().iterator();
            while (itera.hasNext()) {
                String indexName = itera.next();
                if (classMap.keySet().contains(indexName)) {
                    throw new CodeGException(CodeGMsgContants.CODEG_CLASS_DUPLICATED, indexName + " is duplicated");
                }
                classMap.put(indexName, indexMap.get(indexName));
            }
        }
        return classMap;
    }

    public static Map<String, Element> checkClassNameRepeat(String bundleFile) {
        Element xml = getbundleRoot(bundleFile);
        Map<String, Element> classMap = new HashMap<>();
        List<Node> nodePackets = xml.selectNodes("packets/packet");
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
        List<Node> nodeServices = xml.selectNodes("services/service");
        for (Node nodeService : nodeServices) {
            String classP = ((Element) nodeService).attributeValue("class");
            if (classMap.keySet().contains(classP)) {
                throw new CodeGException(CodeGMsgContants.CODEG_CLASS_DUPLICATED,
                        "service." + classP + " is duplicated");
            }
            classMap.put(classP, (Element) nodeService);

        }
        List<Node> nodeControllers = xml.selectNodes("*/controller");
        for (Node nodeController : nodeControllers) {
            String classP = ((Element) nodeController).attributeValue("name");
            if (classMap.keySet().contains(classP)) {
                throw new CodeGException(CodeGMsgContants.CODEG_CLASS_DUPLICATED,
                        "web.controller." + classP + " is duplicated");
            }
            classMap.put(classP, (Element) nodeController);
        }
        List<Node> nodeDubbos = xml.selectNodes("*/dubbo");
        for (Node nodeController : nodeDubbos) {
            String classP = ((Element) nodeController).attributeValue("name");
            if (classMap.keySet().contains(classP)) {
                throw new CodeGException(CodeGMsgContants.CODEG_CLASS_DUPLICATED, "biz." + classP + " is duplicated");
            }
            classMap.put(classP, (Element) nodeController);
        }
        return classMap;

    }

    private static Map<String, Element> checkClassNameRepeatInPO(Element po) {
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

    public static Map<String, CommonController> processControllers(CodeGConstants.ControllerType controllerType,
                                                                   String bundleFile,
                                                                   Map<String, CommonService> serviceMap) {

        Element xml = getbundleRoot(bundleFile);
        List<Node> nodeControllers = null;
        if (controllerType == CodeGConstants.ControllerType.ControllerType_API) {
            nodeControllers = xml.selectNodes("apiControllers/controller");
        } else if (controllerType == CodeGConstants.ControllerType.ControllerType_APP) {
            nodeControllers = xml.selectNodes("appControllers/controller");
        } else if (controllerType == CodeGConstants.ControllerType.ControllerType_GENERIC) {
            nodeControllers = xml.selectNodes("genericControllers/controller");
        }
        return pushController2Map(nodeControllers, controllerType, serviceMap);
    }

    public static Map<String, CommonController> processPPCs(CodeGConstants.RpcType rpcType, String bundleFile,
                                                            Map<String, CommonService> serviceMap) {

        Element xml = getbundleRoot(bundleFile);
        List<Node> nodeControllers = null;
        CodeGConstants.ControllerType controllerType = CodeGConstants.ControllerType.ControllerType_DUBBO;
        if (rpcType == CodeGConstants.RpcType.RpcType_Dubbo) {
            nodeControllers = xml.selectNodes("dubbos/dubbo");
        } else if (rpcType == CodeGConstants.RpcType.RpcType_Grpc) {
            nodeControllers = xml.selectNodes("grpcs/grpc");
        }

        return pushController2Map(nodeControllers, controllerType, serviceMap);
    }

    private static Map<String, CommonController> pushController2Map(List<Node> controllerNodes, CommonController
            .ControllerType controllerType,
                                                                    Map<String, CommonService> serviceMap) {
        Map<String, CommonController> controllerMap = new HashMap<>();
        for (Node controllerNode : controllerNodes) {
            Element eleController = (Element) controllerNode;
            CommonController commonController = new CommonController();
            commonController.setControllerType(controllerType);
            String name = eleController.attributeValue("name");
            String path = eleController.attributeValue("path");
            String version = eleController.attributeValue("version");
            String remark = eleController.attributeValue("remark");
            String clientName = eleController.attributeValue("clientName");
            String tags = eleController.attributeValue("tags");
            commonController.setName(name);
            commonController.setPath(path);
            commonController.setVersion(version);
            commonController.setRemark(remark);
            commonController.setClientName(clientName);
            commonController.setTags(tags);
            Element eleService = eleController.element("service");
            String serviceName = eleService.attributeValue("name");
            commonController.setServiceName(serviceName);
            String serviceRef = eleService.attributeValue("ref");
            commonController.setServiceRef(serviceRef);

            CommonService service = serviceMap.get(serviceRef);

            commonController.setService(service);

            controllerMap.put(name, commonController);
        }
        return controllerMap;
    }

    public static Map<String, PacketObject> processPacket(String bundleFile) {

        Element xml = getbundleRoot(bundleFile);
        Map<String, PacketObject> packetMap = new HashMap<>();
        List<Node> nodePackets = xml.selectNodes("packets/packet");
        for (Node nodePacket : nodePackets) {
            Element elePacket = (Element) nodePacket;
            String classP = elePacket.attributeValue("class");
            String parent = elePacket.attributeValue("parent");
            PacketObject restObject = processPO(elePacket);
            restObject.setParent(parent);
            packetMap.put(classP, restObject);
        }
        return packetMap;

    }

    public static Map<String, CommonService> processService(String bundleFile, Map<String, PacketObject> packetMap) {

        Element xml = getbundleRoot(bundleFile);
        Map<String, CommonService> serviceMap = new HashMap<>();
        List<Node> nodeServices = xml.selectNodes("services/service");
        for (Node nodeService : nodeServices) {
            Element eleService = (Element) nodeService;
            String name = eleService.attributeValue("name");
            String classP = eleService.attributeValue("class");
            CommonService service = new CommonService();

            service.set_class(classP);
            service.setName(name);
            List<Node> nodeMethods = eleService.selectNodes("method");
            if (nodeMethods != null && nodeMethods.size() > 0) {
                List<CommonMethod> methods = new ArrayList<CommonMethod>();
                for (Node nodeMethod : nodeMethods) {
                    CommonMethod method = processControllerMethod(nodeMethod, packetMap);
                    methods.add(method);
                }
                service.setMethods(methods);
            }
            serviceMap.put(name, service);

        }
        return serviceMap;

    }

    private static PacketObject processPO(Element elePacket) {
        PacketObject restObject = new PacketObject();
        Map<String, PacketField> fields = new HashMap<>();
        Map<String, PacketObject> lists = new HashMap<>();
        Map<String, PacketObject> objects = new HashMap<>();
        restObject.setName(elePacket.getName());
        restObject.set_class(elePacket.attributeValue("class"));
        restObject.setParent(elePacket.attributeValue("parent"));
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
                PacketField field = new PacketField();
                field.setName(fieldName);
                field.setDatatype(dataType);
                field.setLength(length);
                field.setCanBeNull(Boolean.parseBoolean(canBeNull));
                field.setRemark(remark);
                fields.put(fieldName, field);
            }
        }
        restObject.setFields(fields);
        restObject.setLists(lists);
        restObject.setObjects(objects);
        return restObject;
    }

    private static CommonMethod processControllerMethod(Node nodeMethod, Map<String, PacketObject> packetMap) {
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
            String requestClass = eleRequest.attributeValue("class");
            method.setRequest(packetMap.get(requestClass));
        }
        Element eleResponse = eleMethod.element("response");
        if (eleResponse != null) {
            String responseClass = eleResponse.attributeValue("class");
            if (responseClass.endsWith(":List")) {
                responseClass = responseClass.split(":")[0];
                method.setResponseIsArray(true);
            } else {
                method.setResponseIsArray(false);
            }
            method.setResponse(packetMap.get(responseClass));
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
            List<Element> eleParameters = eleParametersRoot.elements("parameter");
            List<PacketField> parameters = new ArrayList<PacketField>();
            for (int index = 0; index < eleParameters.size(); index++) {
                Element eleParameter = eleParameters.get(index);
                String fieldName = eleParameter.attributeValue("name");
                String datatype = eleParameter.attributeValue("dataType");
                String length = eleParameter.attributeValue("length");
                String canBeNull = eleParameter.attributeValue("canBeNull");
                String parameterRemark = eleParameter.attributeValue("remark");
                PacketField field = new PacketField();
                field.setName(fieldName);
                field.setDatatype(datatype);
                field.setLength(length);
                field.setCanBeNull(Boolean.parseBoolean(canBeNull));
                field.setRemark(parameterRemark);
                parameters.add(field);
            }
            method.setParameters(parameters);
        }
        Element elePathVariablesRoot = eleMethod.element("pathVariables");
        if (elePathVariablesRoot != null) {
            List<PacketField> pathVariables = new ArrayList<PacketField>();
            List<Element> elePathVariables = elePathVariablesRoot.elements("pathVariable");
            for (int index = 0; index < elePathVariables.size(); index++) {
                Element elePathVariable = elePathVariables.get(index);
                String fieldName = elePathVariable.attributeValue("name");
                String datatype = elePathVariable.attributeValue("dataType");
                String length = elePathVariable.attributeValue("length");
                String parameterRemark = elePathVariable.attributeValue("remark");
                PacketField field = new PacketField();
                field.setIndex(index);
                field.setName(fieldName);
                field.setDatatype(datatype);
                field.setLength(length);
                field.setRemark(parameterRemark);
                pathVariables.add(field);
            }
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

        return method;
    }

    private static List<PacketField> parsePacketFields(Element elePacketFieldsRoot, String elePacketFieldName) {
        List<PacketField> packetFields = new ArrayList<>();
        List<Element> eleCookieVariables = elePacketFieldsRoot.elements(elePacketFieldName);
        for (int index = 0; index < eleCookieVariables.size(); index++) {
            Element element = eleCookieVariables.get(index);
            String tagName = element.getName();
            String fieldName = element.attributeValue("name");
            String fieldNameAlias = element.attributeValue("nameAlias");
            String datatype = element.attributeValue("dataType");
            String length = element.attributeValue("length");
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
            field.setRemark(parameterRemark);
            field.setIndex(index);
            packetFields.add(field);
        }
        return packetFields;
    }

}
