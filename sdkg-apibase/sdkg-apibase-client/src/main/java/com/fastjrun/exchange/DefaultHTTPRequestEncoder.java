/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.exchange;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fastjrun.common.ClientException;
import com.fastjrun.common.CodeMsgConstants;
import com.fastjrun.common.exchange.BaseHTTPRequestEncoder;

public class DefaultHTTPRequestEncoder extends BaseHTTPRequestEncoder {

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
