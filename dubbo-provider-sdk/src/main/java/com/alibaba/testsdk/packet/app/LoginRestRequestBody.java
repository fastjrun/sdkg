package com.alibaba.testsdk.packet.app;

import com.fastjrun.packet.BaseRequestBody;

import java.io.Serializable;


/**
 * 注意：本内容仅限于公司内部传阅，禁止外泄以及用于其他的商业目的
 *
 * @author cuiyingfeng
 * @Copyright 2018 快嘉框架. All rights reserved.
 */
public class LoginRestRequestBody
        extends BaseRequestBody
        implements Serializable {

    private final static long serialVersionUID = 450711736L;
    /**
     * 密码
     */
    @io.swagger.annotations.ApiModelProperty(value = "\u5bc6\u7801", required = true)
    private String loginpwd;
    /**
     * 登录名
     */
    @io.swagger.annotations.ApiModelProperty(value = "\u767b\u5f55\u540d", required = true)
    private String loginName;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(("LoginRestRequestBody" + " ["));
        sb.append("field [");
        sb.append("loginpwd");
        sb.append("=");
        sb.append(this.loginpwd);
        sb.append(",");
        sb.append("loginName");
        sb.append("=");
        sb.append(this.loginName);
        sb.append("]");
        sb.append("]");
        return sb.toString();
    }

    public String getLoginpwd() {
        return this.loginpwd;
    }

    public void setLoginpwd(String loginpwd) {
        this.loginpwd = loginpwd;
    }

    public String getLoginName() {
        return this.loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

}
