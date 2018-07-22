package com.fastjrun.client;

import java.io.IOException;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fastjrun.common.ClientException;
import com.fastjrun.common.CodeMsgConstants;
import com.fastjrun.packet.BaseRequestBody;
import com.fastjrun.packet.BaseResponseBody;

/*
 * *
 *  * 注意：本内容仅限于公司内部传阅，禁止外泄以及用于其他的商业目的
 *  *
 *  * @author 崔莹峰
 *  * @Copyright 2018 快嘉框架. All rights reserved.
 *
 */

public abstract class BaseResponseHandleClient {

    public static final String METHOD_DEFAULT = "POST";

    protected final Logger log = LogManager.getLogger(this.getClass());

    protected ObjectMapper objectMapper;

    protected BaseUtilClient baseClient;

    public BaseUtilClient getBaseClient() {
        return baseClient;
    }

    public void setBaseClient(BaseUtilClient baseClient) {
        this.baseClient = baseClient;
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public <T extends BaseResponseBody> T process(String path) {
        return this.process(path, "GET");
    }

    public <T extends BaseResponseBody> T process(String path, BaseRequestBody<T> requestBody) {
        return this.process(path, METHOD_DEFAULT, requestBody);
    }

    public <T extends BaseResponseBody> T process(String path, String method) {
        return this.process(path, method, null);
    }

    public <T extends BaseResponseBody> T process(String path, String method, BaseRequestBody<T> requestBody) {
        return this.process(path, method, null, null, requestBody);
    }

    public <T extends BaseResponseBody> T process(String path, String method,
                                                  Map<String,
                                                          String>
                                                          queryParams,
                                                  Map<String, String> headParams, BaseRequestBody<T> requestBody) {
        String bodyDate;
        try {
            bodyDate = this.objectMapper.writeValueAsString(requestBody);
        } catch (JsonProcessingException e) {
            throw new ClientException(CodeMsgConstants.CodeMsg.ClIENT_REQUEST_COMPOSE_FAIL);
        }
        String urlSuffix = this.generateUrlSuffix();
        String result =
                this.baseClient.process(path + urlSuffix, method, queryParams, headParams, bodyDate);

        JsonNode responseJsonObject;
        try {
            responseJsonObject = this.objectMapper.readTree(result);
        } catch (IOException e) {
            throw new ClientException(CodeMsgConstants.CodeMsg.ClIENT_RESPONSE_NOT_VALID);
        }

        return this.parseBodyFromResponse(responseJsonObject, requestBody);
    }

    public abstract void initSDKConfig(String apiWorld);

    // 和request结构有关系
    protected abstract String generateUrlSuffix();

    // 特定的responseHead
    public abstract <T extends BaseResponseBody> T parseBodyFromResponse(JsonNode responseJsonObject,
                                                                         BaseRequestBody<T> requestBody);

}
