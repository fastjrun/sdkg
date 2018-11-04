package com.fastjrun.exchange;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fastjrun.common.ClientException;
import com.fastjrun.common.CodeMsgConstants;

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

        JavaType javaType =
                objectMapper.getTypeFactory().constructParametricType(List.class, valueType);
        List<T> resObj;

        if (data.elements().hasNext()) {
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

    protected abstract JsonNode parseBodyFromResponse(String responseResult);
}
