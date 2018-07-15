package com.fastjrun.mock;

import com.alibaba.testsdk.packet.generic.AutoLoginRestRequestBody;
import com.alibaba.testsdk.packet.generic.RegistserRestRequestBody;
import com.alibaba.testsdk.service.UserServiceRestGeneric;
import com.fastjrun.helper.BaseResponseHelper;
import com.fastjrun.helper.MockHelper;
import com.fastjrun.packet.BaseDefaultResponseBody;
import com.fastjrun.packet.BaseResponse;
import org.springframework.stereotype.Service;


/**
 * 注意：本内容仅限于公司内部传阅，禁止外泄以及用于其他的商业目的
 *
 * @author cuiyingfeng
 * @Copyright 2018 快嘉框架. All rights reserved.
 */
@Service("userServiceRestGeneric")
public class UserServiceRestGenericMock
        implements UserServiceRestGeneric {


    @Override
    public BaseResponse<BaseDefaultResponseBody> register(RegistserRestRequestBody request) {
        BaseResponse<BaseDefaultResponseBody> response = BaseResponseHelper.getSuccessResult();
        return response;
    }

    @Override
    public com.alibaba.testsdk.packet.generic.LoginRestResponseBody login(com.alibaba.testsdk.packet.generic.LoginRestRequestBody request) {
        com.alibaba.testsdk.packet.generic.LoginRestResponseBody loginRestResponseBody = new com.alibaba.testsdk.packet.generic.LoginRestResponseBody();
        loginRestResponseBody.setNickName(MockHelper.geStringWithAscii(30));
        loginRestResponseBody.setSex(MockHelper.geStringWithAscii(1));
        loginRestResponseBody.setMobileNo(MockHelper.geStringWithAscii(20));
        loginRestResponseBody.setUuid(MockHelper.geStringWithAscii(64));
        loginRestResponseBody.setEmail(MockHelper.geStringWithAscii(30));
        return loginRestResponseBody;
    }

    @Override
    public com.alibaba.testsdk.packet.generic.LoginRestResponseBody loginv1_1(com.alibaba.testsdk.packet.generic.LoginRestRequestBody request) {
        com.alibaba.testsdk.packet.generic.LoginRestResponseBody loginRestResponseBody = new com.alibaba.testsdk.packet.generic.LoginRestResponseBody();
        loginRestResponseBody.setNickName(MockHelper.geStringWithAscii(30));
        loginRestResponseBody.setSex(MockHelper.geStringWithAscii(1));
        loginRestResponseBody.setMobileNo(MockHelper.geStringWithAscii(20));
        loginRestResponseBody.setUuid(MockHelper.geStringWithAscii(64));
        loginRestResponseBody.setEmail(MockHelper.geStringWithAscii(30));
        return loginRestResponseBody;
    }

    @Override
    public com.alibaba.testsdk.packet.generic.LoginRestResponseBody autoLogin(AutoLoginRestRequestBody request) {
        com.alibaba.testsdk.packet.generic.LoginRestResponseBody loginRestResponseBody = new com.alibaba.testsdk.packet.generic.LoginRestResponseBody();
        loginRestResponseBody.setNickName(MockHelper.geStringWithAscii(30));
        loginRestResponseBody.setSex(MockHelper.geStringWithAscii(1));
        loginRestResponseBody.setMobileNo(MockHelper.geStringWithAscii(20));
        loginRestResponseBody.setUuid(MockHelper.geStringWithAscii(64));
        loginRestResponseBody.setEmail(MockHelper.geStringWithAscii(30));
        return loginRestResponseBody;
    }

}
