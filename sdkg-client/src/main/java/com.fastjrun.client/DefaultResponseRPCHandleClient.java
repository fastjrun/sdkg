package com.fastjrun.client;

import com.fastjrun.common.ClientException;
import com.fastjrun.common.CodeMsgConstants;
import com.fastjrun.dto.DefaultListResponse;
import com.fastjrun.dto.DefaultResponse;
import com.fastjrun.dto.DefaultResponseHead;

import java.util.List;

/*
 * *
 *  * 注意：本内容仅限于公司内部传阅，禁止外泄以及用于其他的商业目的
 *  *
 *  * @author 崔莹峰
 *  * @Copyright 2018 快嘉框架. All rights reserved.
 *
 */

public abstract class DefaultResponseRPCHandleClient extends BaseRPCResponseHandleClient {

    public <T> T process(Class classType, String methodName) {

        return this.process(classType, methodName, null, null);
    }

    public <T> T process(Class classType, String methodName, Class[]
            paramterTypes, Object[] paramerValues) {

        Object result = this.baseClient.process(classType, methodName, paramterTypes, paramerValues);
        DefaultResponse<T> response = (DefaultResponse<T>) result;
        parseResponseHead(response.getHead());
        return response.getBody();
    }

    public <T> List<T> processList(Class classType, String methodName) {

        return this.processList(classType, methodName, null, null);
    }

    public <T> List<T> processList(Class classType, String methodName, Class[]
            paramterTypes, Object[] paramerValues) {

        Object result = this.baseClient.process(classType, methodName, paramterTypes, paramerValues);

        DefaultListResponse<T> response = (DefaultListResponse<T>) result;
        parseResponseHead(response.getHead());
        return response.getBody();
    }

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
}
