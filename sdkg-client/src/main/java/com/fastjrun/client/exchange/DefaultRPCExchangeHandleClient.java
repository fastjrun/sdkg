/*
 * Copyright (C) 2018 Fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.client.exchange;

import com.fastjrun.common.ClientException;
import com.fastjrun.common.CodeMsgConstants;
import com.fastjrun.dto.DefaultResponseHead;
import com.fastjrun.exchange.BaseRPCRequestEncoder;
import com.fastjrun.exchange.BaseRPCResponseDecoder;
import com.fastjrun.exchange.DefaultRPCExchange;
import com.fastjrun.exchange.DefaultRPCRequestEncoder;
import com.fastjrun.exchange.DefaultRPCResponseDecoder;

public abstract class DefaultRPCExchangeHandleClient extends BaseRPCExchangeHandleClient {

    private void parseResponseHead(DefaultResponseHead defaultResponseHead) {
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
    protected void initExchange() {
        this.defaultRPCExchange = new DefaultRPCExchange();
        BaseRPCRequestEncoder baseRPCRequestEncoder = new DefaultRPCRequestEncoder();
        this.defaultRPCExchange.setRequestEncoder(baseRPCRequestEncoder);
        BaseRPCResponseDecoder responseDecoder = new DefaultRPCResponseDecoder();
        this.defaultRPCExchange.setResponseDecoder(responseDecoder);
    }
}
