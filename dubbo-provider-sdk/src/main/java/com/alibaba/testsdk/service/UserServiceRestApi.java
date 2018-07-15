package com.alibaba.testsdk.service;

import com.alibaba.testsdk.packet.api.AutoLoginRestRequestBody;
import com.alibaba.testsdk.packet.api.RegistserRestRequestBody;
import com.fastjrun.packet.BaseApiRequest;
import com.fastjrun.packet.BaseDefaultResponseBody;
import com.fastjrun.packet.BaseResponse;


/**
 * 注意：本内容仅限于公司内部传阅，禁止外泄以及用于其他的商业目的
 *
 * @author cuiyingfeng
 * @Copyright 2018 快嘉框架. All rights reserved.
 */
public interface UserServiceRestApi {


    public BaseResponse<BaseDefaultResponseBody> register(BaseApiRequest<RegistserRestRequestBody> request);

    public BaseResponse<com.alibaba.testsdk.packet.api.LoginRestResponseBody> login(BaseApiRequest<com.alibaba.testsdk.packet.api.LoginRestRequestBody> request);

    public BaseResponse<com.alibaba.testsdk.packet.api.LoginRestResponseBody> loginv1_1(BaseApiRequest<com.alibaba.testsdk.packet.api.LoginRestRequestBody> request);

    public BaseResponse<com.alibaba.testsdk.packet.api.LoginRestResponseBody> autoLogin(BaseApiRequest<AutoLoginRestRequestBody> request);

}
