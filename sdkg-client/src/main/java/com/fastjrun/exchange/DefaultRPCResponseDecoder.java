package com.fastjrun.exchange;

import com.fastjrun.common.ClientException;
import com.fastjrun.common.CodeMsgConstants;
import com.fastjrun.dto.DefaultResponseHead;

public class DefaultRPCResponseDecoder extends BaseRPCResponseDecoder {

    @Override
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
}
