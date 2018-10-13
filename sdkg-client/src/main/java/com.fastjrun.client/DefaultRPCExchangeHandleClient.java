package com.fastjrun.client;

import com.fastjrun.common.ClientException;
import com.fastjrun.common.CodeMsgConstants;
import com.fastjrun.dto.DefaultResponseHead;
import com.fastjrun.exchange.DefaultRPCExchange;
import com.fastjrun.exchange.DefaultRPCResponseDecoder;

/*
 * *
 *  * 注意：本内容仅限于公司内部传阅，禁止外泄以及用于其他的商业目的
 *  *
 *  * @author 崔莹峰
 *  * @Copyright 2018 快嘉框架. All rights reserved.
 *
 */

public abstract class DefaultRPCExchangeHandleClient extends BaseRpcExchangeHandleClient {

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
        DefaultRPCExchange defaultRPCExchange = new DefaultRPCExchange();
        DefaultRPCResponseDecoder responseDecoder = new DefaultRPCResponseDecoder();
        defaultRPCExchange.setResponseDecoder(responseDecoder);
        this.defaultRPCExchange = defaultRPCExchange;
    }
}
