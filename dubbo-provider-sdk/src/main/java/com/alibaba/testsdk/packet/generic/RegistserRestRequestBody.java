package com.alibaba.testsdk.packet.generic;

import com.fastjrun.packet.BaseRequestBody;

import java.io.Serializable;


/**
 * 注意：本内容仅限于公司内部传阅，禁止外泄以及用于其他的商业目的
 *
 * @author cuiyingfeng
 * @Copyright 2018 快嘉框架. All rights reserved.
 */
public class RegistserRestRequestBody
        extends BaseRequestBody
        implements Serializable {

    private final static long serialVersionUID = 450711736L;
    /**
     * 登录名
     */
    @io.swagger.annotations.ApiModelProperty(value = "\u767b\u5f55\u540d", required = true)
    private String loginId;
    /**
     * 密码
     */
    @io.swagger.annotations.ApiModelProperty(value = "\u5bc6\u7801", required = true)
    private String loginpwd;
    /**
     * 昵称
     */
    @io.swagger.annotations.ApiModelProperty(value = "\u6635\u79f0", required = false)
    private String nickName;
    /**
     * 性别
     */
    @io.swagger.annotations.ApiModelProperty(value = "\u6027\u522b", required = false)
    private String sex;
    /**
     * 手机号
     */
    @io.swagger.annotations.ApiModelProperty(value = "\u624b\u673a\u53f7", required = false)
    private String mobileNo;
    /**
     * 手机号
     */
    @io.swagger.annotations.ApiModelProperty(value = "\u624b\u673a\u53f7", required = false)
    private String email;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(("RegistserRestRequestBody" + " ["));
        sb.append("field [");
        sb.append("loginId");
        sb.append("=");
        sb.append(this.loginId);
        sb.append(",");
        sb.append("loginpwd");
        sb.append("=");
        sb.append(this.loginpwd);
        sb.append(",");
        sb.append("nickName");
        sb.append("=");
        sb.append(this.nickName);
        sb.append(",");
        sb.append("sex");
        sb.append("=");
        sb.append(this.sex);
        sb.append(",");
        sb.append("mobileNo");
        sb.append("=");
        sb.append(this.mobileNo);
        sb.append(",");
        sb.append("email");
        sb.append("=");
        sb.append(this.email);
        sb.append("]");
        sb.append("]");
        return sb.toString();
    }

    public String getLoginId() {
        return this.loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getLoginpwd() {
        return this.loginpwd;
    }

    public void setLoginpwd(String loginpwd) {
        this.loginpwd = loginpwd;
    }

    public String getNickName() {
        return this.nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getSex() {
        return this.sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getMobileNo() {
        return this.mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
