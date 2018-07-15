
package com.fastjrun.share.sdk.service;

import com.fastjrun.share.sdk.packet.api.AutoLoginRestRequestBody;
import com.fastjrun.share.sdk.packet.api.LoginRestRequestBody;
import com.fastjrun.share.sdk.packet.api.LoginRestResponseBody;
import com.fastjrun.share.sdk.packet.api.RegistserRestRequestBody;


/**
 * 注意：本内容仅限于公司内部传阅，禁止外泄以及用于其他的商业目的
 * 
 * @Copyright 2018 快嘉框架. All rights reserved.
 * @author cuiyingfeng
 */
public interface UserServiceRestApi {


    /**
     * 注册
     * 
     */
    void register(RegistserRestRequestBody request);

    /**
     * 登录
     * 
     */
    LoginRestResponseBody login(LoginRestRequestBody request);

    /**
     * 登录v1.1
     * 
     */
    LoginRestResponseBody loginv1_1(LoginRestRequestBody request);

    /**
     * 自动登录
     * 
     */
    LoginRestResponseBody autoLogin(AutoLoginRestRequestBody request);

}
