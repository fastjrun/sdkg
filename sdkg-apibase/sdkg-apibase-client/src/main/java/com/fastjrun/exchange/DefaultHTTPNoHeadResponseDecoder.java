/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.exchange;

import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fastjrun.common.ClientException;
import com.fastjrun.common.CodeMsgConstants;
import com.fastjrun.common.exchange.BaseHTTPResponseDecoder;

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
