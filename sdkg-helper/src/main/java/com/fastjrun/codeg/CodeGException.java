package com.fastjrun.codeg;

public class CodeGException extends RuntimeException {

    
    private static final long serialVersionUID = 8706215723970812529L;

    private String code;

    private String msg;

    public CodeGException(String code, String msg) {
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