package com.fastjrun.common;

public interface BaseCodeMsgConstants {

    public static final String CODE_OK = "0000";

    public enum BaseCodeMsg implements CodeMsgI {
        OK(CODE_OK, "OK");

        private String code;

        private String msg;

        BaseCodeMsg(String code, String msg) {
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
