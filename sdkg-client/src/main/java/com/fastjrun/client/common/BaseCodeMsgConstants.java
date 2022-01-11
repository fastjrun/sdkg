/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.client.common;

public interface BaseCodeMsgConstants {

   String CODE_OK = "0000";

    public enum BaseCodeMsg implements CodeMsgI {
        OK(CODE_OK, "OK");

        private String code;

        private String msg;

        BaseCodeMsg(String code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        @Override
        public String getCode() {
            return code;
        }
        @Override
        public String getMsg() {
            return msg;
        }
    }

}
