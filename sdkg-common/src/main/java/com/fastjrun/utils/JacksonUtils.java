package com.fastjrun.utils;

/*
 * *
 *  * 注意：本内容仅限于公司内部传阅，禁止外泄以及用于其他的商业目的
 *  *
 *  * @author fastjrun
 *  * @Copyright 2018 快嘉框架. All rights reserved.
 *
 */

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fastjrun.util.FastJsonObjectMapper;

public class JacksonUtils {

    public static ObjectMapper objectMapper = new FastJsonObjectMapper();

    /**
     * 使用泛型方法，把json字符串转换为相应的JavaBean对象或者把得到的数组转换为特定类型的List
     *
     * @param <T>       This is the type parameter
     * @param jsonStr   jsonStr
     * @param valueType valueType
     *
     * @return object
     */
    public static <T> T readValue(String jsonStr, Class<T> valueType) {

        try {
            return objectMapper.readValue(jsonStr, valueType);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * json数组转List
     *
     * @param <T>       This is the type parameter
     * @param jsonNode  jsonNode
     * @param valueType valueType
     *
     * @return Object Array
     */
    public static <T> List<T> readList(JsonNode jsonNode, Class<T> valueType) {
        JavaType javaType =
                objectMapper.getTypeFactory().constructParametricType(List.class, valueType);
        List<T> list = new ArrayList<>();

        while (jsonNode.elements().hasNext()) {
            JsonNode element = jsonNode.elements().next();
            T t = readValue(element.toString(), valueType);
            list.add(t);
        }

        return list;
    }

    /**
     * 把JavaBean转换为json字符串
     *
     * @param object object
     *
     * @return String
     */
    public static String toJSon(Object object) {

        try {
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 把string转换为jsonNode
     *
     * @param data data
     *
     * @return JsonNode
     */
    public static JsonNode toJsonNode(String data) {

        try {
            return objectMapper.readTree(data);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 产生ObjectNode
     *
     * @return ObjectNode
     */
    public static ObjectNode createObjectNode() {

        try {
            return objectMapper.createObjectNode();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 产生ArrayNode
     *
     * @return ArrayNode
     */
    public static ArrayNode createArrayNode() {

        try {
            return objectMapper.createArrayNode();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
