/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.codeg.common;

public class CodeGException extends RuntimeException {

    private static final long serialVersionUID = 8706215723970812529L;

    private String code;

    private String msg;

    private Exception ex;

    public CodeGException(String code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }

    public CodeGException(String code, String msg, Exception ex) {
        this(code, msg);
        this.ex = ex;
    }

    public String getCode() {
        return this.code;
    }

    public String getMsg() {
        return this.msg;
    }

    public Exception getException() {
        return this.ex;
    }
}