package com.fastjrun.sdkg.packet;

import java.io.Serializable;

public class BaseResponseHead implements Serializable {
    
    private static final long serialVersionUID = 6153642938936890248L;

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
        sb.append(("BaseResponseHead"+" ["));
        sb.append("code");
        sb.append("=");
        sb.append(this.code);
        sb.append(",");
        sb.append("message");
        sb.append("=");
        sb.append(this.msg);
        sb.append("]");
        return sb.toString();
    }
}
