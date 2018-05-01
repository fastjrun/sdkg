package com.fastjrun.codeg.helper;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import com.fastjrun.codeg.CodeGException;
import com.fastjrun.codeg.bundle.common.RestController;
import com.fastjrun.codeg.bundle.common.RestField;
import com.fastjrun.codeg.bundle.common.RestObject;
import com.fastjrun.codeg.bundle.common.RestService;
import com.fastjrun.codeg.bundle.common.RestServiceMethod;

public class BundleXMLParser {

    public static Map<String, RestController> processApp(String bundleFile,
            Set<String> classMap) {
        return processRest(bundleFile, "app", classMap);
    }

    public static Map<String, RestController> processApi(String bundleFile,
            Set<String> classMap) {
        return processRest(bundleFile, "api", classMap);
    }

    public static Map<String, RestController> processBase(String bundleFile,
            Set<String> classMap) {
        return processRest(bundleFile, "generic", classMap);
    }

    public static Map<String, RestController> processTX(String bundleFile,
            Set<String> classMap) {
        Map<String, RestController> restMap = new HashMap<String, RestController>();
        try {
            Document xml = Jsoup.parse(new FileInputStream(bundleFile), null, "",
                    Parser.xmlParser());
            Elements eleTxs = xml.getElementsByTag("txs");
            if (eleTxs != null && eleTxs.size() > 0) {
                Elements eleRests = eleTxs.first().getElementsByTag("tx");
                if (eleRests != null) {
                    for (Element eleRest : eleRests) {
                        RestController rest = new RestController();
                        String restName = eleRest.attr("name");
                        if (!classMap.add(restName)) {
                            throw new CodeGException("CG501", "tx." + restName
                                    + " is duplicated");
                        }
                        String remark = eleRest.attr("remark");
                        String clientName = eleRest.attr("clientName");
                        String clientParent = eleRest.attr("clientParent");
                        rest.setName(restName);
                        rest.setRemark(remark);
                        rest.setClientName(clientName);
                        rest.setClientParent(clientParent);
                        Element eleService = eleRest
                                .getElementsByTag("service").first();
                        List<RestServiceMethod> restServiceMethods = new ArrayList<RestServiceMethod>();
                        Elements eleServiceMethods = eleService
                                .getElementsByTag("method");
                        for (Element eleServiceMethod : eleServiceMethods) {
                            RestServiceMethod restServiceMethod = new RestServiceMethod();
                            String restServiceMethodName = eleServiceMethod
                                    .attr("name");
                            String restServiceMethodVersion = eleServiceMethod
                                    .attr("version");
                            String restServiceMethodPath = eleServiceMethod
                                    .attr("path");
                            String restServiceMethodRemark = eleServiceMethod
                                    .attr("remark");
                            restServiceMethod.setName(restServiceMethodName);
                            restServiceMethod
                                    .setVersion(restServiceMethodVersion);
                            restServiceMethod
                                    .setRemark(restServiceMethodRemark);
                            restServiceMethod.setPath(restServiceMethodPath);
                            Element eleRequest = eleServiceMethod
                                    .getElementsByTag("request").first();
                            RestObject request = processPacket(
                                    eleRequest.children(),
                                    eleRequest.attr("class"), classMap);
                            restServiceMethod.setRequest(request);
                            Element eleResponse = eleServiceMethod
                                    .getElementsByTag("response").first();
                            RestObject response = processPacket(
                                    eleResponse.children(),
                                    eleResponse.attr("class"), classMap);
                            restServiceMethod.setResponse(response);
                            restServiceMethods.add(restServiceMethod);
                        }
                        RestService restService = new RestService();
                        String restServiceName = eleService.attr("name");
                        String restServiceClass = eleService.attr("class");
                        if (!classMap.add(restServiceClass)) {
                            throw new CodeGException("CG501", "service."
                                    + restServiceClass + " is duplicated");
                        }
                        restService.setName(restServiceName);
                        restService.set_class(restServiceClass);
                        restService.setMethods(restServiceMethods);
                        rest.setRestService(restService);
                        restMap.put(restName, rest);
                    }
                }
            }
        } catch (IOException e) {
            throw new CodeGException("CG500", "system error:" + e.getMessage());
        }
        return restMap;
    }

    private static Map<String, RestController> processRest(String bundleFile,
            String prefix, Set<String> classMap) {
        Map<String, RestController> restMap = new HashMap<String, RestController>();
        try {
            Document xml = Jsoup.parse(new FileInputStream(bundleFile), null, "",
                    Parser.xmlParser());
            Elements eleApiControllers = xml.getElementsByTag(prefix
                    + "Controllers");
            if (eleApiControllers != null && eleApiControllers.size() > 0) {
                Elements eleRests = eleApiControllers.first().getElementsByTag(
                        prefix + "Controller");
                if (eleRests != null) {
                    for (Element eleRest : eleRests) {
                        RestController rest = new RestController();
                        String restName = eleRest.attr("name");
                        if (!classMap.add(restName)) {
                            throw new CodeGException("CG501", "web.controller."
                                    + restName + " is duplicated");
                        }
                        String path = eleRest.attr("path");
                        String remark = eleRest.attr("remark");
                        String clientName = eleRest.attr("clientName");
                        String clientParent = eleRest.attr("clientParent");
                        String tags = eleRest.attr("tags");
                        rest.setName(restName);
                        rest.setPath(path);
                        rest.setRemark(remark);
                        rest.setClientName(clientName);
                        rest.setClientParent(clientParent);
                        rest.setTags(tags);
                        Element eleService = eleRest
                                .getElementsByTag("service").first();
                        List<RestServiceMethod> restServiceMethods = new ArrayList<RestServiceMethod>();
                        Elements eleServiceMethods = eleService
                                .getElementsByTag("method");
                        for (Element eleServiceMethod : eleServiceMethods) {
                            RestServiceMethod restServiceMethod = new RestServiceMethod();
                            String restServiceMethodName = eleServiceMethod
                                    .attr("name");
                            String restServiceMethodVersion = eleServiceMethod
                                    .attr("version");
                            String restServiceMethodPath = eleServiceMethod
                                    .attr("path");
                            String restServiceMethodMethod = eleServiceMethod
                                    .attr("method");
                            String restServiceMethodRemark = eleServiceMethod
                                    .attr("remark");
                            restServiceMethod.setName(restServiceMethodName);
                            restServiceMethod
                                    .setVersion(restServiceMethodVersion);
                            restServiceMethod
                                    .setRemark(restServiceMethodRemark);
                            restServiceMethod.setPath(restServiceMethodPath);
                            if (restServiceMethodMethod != null
                                    && !restServiceMethodMethod.equals("")) {
                                restServiceMethod
                                        .setHttpMethod(restServiceMethodMethod);
                            } else {
                                restServiceMethod.setHttpMethod("POST");
                            }

                            if (eleServiceMethod.getElementsByTag("parameters") != null
                                    && eleServiceMethod.getElementsByTag(
                                            "parameters").size() > 0) {
                                Element eleParameters = eleServiceMethod
                                        .getElementsByTag("parameters").first();
                                Map<String, RestField> parameters = new HashMap<String, RestField>();
                                if (eleParameters.children() != null
                                        && eleParameters.children().size() > 0) {
                                    for (Element element : eleParameters
                                            .children()) {
                                        String tagName = element.tagName();
                                        if (tagName.equals("parameter")) {
                                            String fieldName = element
                                                    .attr("name");
                                            String datatype = element
                                                    .attr("dataType");
                                            String length = element
                                                    .attr("length");
                                            String canBeNull = element
                                                    .attr("canBeNull");
                                            String parameterRemark = element
                                                    .attr("remark");
                                            RestField field = new RestField();
                                            field.setName(fieldName);
                                            field.setDatatype(datatype);
                                            field.setLength(length);
                                            field.setCanBeNull(Boolean
                                                    .parseBoolean(canBeNull));
                                            field.setRemark(parameterRemark);
                                            parameters.put(fieldName, field);
                                        }
                                    }
                                }
                                restServiceMethod.setParameters(parameters);
                            }
                            if (eleServiceMethod
                                    .getElementsByTag("pathvariables") != null
                                    && eleServiceMethod.getElementsByTag(
                                            "pathvariables").size() > 0) {
                                Element elePathVariables = eleServiceMethod
                                        .getElementsByTag("pathvariables")
                                        .first();
                                List<RestField> pathVariables = new ArrayList<RestField>();
                                if (elePathVariables.children() != null
                                        && elePathVariables.children().size() > 0) {
                                    for (int index = 0; index < elePathVariables
                                            .children().size(); index++) {
                                        Element element = elePathVariables
                                                .child(index);
                                        String tagName = element.tagName();
                                        if (tagName.equals("pathvariable")) {
                                            String fieldName = element
                                                    .attr("name");
                                            String datatype = element
                                                    .attr("dataType");
                                            String length = element
                                                    .attr("length");
                                            String canBeNull = element
                                                    .attr("canBeNull");
                                            String parameterRemark = element
                                                    .attr("remark");
                                            RestField field = new RestField();
                                            field.setName(fieldName);
                                            field.setDatatype(datatype);
                                            field.setLength(length);
                                            field.setCanBeNull(Boolean
                                                    .parseBoolean(canBeNull));
                                            field.setRemark(parameterRemark);
                                            field.setIndex(index);
                                            pathVariables.add(field);
                                        }
                                    }
                                }
                                restServiceMethod
                                        .setPathVariables(pathVariables);
                            }
                            if (eleServiceMethod
                                    .getElementsByTag("headvariables") != null
                                    && eleServiceMethod.getElementsByTag(
                                            "headvariables").size() > 0) {
                                Element eleHeadVariables = eleServiceMethod
                                        .getElementsByTag("headvariables")
                                        .first();
                                List<RestField> headVariables = new ArrayList<RestField>();
                                if (eleHeadVariables.children() != null
                                        && eleHeadVariables.children().size() > 0) {
                                    for (int index = 0; index < eleHeadVariables
                                            .children().size(); index++) {
                                        Element element = eleHeadVariables
                                                .child(index);
                                        String tagName = element.tagName();
                                        if (tagName.equals("headvariable")) {
                                            String fieldName = element
                                                    .attr("name");
                                            String datatype = element
                                                    .attr("dataType");
                                            String length = element
                                                    .attr("length");
                                            String canBeNull = element
                                                    .attr("canBeNull");
                                            String parameterRemark = element
                                                    .attr("remark");
                                            RestField field = new RestField();
                                            field.setName(fieldName);
                                            field.setDatatype(datatype);
                                            field.setLength(length);
                                            field.setCanBeNull(Boolean
                                                    .parseBoolean(canBeNull));
                                            field.setRemark(parameterRemark);
                                            field.setIndex(index);
                                            headVariables.add(field);
                                        }
                                    }
                                }
                                restServiceMethod
                                        .setHeadVariables(headVariables);
                            }

                            if (eleServiceMethod.getElementsByTag("request") != null
                                    && eleServiceMethod.getElementsByTag(
                                            "request").size() > 0) {
                                Element eleRequest = eleServiceMethod
                                        .getElementsByTag("request").first();
                                RestObject request = processPacket(
                                        eleRequest.children(),
                                        eleRequest.attr("class"), classMap);
                                restServiceMethod.setRequest(request);
                            }
                            if (eleServiceMethod.getElementsByTag("response") != null
                                    && eleServiceMethod.getElementsByTag(
                                            "response").size() > 0) {
                                Element eleResponse = eleServiceMethod
                                        .getElementsByTag("response").first();
                                RestObject response = processPacket(
                                        eleResponse.children(),
                                        eleResponse.attr("class"), classMap);
                                restServiceMethod.setResponse(response);
                            }

                            restServiceMethods.add(restServiceMethod);
                        }
                        RestService restService = new RestService();
                        String restServiceName = eleService.attr("name");
                        String restServiceClass = eleService.attr("class");
                        if (!classMap.add(restServiceClass)) {
                            throw new CodeGException("CG501", "service."
                                    + restServiceClass + " is duplicated");
                        }
                        restService.setName(restServiceName);
                        restService.set_class(restServiceClass);
                        restService.setMethods(restServiceMethods);
                        rest.setRestService(restService);
                        restMap.put(restName, rest);
                    }
                }
            }
        } catch (Exception e) {
            throw new CodeGException("CG500", "system error:" + e.getMessage());
        }
        return restMap;
    }

    public static Map<String, RestObject> processRestPacket(String bundleFile,
            Set<String> classMap) {
        Map<String, RestObject> packetMap = new HashMap<String, RestObject>();
        try {
            Document xml = Jsoup.parse(new FileInputStream(bundleFile), null, "",
                    Parser.xmlParser());
            Elements elePackets = xml.getElementsByTag("packet");
            for (Element elePacket : elePackets) {
                String _class = elePacket.attr("class");
                String parent = elePacket.attr("parent");
                RestObject restObject = process("", _class, parent,
                        elePacket.children(), classMap);
                restObject.setParent(parent);
                packetMap.put(_class, restObject);
            }
            return packetMap;
        } catch (Exception e1) {
            throw new CodeGException("CG500", "system error");
        }
    }

    

    private static RestObject processPacket(Elements elements, String _class,
            Set<String> classMap) {
        RestObject restObject = new RestObject();
        restObject.set_class(_class);
        Map<String, RestField> fields = new HashMap<String, RestField>();
        Map<String, RestObject> lists = new HashMap<String, RestObject>();
        Map<String, RestObject> objects = new HashMap<String, RestObject>();
        if (elements != null && elements.size() > 0) {
            for (Element element : elements) {
                if (element == null) {
                    continue;
                }
                String tagName = element.tagName();
                if (tagName == null || tagName.equals("")) {
                    continue;
                }
                String name = element.attr("name");
                if (name == null || name.equals("")) {
                    continue;
                }
                if (tagName.equals("list")) {
                    String objectClass = element.attr("class");
                    RestObject list = processPacket(element.children(),
                            objectClass, classMap);
                    lists.put(name, list);
                } else if (tagName.equals("object")) {
                    String objectName = element.attr("name");
                    String objectClass = element.attr("class");
                    String objectParent = element.attr("parent");
                    RestObject object = process(objectName, objectClass,
                            objectParent, element.children(), classMap);
                    objects.put(objectName, object);
                } else if (tagName.equals("field")) {
                    String fieldName = element.attr("name");
                    String datatype = element.attr("dataType");
                    String length = element.attr("length");
                    String canBeNull = element.attr("canBeNull");
                    String remark = element.attr("remark");
                    RestField field = new RestField();
                    field.setName(fieldName);
                    field.setDatatype(datatype);
                    field.setLength(length);
                    field.setCanBeNull(Boolean.parseBoolean(canBeNull));
                    field.setRemark(remark);
                    fields.put(fieldName, field);
                }
            }
        }
        restObject.setFields(fields);
        restObject.setLists(lists);
        restObject.setObjects(objects);
        return restObject;
    }

    private static RestObject process(String name, String _class,
            String parent, Elements elements, Set<String> classMap) {
        RestObject restObject = new RestObject();
        restObject.setName(name);
        restObject.set_class(_class);
        restObject.setParent(parent);
        if (parent != null && !parent.equals("") && _class != null
                && !_class.equals("") && !classMap.add(_class)) {
            throw new CodeGException("CG501", _class + " is duplicated");
        }
        Map<String, RestField> fields = new HashMap<String, RestField>();
        Map<String, RestObject> lists = new HashMap<String, RestObject>();
        Map<String, RestObject> objects = new HashMap<String, RestObject>();
        for (Element element : elements) {
            if (element == null) {
                continue;
            }
            String tagName = element.tagName();
            if (tagName == null || tagName.equals("")) {
                continue;
            }
            String eleName = element.attr("name");
            if (eleName == null || eleName.equals("")) {
                continue;
            }
            if (tagName.equals("list")) {
                String objectClass = element.attr("class");
                RestObject list = processPacket(element.children(),
                        objectClass, classMap);
                lists.put(eleName, list);
            } else if (tagName.equals("object")) {
                String objectName = element.attr("name");
                String objectClass = element.attr("class");
                String objectParent = element.attr("parent");
                RestObject object = process(objectName, objectClass,
                        objectParent, element.children(), classMap);
                objects.put(objectName, object);
            } else {
                String fieldName = element.attr("name");
                String datatype = element.attr("dataType");
                String length = element.attr("length");
                String canBeNull = element.attr("canBeNull");
                String remark = element.attr("remark");
                RestField field = new RestField();
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

}
