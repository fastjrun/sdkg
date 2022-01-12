/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.example.exchange;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fastjrun.client.common.ClientException;
import com.fastjrun.client.common.CodeMsgConstants;
import com.fastjrun.client.exchange.BaseHTTPRequestEncoder;

public class ExampleRequestEncoder extends BaseHTTPRequestEncoder {

    @Override
    public String generateRequestBody(Object body) {
        String bodyData;
        try {
            bodyData = this.objectMapper.writeValueAsString(body);
        } catch (JsonProcessingException e) {
            throw new ClientException(CodeMsgConstants.CodeMsg.ClIENT_REQUEST_COMPOSE_FAIL);
        }
        return bodyData;
    }
}
