package com.fastjrun.common;

/**
 * @author fastjrun
 * 响应码和响应消息封装类
 */
public class ClientException extends BaseException {

    private static final long serialVersionUID = 3940536898565747474L;

    public ClientException(String code, String msg) {
        super(code, msg);
    }

    public ClientException(CodeMsgConstants.CodeMsg codeMsg) {
        this(codeMsg.getCode(), codeMsg.getMsg());
    }
}