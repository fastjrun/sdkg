/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.client.exchange;

import java.util.List;

public abstract class BaseHTTPExchange<U extends BaseHTTPRequestEncoder, V extends
        BaseHTTPResponseDecoder> {

    protected U requestEncoder;

    protected V responseDecoder;

    public <T> T parseObjectFromResponse(String responseResult, Class<T> valueType) {
        return this.responseDecoder.parseObjectFromResponse(responseResult, valueType);

    }

    public <T> List<T> parseListFromResponse(String responseResult, Class<T> valueType) {
        return this.responseDecoder.parseListFromResponse(responseResult, valueType);

    }

    public String generateRequestBody(Object body) {
        return this.requestEncoder.generateRequestBody(body);

    }
}
