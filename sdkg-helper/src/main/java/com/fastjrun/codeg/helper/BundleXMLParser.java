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

import com.fastjrun.codeg.CodeGException;
import com.fastjrun.codeg.bundle.common.CommonController;
import com.fastjrun.codeg.bundle.common.CommonController.ControllerType;
import com.fastjrun.codeg.bundle.common.CommonMethod;
import com.fastjrun.codeg.bundle.common.CommonService;
import com.fastjrun.codeg.bundle.common.CommonService.ServiceType;
import com.fastjrun.codeg.bundle.common.PacketField;
import com.fastjrun.codeg.bundle.common.PacketObject;

public class BundleXMLParser {

    static Map<String, String> contentType = new HashMap<String, String>();

    static {
        contentType.put("json", MediaType.APPLICATION_JSON_UTF8_VALUE);
        contentType.put("xml", MediaType.APPLICATION_XML_VALUE);
        contentType.put("json,xml", MediaType.APPLICATION_XML_VALUE + "," + MediaType.APPLICATION_XML_VALUE);
    }

    public static Map<String, Element> checkClassNameRepeat(String[] bundleFiles) {
        Map<String, Element> classMap = new HashMap<String, Element>();
        for (String bundleFile : bundleFiles) {
            Map<String, Element> indexMap = checkClassNameRepeat(bundleFile);
            Iterator<String> itera = indexMap.keySet().iterator();
            while (itera.hasNext()) {
                String indexName = itera.next();
                if (classMap.keySet().contains(indexName)) {
                    throw new CodeGException("CG501", indexName + " is duplicated");
                }
                classMap.put(indexName, indexMap.get(indexName));
            }
        }
        return classMap;
    }

    public static Map<String, Element> checkClassNameRepeat(String bundleFile) {
        Map<String, Element> classMap = new HashMap<String, Element>();
        SAXReader reader = new SAXReader();
        try {
            Document document = reader.read(bundleFile);
            Element xml = document.getRootElement();
            List<Node> nodePackets = xml.selectNodes("packets/packet");
            for (Node nodePacket : nodePackets) {
                Map<String, Element> childClassMap = checkClassNameRepeatInPO((Element) nodePacket);
                Iterator<String> itera = childClassMap.keySet().iterator();
                while (itera.hasNext()) {
                    String childClassName = itera.next();
                    if (classMap.keySet().contains(childClassName)) {
                        throw new CodeGException("CG501", childClassName + " is duplicated");
                    }
                    classMap.put(childClassName, childClassMap.get(childClassName));
                }
            }
            List<Node> nodeServices = xml.selectNodes("services/*/service");
            for (Node nodeService : nodeServices) {
                String _class = ((Element) nodeService).attributeValue("class");
                if (classMap.keySet().contains(_class)) {
                    throw new CodeGException("CG501", "service." + _class + " is duplicated");
                }
                classMap.put(_class, (Element) nodeService);

            }
            List<Node> nodeControllers = xml.selectNodes("*/controller");
            for (Node nodeController : nodeControllers) {
                String _class = ((Element) nodeController).attributeValue("name");
                if (classMap.keySet().contains(_class)) {
                    throw new CodeGException("CG501", "web.controller." + _class + " is duplicated");
                }
                classMap.put(_class, (Element) nodeController);
            }
            return classMap;
        } catch (DocumentException e) {
            throw new CodeGException("CG500", "system error");
        }
    }

    private static Map<String, Element> checkClassNameRepeatInPO(Element po) {
        List<Element> elements = po.elements();
        Map<String, Element> classMap = new HashMap<String, Element>();
        String _class = po.attributeValue("class");
        if (classMap.keySet().contains(_class)) {
            throw new CodeGException("CG501", _class + " is duplicated");
        }
        classMap.put(_class, po);
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
                        throw new CodeGException("CG501", childClassName + " is duplicated");
                    }
                    classMap.put(childClassName, childClassMap.get(childClassName));
                }
            }
        }
        return classMap;
    }

    public static Map<String, CommonController> processControllers(String bundleFile,
                                                                   Map<String, CommonService> serviceMap) {
        Map<String, CommonController> controllerMap = new HashMap<String, CommonController>();
        SAXReader reader = new SAXReader();
        try {
            Document document = reader.read(bundleFile);

            Element xml = document.getRootElement();
            List<Node> nodeControllers = xml.selectNodes("*/controller");
            if (nodeControllers != null && nodeControllers.size() > 0) {
                for (Node nodeController : nodeControllers) {
                    Element eleController = (Element) nodeController;
                    CommonController commonController = new CommonController();
                    if (eleController.getParent().getName().equals("appControllers")) {
                        commonController.setControllerType(CommonController.ControllerType.ControllerType_APP);
                    } else if (eleController.getParent().getName().equals("apiControllers")) {
                        commonController.setControllerType(CommonController.ControllerType.ControllerType_API);
                    } else if (eleController.getParent().getName().equals("genericControllers")) {
                        commonController.setControllerType(CommonController.ControllerType.ControllerType_GENERIC);
                    }
                    String name = eleController.attributeValue("name");
                    String path = eleController.attributeValue("path");
                    String remark = eleController.attributeValue("remark");
                    String clientName = eleController.attributeValue("clientName");
                    String clientParent = eleController.attributeValue("clientParent");
                    String tags = eleController.attributeValue("tags");
                    commonController.setName(name);
                    commonController.setPath(path);
                    commonController.setRemark(remark);
                    commonController.setClientName(clientName);
                    commonController.setTags(tags);
                    Element eleService = eleController.element("service");
                    String serviceName = eleService.attributeValue("name");
                    commonController.setServiceName(serviceName);
                    String serviceRef = eleService.attributeValue("ref");

                    CommonService service = serviceMap.get(serviceRef);

                    commonController.setService(service);

                    controllerMap.put(name, commonController);

                }
            }
            List<Node> nodeServices = xml.selectNodes("services/*/service");
            for (Node nodeService : nodeServices) {
                Element eleService = (Element) nodeService;
                if (eleService.getParent().getName().equals("rpcServices")) {
                    CommonController commonController = new CommonController();
                    commonController.setControllerType(ControllerType.ControllerType_RPC);
                    String name = eleService.attributeValue("name");
                    commonController.setName(StringHelper.toUpperCaseFirstOne(name) + "RPCController");
                    commonController.setPath("/" + name);
                    commonController.setRemark("rpc服务");
                    commonController.setClientName(StringHelper.toUpperCaseFirstOne(name) + "RPCClient");
                    commonController.setTags("rpc");
                    commonController.setServiceName(name);
                    CommonService service = serviceMap.get(name);
                    commonController.setService(service);
                    controllerMap.put(commonController.getName(), commonController);
                }

            }
        } catch (Exception e) {
            throw new CodeGException("CG502", "controller error:" + e.getMessage());
        }

        return controllerMap;
    }

    public static Map<String, PacketObject> processPacket(String bundleFile) {
        Map<String, PacketObject> packetMap = new HashMap<String, PacketObject>();
        SAXReader reader = new SAXReader();
        try {
            Document document = reader.read(bundleFile);

            Element xml = document.getRootElement();
            List<Node> nodePackets = xml.selectNodes("packets/packet");
            for (Node nodePacket : nodePackets) {
                Element elePacket = (Element) nodePacket;
                String _class = elePacket.attributeValue("class");
                String parent = elePacket.attributeValue("parent");
                PacketObject restObject = processPO(elePacket);
                restObject.setParent(parent);
                packetMap.put(_class, restObject);
            }
            List<Node> nodeServices = xml.selectNodes("services/*/service");
            for (Node nodeService : nodeServices) {
                Element eleService = (Element) nodeService;
                List<Node> nodeMethods = eleService.selectNodes("method");
                if (nodeMethods != null && nodeMethods.size() > 0) {
                    for (Node nodeMethod : nodeMethods) {
                        Element eleMethod = (Element) nodeMethod;
                        Element eleRequest = eleMethod.element("request");
                        if (eleRequest != null) {
                            String requestClass = eleRequest.attributeValue("class");
                            PacketObject poRequest = packetMap.get(requestClass);
                            Element eleResponse = eleMethod.element("response");
                            if (eleResponse != null) {
                                String responseClass = eleResponse.attributeValue("class");
                                poRequest.setResponseClass(responseClass);
                            } else {
                                poRequest.setResponseClass("com.fastjrun.packet.EmptyResponseBody");

                            }
                        }

                    }
                }

            }
            return packetMap;
        } catch (Exception e) {
            throw new CodeGException("CG500", "packet error:" + e.getMessage());
        }
    }

    public static Map<String, CommonService> processService(String bundleFile, Map<String, PacketObject> packetMap) {
        Map<String, CommonService> serviceMap = new HashMap<String, CommonService>();
        SAXReader reader = new SAXReader();
        try {
            Document document = reader.read(bundleFile);

            Element xml = document.getRootElement();
            List<Node> nodeServices = xml.selectNodes("services/*/service");
            for (Node nodeService : nodeServices) {
                Element eleService = (Element) nodeService;
                String name = eleService.attributeValue("name");
                String _class = eleService.attributeValue("class");
                CommonService service = new CommonService();
                if (eleService.getParent().getName().equals("controllerServices")) {

                    service.setServiceType(ServiceType.ServiceType_Controller);

                } else if (eleService.getParent().getName().equals("rpcServices")) {
                    service.setServiceType(ServiceType.ServiceType_RPC);

                }

                service.set_class(_class);
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
        } catch (Exception e) {
            throw new CodeGException("CG501", "service error:" + e.getMessage());
        }
    }

    private static PacketObject processPO(Element elePacket) {
        PacketObject restObject = new PacketObject();
        Map<String, PacketField> fields = new HashMap<String, PacketField>();
        Map<String, PacketObject> lists = new HashMap<String, PacketObject>();
        Map<String, PacketObject> objects = new HashMap<String, PacketObject>();
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
                String datatype = element.attributeValue("dataType");
                String length = element.attributeValue("length");
                String canBeNull = element.attributeValue("canBeNull");
                String remark = element.attributeValue("remark");
                PacketField field = new PacketField();
                field.setName(fieldName);
                field.setDatatype(datatype);
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
            List<PacketField> headVariables = new ArrayList<PacketField>();
            List<Element> eleHeadVariables = eleHeadVariablesRoot.elements("headVariable");
            for (int index = 0; index < eleHeadVariables.size(); index++) {
                Element element = eleHeadVariables.get(index);
                String tagName = element.getName();
                if (tagName.equals("headVariable")) {
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
                    headVariables.add(field);
                }
            }
            method.setHeadVariables(headVariables);
        }

        return method;
    }

}
