package com.fastjrun.common;

public interface CodeMsgConstants {

    public static final String CODE_OK = "0000";

    public static final String CODE_ClIENT_NETWORK_NOT_AVAILABLE = "6999";
    public static final String CODE_ClIENT_RESPONSE_EMPTY = "6998";
    public static final String CODE_ClIENT_RESPONSE_NOT_VALID = "6997";

    public static final String CODE_ClIENT_REQUEST_COMPOSE_FAIL = "6996";

    // defaultResponse
    public static final String CODE_CLIENT_RESPONSE_HEAD_NULL = "6995";
    public static final String CODE_CLIENT_RESPONSE_HEAD_CODE_NULL = "6994";
    public static final String CODE_CLIENT_RESPONSE_HEAD_CODE_EMPTY = "6993";
    public static final String CODE_ClIENT_RESPONSE_BODY_NOT_VALID = "6992";
    public static final String CODE_ClIENT_RESPONSE_HEAD_MSG_NULL = "6991";
    public static final String CODE_ClIENT_RESPONSE_HEAD_MSG_EMPTY = "6990";
    public static final String CODE_ClIENT_SERVER_EXCEPTION = "6989";
    public static final String CODE_ClIENT_REQUEST_QUERYSTRING_ENCODE_FAIL = "6988";
    public static final String CODE_ClIENT_RESPONSE_NOT_OK = "6987";

    public enum CodeMsg implements CodeMsgI {
        OK(CODE_OK, "OK"),
        // client
        //  simplehttpClient
        ClIENT_NETWORK_NOT_AVAILABLE(CODE_ClIENT_NETWORK_NOT_AVAILABLE, "网络异常"),
        ClIENT_NETWORK_RESPONSE_NOT_OK(CODE_ClIENT_RESPONSE_NOT_OK, "返回响应不成功"),
        ClIENT_RESPONSE_EMPTY(CODE_ClIENT_RESPONSE_EMPTY, "返回数据为空"),
        ClIENT_SERVER_EXCEPTION(CODE_ClIENT_SERVER_EXCEPTION, "服务端系统异常"),
        // json
        ClIENT_REQUEST_COMPOSE_FAIL(CODE_ClIENT_REQUEST_COMPOSE_FAIL, "组装报文失败"),
        ClIENT_RESPONSE_NOT_VALID(CODE_ClIENT_RESPONSE_NOT_VALID, "返回数据不是json格式"),

        // defaultResponse
        CLIENT_RESPONSE_HEAD_NULL(CODE_CLIENT_RESPONSE_HEAD_NULL, "返回数据没有head节点"),
        CLIENT_RESPONSE_HEAD_CODE_NULL(CODE_CLIENT_RESPONSE_HEAD_CODE_NULL, "返回数据head中code为空"),
        CLIENT_RESPONSE_HEAD_CODE_EMPTY(CODE_CLIENT_RESPONSE_HEAD_CODE_EMPTY, "返回数据head中code为空值"),
        ClIENT_RESPONSE_BODY_NOT_VALID(CODE_ClIENT_RESPONSE_BODY_NOT_VALID, "返回数据中body不是约定的报文格式"),
        ClIENT_RESPONSE_HEAD_MSG_NULL(CODE_ClIENT_RESPONSE_HEAD_MSG_NULL, "返回数据中没有msg"),
        ClIENT_RESPONSE_HEAD_MSG_EMPTY(CODE_ClIENT_RESPONSE_HEAD_MSG_EMPTY, "返回数据中msg节点为空"),
        ClIENT_REQUEST_QUERYSTRING_ENCODE_FAIL(CODE_ClIENT_REQUEST_QUERYSTRING_ENCODE_FAIL, "url编码错误，不支持utf8");

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
