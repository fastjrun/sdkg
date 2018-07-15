
package com.fastjrun.share.sdk.packet.generic;

import java.io.Serializable;
import com.fastjrun.packet.BaseBody;


/**
 * 注意：本内容仅限于公司内部传阅，禁止外泄以及用于其他的商业目的
 * 
 * @Copyright 2018 快嘉框架. All rights reserved.
 * @author cuiyingfeng
 */
public class LoginRestRequestBody
    extends BaseBody
    implements Serializable
{

    /**
     * 密码
     * 
     */
    private String loginpwd;
    /**
     * 登录名
     * 
     */
    private String loginName;
    private final static long serialVersionUID = 450711736L;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(("LoginRestRequestBody"+" ["));
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
