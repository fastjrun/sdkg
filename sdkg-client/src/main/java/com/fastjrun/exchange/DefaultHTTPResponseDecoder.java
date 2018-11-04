package com.fastjrun.exchange;

import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fastjrun.common.ClientException;
import com.fastjrun.common.CodeMsgConstants;

public class DefaultHTTPResponseDecoder extends BaseHTTPResponseDecoder {

    @Override
    protected JsonNode parseBodyFromResponse(String responseResult) {
        JsonNode responseJsonObject;
        try {
            responseJsonObject = this.objectMapper.readTree(responseResult);
        } catch (IOException e) {
            throw new ClientException(CodeMsgConstants.CodeMsg.ClIENT_RESPONSE_NOT_VALID);
        }
        JsonNode headNode = responseJsonObject.get("head");
        if (headNode == null) {
            throw new ClientException(CodeMsgConstants.CodeMsg.CLIENT_RESPONSE_HEAD_NULL);
        }

        JsonNode codeNode = headNode.get("code");

        if (codeNode == null) {
            throw new ClientException(CodeMsgConstants.CodeMsg.CLIENT_RESPONSE_HEAD_CODE_NULL);
        }
        String code = codeNode.asText();
        if (code.equals("")) {
            throw new ClientException(CodeMsgConstants.CodeMsg.CLIENT_RESPONSE_HEAD_CODE_EMPTY);
        }
        if (code.equals(CodeMsgConstants.CODE_OK)) {
            return responseJsonObject.get("body");
        }
        JsonNode msgNode = headNode.get("msg");
        if (msgNode == null) {
            throw new ClientException(CodeMsgConstants.CodeMsg.ClIENT_RESPONSE_HEAD_MSG_NULL);
        }
        String msg = msgNode.asText();
        if (msg.equals("")) {
            throw new ClientException(CodeMsgConstants.CodeMsg.ClIENT_RESPONSE_HEAD_MSG_EMPTY);
        }
        log.warn("code = {},msg = {}", code, msg);

        throw new ClientException(CodeMsgConstants.CodeMsg.ClIENT_SERVER_EXCEPTION);
    }
}
