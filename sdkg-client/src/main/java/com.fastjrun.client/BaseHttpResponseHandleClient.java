package com.fastjrun.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fastjrun.common.ClientException;
import com.fastjrun.common.CodeMsgConstants;

/*
 * *
 *  * 注意：本内容仅限于公司内部传阅，禁止外泄以及用于其他的商业目的
 *  *
 *  * @author 崔莹峰
 *  * @Copyright 2018 快嘉框架. All rights reserved.
 *
 */

public abstract class BaseHttpResponseHandleClient extends BaseResponseHandleClient<BaseHttpUtilClient> {

    public static final String METHOD_DEFAULT = "POST";

    protected ObjectMapper objectMapper;

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public <T> void process(String path, String method,
                            Map<String,
                                    String>
                                    queryParams,
                            Map<String, String> headParams,
                            Map<String, String> cookieParams, T requestBody) {

        this.parseBodyFromResponse(path, method, queryParams, headParams, cookieParams,
                requestBody);
    }

    public <T, V> T process(String path, String method,
                            Map<String,
                                    String>
                                    queryParams,
                            Map<String, String> headParams,
                            Map<String, String> cookieParams, V requestBody, Class<T> classType) {

        JsonNode responseJsonObject = this.parseBodyFromResponse(path, method, queryParams, headParams, cookieParams,
                requestBody);

        return this.parseObjectFromResponse(responseJsonObject, classType);
    }

    public <T, V> List<T> processList(String path, String method,
                                      Map<String,
                                              String>
                                              queryParams,
                                      Map<String, String> headParams,
                                      Map<String, String> cookieParams, V requestBody, Class<T> classType) {

        JsonNode responseJsonObject = this.parseBodyFromResponse(path, method, queryParams, headParams, cookieParams,
                requestBody);

        return this.parseListFromResponse(responseJsonObject, classType);
    }

    protected <T> T parseObjectFromResponse(JsonNode responseJsonObject, Class<T> valueType) {
        JsonNode data = this.parseBodyFromResponse(responseJsonObject);
        try {
            return this.objectMapper
                    .readValue(data.toString(), valueType);
        } catch (IOException e) {
            log.error("{}", e);
            throw new ClientException(CodeMsgConstants.CodeMsg.ClIENT_RESPONSE_NOT_VALID);
        }
    }

    protected <T> List<T> parseListFromResponse(JsonNode responseJsonObject, Class<T> valueType) {
        JsonNode data = this.parseBodyFromResponse(responseJsonObject);

        JavaType javaType =
                objectMapper.getTypeFactory().constructParametricType(List.class, valueType);
        List<T> resObj;

        if (responseJsonObject.elements().hasNext()) {
            try {
                resObj = this.objectMapper
                        .readValue(data.toString(), javaType);
            } catch (IOException e) {
                log.error("{}", e);
                throw new ClientException(CodeMsgConstants.CodeMsg.ClIENT_RESPONSE_NOT_VALID);
            }
        } else {
            resObj = new ArrayList<>();
        }
        return resObj;

    }

    public <T> JsonNode parseBodyFromResponse(String path, String method,
                                              Map<String,
                                                      String>
                                                      queryParams,
                                              Map<String, String> headParams,
                                              Map<String, String> cookieParams, T requestBody) {
        String bodyDate;
        try {
            bodyDate = this.objectMapper.writeValueAsString(requestBody);
        } catch (JsonProcessingException e) {
            throw new ClientException(CodeMsgConstants.CodeMsg.ClIENT_REQUEST_COMPOSE_FAIL);
        }
        String urlSuffix = this.generateUrlSuffix();
        String result =
                this.baseClient.process(path + urlSuffix, method, queryParams, headParams, cookieParams, bodyDate);

        JsonNode responseJsonObject;
        try {
            responseJsonObject = this.objectMapper.readTree(result);
        } catch (IOException e) {
            throw new ClientException(CodeMsgConstants.CodeMsg.ClIENT_RESPONSE_NOT_VALID);
        }

        return responseJsonObject;
    }

    protected abstract JsonNode parseBodyFromResponse(JsonNode responseJsonObject);

    // 和request结构有关系
    protected abstract String generateUrlSuffix();

}
