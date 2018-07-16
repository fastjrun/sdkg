package com.fastjrun.common;

import java.io.Serializable;

/**
 * @author fastjrun
 * 响应码和响应消息封装类
 */
public class CodeException extends BaseException implements Serializable {

    private static final long serialVersionUID = -8082188922111474558L;

    public CodeException(String code, String msg) {
        super(code, msg);
    }
}