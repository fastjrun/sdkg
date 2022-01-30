/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.web.common;

import com.fastjrun.common.BaseException;

public class RestException extends BaseException {

    private static final long serialVersionUID = -8699442385801042432L;

    public RestException(String code, String msg) {
        super(code, msg);
    }
}