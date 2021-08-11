/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.exchange;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.fastjrun.common.ClientException;
import com.fastjrun.common.CodeMsgConstants;
import com.fastjrun.common.exchange.BaseRPCResponseDecoder;
import com.fastjrun.example.dto.BasePacket;
import com.fastjrun.example.dto.DefaultResponseHead;

public class DefaultRPCResponseDecoder extends BaseRPCResponseDecoder {

    protected <T> void parseResponseHead(T head) {
        DefaultResponseHead defaultResponseHead = (DefaultResponseHead) head;
        String code = defaultResponseHead.getCode();
        if (!code.equals(CodeMsgConstants.CODE_OK)) {
            String msg = defaultResponseHead.getMsg();
            if (msg == null) {
                throw new ClientException(CodeMsgConstants.CodeMsg.ClIENT_RESPONSE_HEAD_MSG_NULL);
            }
            if (msg.equals("")) {
                throw new ClientException(CodeMsgConstants.CodeMsg.ClIENT_RESPONSE_HEAD_MSG_EMPTY);
            }
            log.warn("code = {},msg = {}", code, msg);

            throw new ClientException(code, msg);

        }
    }

    @Override
    protected Object parseDataFromResponse(Object response) {
        BasePacket<?, ?> baseResponse = (BasePacket) response;
        this.parseResponseHead(baseResponse.getHead());
        Method method;
        try {
            method = response.getClass().getMethod("getBody");
            return method.invoke(response);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new ClientException(CodeMsgConstants.CodeMsg.ClIENT_NETWORK_RESPONSE_NOT_OK);
        }
    }
}
