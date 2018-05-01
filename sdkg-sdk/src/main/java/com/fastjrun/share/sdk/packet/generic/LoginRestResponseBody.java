
package com.fastjrun.share.sdk.packet.generic;

import java.io.Serializable;
import com.fastjrun.sdkg.packet.BaseResponseBody;


/**
 * 
 * @author fastjrun
 */
public class LoginRestResponseBody
    extends BaseResponseBody
    implements Serializable
{

    /**
     * 昵称
     * 
     */
    private String nickName;
    /**
     * 性别
     * 
     */
    private String sex;
    /**
     * 手机号
     * 
     */
    private String mobileNo;
    /**
     * 登录凭证
     * 
     */
    private String uuid;
    /**
     * 邮箱
     * 
     */
    private String email;
    private final static long serialVersionUID = 450711736L;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(("LoginRestResponseBody"+" ["));
        sb.append("field [");
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
        sb.append("uuid");
        sb.append("=");
        sb.append(this.uuid);
        sb.append(",");
        sb.append("email");
        sb.append("=");
        sb.append(this.email);
        sb.append("]");
        sb.append("]");
        return sb.toString();
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

    public String getUuid() {
        return this.uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
