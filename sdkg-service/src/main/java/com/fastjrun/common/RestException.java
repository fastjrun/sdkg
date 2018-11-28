/*
 * Copyright (C) 2018 Fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.common;

public class RestException extends BaseException {

    private static final long serialVersionUID = -8699442385801042432L;

    public RestException(String code, String msg) {
        super(code, msg);
    }
}