/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.eladmin.exchange;

import com.fasterxml.jackson.databind.JsonNode;
import com.fastjrun.client.common.ClientException;
import com.fastjrun.client.common.CodeMsgConstants;
import com.fastjrun.client.exchange.BaseHTTPResponseDecoder;

import java.io.IOException;

public class DefaultHTTPNoHeadResponseDecoder extends BaseHTTPResponseDecoder {

    @Override
    protected JsonNode parseBodyFromResponse(String responseResult) {
        try {
            return this.objectMapper.readTree(responseResult);
        } catch (IOException e) {
            throw new ClientException(CodeMsgConstants.CodeMsg.ClIENT_RESPONSE_NOT_VALID);
        }
    }
}
