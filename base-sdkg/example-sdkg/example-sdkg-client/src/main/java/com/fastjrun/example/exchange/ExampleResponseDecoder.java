/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.example.exchange;

import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fastjrun.client.common.ClientException;
import com.fastjrun.client.common.CodeMsgConstants;
import com.fastjrun.client.exchange.BaseHTTPResponseDecoder;

public class ExampleResponseDecoder extends BaseHTTPResponseDecoder {

    @Override
    protected JsonNode parseBodyFromResponse(String responseResult) {
        try {
            return this.objectMapper.readTree(responseResult);
        } catch (IOException e) {
            throw new ClientException(CodeMsgConstants.CodeMsg.ClIENT_RESPONSE_NOT_VALID);
        }
    }
}
