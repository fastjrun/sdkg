/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.common.exchange;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
        return JacksonUtils.readValue(data.toString(), valueType);
    }

    public <T> List<T> parseListFromResponse(String responseResult, Class<T> valueType) {
        JsonNode data = this.parseBodyFromResponse(responseResult);
        return JacksonUtils.readList(data, valueType);
    }

    protected abstract JsonNode parseBodyFromResponse(String responseResult);
}
