package com.alibaba.testsdk.packet.generic;

import com.fastjrun.packet.BaseRequestBody;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;


/**
 * 注意：本内容仅限于公司内部传阅，禁止外泄以及用于其他的商业目的
 *
 * @author cuiyingfeng
 * @Copyright 2018 快嘉框架. All rights reserved.
 */
public class AutoLoginRestRequestBody
        extends BaseRequestBody
        implements Serializable {

    private final static long serialVersionUID = 450711736L;
    /**
     * 旧登录凭证
     */
    @ApiModelProperty(value = "\u65e7\u767b\u5f55\u51ed\u8bc1", required = true)
    private String uuidOld;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(("AutoLoginRestRequestBody" + " ["));
        sb.append("field [");
        sb.append("uuidOld");
        sb.append("=");
        sb.append(this.uuidOld);
        sb.append("]");
        sb.append("]");
        return sb.toString();
    }

    public String getUuidOld() {
        return this.uuidOld;
    }

    public void setUuidOld(String uuidOld) {
        this.uuidOld = uuidOld;
    }

}
