/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.codeg.utils;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fastjrun.codeg.util.FastJsonObjectMapper;

import java.io.IOException;
import java.util.List;

public class JacksonUtils {

    public static ObjectMapper objectMapper = new FastJsonObjectMapper();

    /**
     * 使用泛型方法，把json字符串转换为相应的JavaBean对象或者把得到的数组转换为特定类型的List
     *
     * @param <T>       This is the type parameter
     * @param jsonStr   jsonStr
     * @param valueType valueType
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
     * @return Object Array
     */
    public static <T> List<T> readList(JsonNode jsonNode, Class<T> valueType) {
        JavaType javaType =
          objectMapper.getTypeFactory().constructParametricType(List.class, valueType);
        try {
            return objectMapper.readValue(jsonNode.toString(), javaType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 把JavaBean转换为json字符串
     *
     * @param object object
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

    public static String invokeMethodName(String jTypeName) {
        String jsonInvokeMethodName = "asText";
        switch (jTypeName) {
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
        return jsonInvokeMethodName;
    }
}