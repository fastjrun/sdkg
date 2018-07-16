package com.fastjrun.common;

public interface CodeMsgContants {

    //  Client
    public static final String CODE_CLIENT_NETWORK_NOT_AVAILABLE = "6999";
    public static final String CODE_CLIENT_EMPTY_RESPONSE = "6998";
    public static final String CODE_CLIENT_EMPTY_RESPONSE_HEAD = "6997";
    public static final String CODE_CLIENT_EMPTY_RESPONSE_HEAD_CODE = "6996";
    public static final String CODE_CLIENT_SYSTEM_EXCEPTION = "6995";
    public enum CodeMsg {
        CLIENT_NETWORK_NOT_AVAILABLE(CODE_CLIENT_NETWORK_NOT_AVAILABLE, "网络异常"),
        CLIENT_EMPTY_RESPONSE(CODE_CLIENT_EMPTY_RESPONSE, "返回数据为空"),
        CLIENT_EMPTY_RESPONSE_HEAD(CODE_CLIENT_EMPTY_RESPONSE_HEAD, "返回数据head为空"),
        CLIENT_EMPTY_RESPONSE_HEAD_CODE(CODE_CLIENT_EMPTY_RESPONSE_HEAD_CODE, "返回数据head中code为空"),
        CLIENT_SYSTEM_EXCEPTION(CODE_CLIENT_SYSTEM_EXCEPTION, "返回数据head中code为空");

        private String code;

        private String msg;

        CodeMsg(String code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        public String getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }
    }

}
