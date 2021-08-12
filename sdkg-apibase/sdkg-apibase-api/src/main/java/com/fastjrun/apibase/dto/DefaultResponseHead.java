/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.apibase.dto;

import java.io.Serializable;

public class DefaultResponseHead implements Serializable {

    private static final long serialVersionUID = 2492660045919530641L;
    private String code;

    private String msg;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(("BaseResponseHead" + " ["));
        sb.append("code=").append(this.code);
        sb.append(",message=").append(this.msg);
        sb.append("]");
        return sb.toString();
    }
}
