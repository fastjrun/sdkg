package com.fastjrun.mock;

import com.alibaba.testsdk.packet.app.AutoLoginRestRequestBody;
import com.alibaba.testsdk.packet.app.RegistserRestRequestBody;
import com.alibaba.testsdk.service.UserServiceRestApp;
import com.fastjrun.helper.BaseResponseHelper;
import com.fastjrun.helper.MockHelper;
import com.fastjrun.packet.BaseAppRequest;
import com.fastjrun.packet.BaseDefaultResponseBody;
import com.fastjrun.packet.BaseResponse;
import com.fastjrun.packet.BaseResponseHead;
import org.springframework.stereotype.Service;


/**
 * 注意：本内容仅限于公司内部传阅，禁止外泄以及用于其他的商业目的
 *
 * @author cuiyingfeng
 * @Copyright 2018 快嘉框架. All rights reserved.
 */
@Service("userServiceRestApp")
public class UserServiceRestAppMock
        implements UserServiceRestApp {


    @Override
    public BaseResponse<BaseDefaultResponseBody> registerv2(BaseAppRequest<RegistserRestRequestBody> request) {
        BaseResponse<BaseDefaultResponseBody> response = BaseResponseHelper.getSuccessResult();
        return response;
    }

    @Override
    public BaseResponse<com.alibaba.testsdk.packet.app.LoginRestResponseBody> login(BaseAppRequest<com.alibaba.testsdk.packet.app.LoginRestRequestBody> request) {
        BaseResponse<com.alibaba.testsdk.packet.app.LoginRestResponseBody> response = new BaseResponse<com.alibaba.testsdk.packet.app.LoginRestResponseBody>();
        BaseResponseHead responseHead = new BaseResponseHead();
        responseHead.setCode("0000");
        responseHead.setMsg("Mock.");
        response.setHead(responseHead);
        com.alibaba.testsdk.packet.app.LoginRestResponseBody loginRestResponseBody = new com.alibaba.testsdk.packet.app.LoginRestResponseBody();
        loginRestResponseBody.setNickName(MockHelper.geStringWithAscii(30));
        loginRestResponseBody.setSex(MockHelper.geStringWithAscii(1));
        loginRestResponseBody.setMobileNo(MockHelper.geStringWithAscii(20));
        loginRestResponseBody.setUuid(MockHelper.geStringWithAscii(64));
        loginRestResponseBody.setEmail(MockHelper.geStringWithAscii(30));
        response.setBody(loginRestResponseBody);
        return response;
    }

    @Override
    public BaseResponse<com.alibaba.testsdk.packet.app.LoginRestResponseBody> loginv1_1(BaseAppRequest<com.alibaba.testsdk.packet.app.LoginRestRequestBody> request) {
        BaseResponse<com.alibaba.testsdk.packet.app.LoginRestResponseBody> response = new BaseResponse<com.alibaba.testsdk.packet.app.LoginRestResponseBody>();
        BaseResponseHead responseHead = new BaseResponseHead();
        responseHead.setCode("0000");
        responseHead.setMsg("Mock.");
        response.setHead(responseHead);
        com.alibaba.testsdk.packet.app.LoginRestResponseBody loginRestResponseBody = new com.alibaba.testsdk.packet.app.LoginRestResponseBody();
        loginRestResponseBody.setNickName(MockHelper.geStringWithAscii(30));
        loginRestResponseBody.setSex(MockHelper.geStringWithAscii(1));
        loginRestResponseBody.setMobileNo(MockHelper.geStringWithAscii(20));
        loginRestResponseBody.setUuid(MockHelper.geStringWithAscii(64));
        loginRestResponseBody.setEmail(MockHelper.geStringWithAscii(30));
        response.setBody(loginRestResponseBody);
        return response;
    }

    @Override
    public BaseResponse<com.alibaba.testsdk.packet.app.LoginRestResponseBody> autoLogin(BaseAppRequest<AutoLoginRestRequestBody> request) {
        BaseResponse<com.alibaba.testsdk.packet.app.LoginRestResponseBody> response = new BaseResponse<com.alibaba.testsdk.packet.app.LoginRestResponseBody>();
        BaseResponseHead responseHead = new BaseResponseHead();
        responseHead.setCode("0000");
        responseHead.setMsg("Mock.");
        response.setHead(responseHead);
        com.alibaba.testsdk.packet.app.LoginRestResponseBody loginRestResponseBody = new com.alibaba.testsdk.packet.app.LoginRestResponseBody();
        loginRestResponseBody.setNickName(MockHelper.geStringWithAscii(30));
        loginRestResponseBody.setSex(MockHelper.geStringWithAscii(1));
        loginRestResponseBody.setMobileNo(MockHelper.geStringWithAscii(20));
        loginRestResponseBody.setUuid(MockHelper.geStringWithAscii(64));
        loginRestResponseBody.setEmail(MockHelper.geStringWithAscii(30));
        response.setBody(loginRestResponseBody);
        return response;
    }

}
