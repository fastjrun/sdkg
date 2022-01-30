/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.client.common;

import com.fastjrun.common.BaseException;
import com.fastjrun.common.CodeMsgI;

/**
 * @author fastjrun
 * 响应码和响应消息封装类
 */
public class ClientException extends BaseException {

    private static final long serialVersionUID = 3940536898565747474L;

    public ClientException(String code, String msg) {
        super(code, msg);
    }

    public ClientException(CodeMsgI codeMsg) {
        this(codeMsg.getCode(), codeMsg.getMsg());
    }
}