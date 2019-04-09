package com.fastjrun.exchange;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fastjrun.common.ClientException;
import com.fastjrun.common.CodeMsgConstants;
import com.fastjrun.utils.JacksonUtils;

public abstract class BaseHTTPResponseDecoder extends BaseResponseDecoder {

    protected ObjectMapper objectMapper;

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public <T> T parseObjectFromResponse(String responseResult, Class<T> valueType) {
        JsonNode data = this.parseBodyFromResponse(responseResult);
        try {
            return objectMapper
                    .readValue(data.toString(), valueType);
        } catch (IOException e) {
            log.error("{}", e);
            throw new ClientException(CodeMsgConstants.CodeMsg.ClIENT_RESPONSE_NOT_VALID);
        }
    }

    public <T> List<T> parseListFromResponse(String responseResult, Class<T> valueType) {
        JsonNode data = this.parseBodyFromResponse(responseResult);
        return JacksonUtils.readList(data,valueType);
    }

    protected abstract JsonNode parseBodyFromResponse(String responseResult);
}
