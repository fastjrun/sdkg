/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.client.common;

/**
 * @author fastjrun
 * 响应码和响应消息封装类
 */
public abstract class BaseException extends RuntimeException {

    protected String code;

    protected String msg;

    public BaseException(String code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return this.code;
    }

    public String getMsg() {
        return this.msg;
    }
}