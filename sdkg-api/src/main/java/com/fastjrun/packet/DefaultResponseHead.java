package com.fastjrun.packet;

import java.io.Serializable;

/*
 * *
 *  * 注意：本内容仅限于公司内部传阅，禁止外泄以及用于其他的商业目的
 *  *
 *  * @author 崔莹峰
 *  * @Copyright 2018 快嘉框架. All rights reserved.
 *
 */

public class DefaultResponseHead extends BaseResponseHead implements Serializable {

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
