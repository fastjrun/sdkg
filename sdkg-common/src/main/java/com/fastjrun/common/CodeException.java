package com.fastjrun.common;

/**
 * @author fastjrun
 * 响应码和响应消息封装类
 *
 */
public class CodeException extends RuntimeException {
    
    private static final long serialVersionUID = -8082188922111474558L;

    private String code;

    private String msg;

    public CodeException(String code, String msg) {
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