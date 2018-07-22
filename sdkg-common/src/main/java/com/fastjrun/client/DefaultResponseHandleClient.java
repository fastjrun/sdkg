package com.fastjrun.client;

import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fastjrun.common.ClientException;
import com.fastjrun.common.CodeMsgConstants;
import com.fastjrun.packet.BaseRequestBody;
import com.fastjrun.packet.BaseResponseBody;

/*
 * *
 *  * 注意：本内容仅限于公司内部传阅，禁止外泄以及用于其他的商业目的
 *  *
 *  * @author 崔莹峰
 *  * @Copyright 2018 快嘉框架. All rights reserved.
 *
 */

public abstract class DefaultResponseHandleClient extends BaseResponseHandleClient {

    // DefaultResponseHead
    @Override
    public <T extends BaseResponseBody> T parseBodyFromResponse(JsonNode responseJsonObject,
                                                                BaseRequestBody<T> requestBody) {
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
            JsonNode body = responseJsonObject.get("body");
            T resObj = null;
            if (body != null) {
                try {
                    resObj = this.objectMapper
                            .readValue(body.toString(), requestBody.getResponseBodyClass());
                } catch (IOException e) {
                    throw new ClientException(CodeMsgConstants.CodeMsg.ClIENT_RESPONSE_BODY_NOT_VALID);
                }
            }
            return resObj;
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

    protected void initUtilClient(String baseUrl) {

        this.baseClient = new DefaultHttpClient();
        baseClient.setBaseUrl(baseUrl);
    }
}
