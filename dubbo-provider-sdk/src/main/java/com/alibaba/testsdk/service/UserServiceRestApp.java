package com.alibaba.testsdk.service;

import com.alibaba.testsdk.packet.app.AutoLoginRestRequestBody;
import com.alibaba.testsdk.packet.app.RegistserRestRequestBody;
import com.fastjrun.packet.BaseAppRequest;
import com.fastjrun.packet.BaseDefaultResponseBody;
import com.fastjrun.packet.BaseResponse;


/**
 * 注意：本内容仅限于公司内部传阅，禁止外泄以及用于其他的商业目的
 *
 * @author cuiyingfeng
 * @Copyright 2018 快嘉框架. All rights reserved.
 */
public interface UserServiceRestApp {


    public BaseResponse<BaseDefaultResponseBody> registerv2(BaseAppRequest<RegistserRestRequestBody> request);

    public BaseResponse<com.alibaba.testsdk.packet.app.LoginRestResponseBody> login(BaseAppRequest<com.alibaba.testsdk.packet.app.LoginRestRequestBody> request);

    public BaseResponse<com.alibaba.testsdk.packet.app.LoginRestResponseBody> loginv1_1(BaseAppRequest<com.alibaba.testsdk.packet.app.LoginRestRequestBody> request);

    public BaseResponse<com.alibaba.testsdk.packet.app.LoginRestResponseBody> autoLogin(BaseAppRequest<AutoLoginRestRequestBody> request);

}
