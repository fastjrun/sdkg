package com.alibaba.testsdk.service;

import com.alibaba.testsdk.packet.generic.AutoLoginRestRequestBody;
import com.alibaba.testsdk.packet.generic.RegistserRestRequestBody;
import com.fastjrun.packet.BaseDefaultResponseBody;
import com.fastjrun.packet.BaseResponse;


/**
 * 注意：本内容仅限于公司内部传阅，禁止外泄以及用于其他的商业目的
 *
 * @author cuiyingfeng
 * @Copyright 2018 快嘉框架. All rights reserved.
 */
public interface UserServiceRestGeneric {


    public BaseResponse<BaseDefaultResponseBody> register(RegistserRestRequestBody request);

    public com.alibaba.testsdk.packet.generic.LoginRestResponseBody login(com.alibaba.testsdk.packet.generic.LoginRestRequestBody request);

    public com.alibaba.testsdk.packet.generic.LoginRestResponseBody loginv1_1(com.alibaba.testsdk.packet.generic.LoginRestRequestBody request);

    public com.alibaba.testsdk.packet.generic.LoginRestResponseBody autoLogin(AutoLoginRestRequestBody request);

}
