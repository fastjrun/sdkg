
/*
 * Copyright (C) 2018 Fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.sdkg.demo.service.impl;

import java.sql.Timestamp;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.fastjrun.common.ServiceException;
import com.fastjrun.helper.MockHelper;
import com.fastjrun.sdkg.demo.dao.BaseUserDao;
import com.fastjrun.sdkg.demo.entity.User;
import com.fastjrun.sdkg.demo.packet.app.AutoLoginRestRequestBody;
import com.fastjrun.sdkg.demo.packet.app.RegistserRestRequestBody;
import com.fastjrun.sdkg.demo.service.UserServiceRestApp;
import com.fastjrun.service.BaseService;

@Service("userServiceRestApp")
public class UserServiceRestAppImpl extends BaseService
        implements UserServiceRestApp {

    @Resource
    private BaseUserDao baseUserDao;

    private final static String USER_ALREADY_EXISTS = "0001";

    /**
     * 注册
     */
    public void registerv2(RegistserRestRequestBody requestBody) {

        String loginPwd = requestBody.getLoginpwd();
        String loginName = requestBody.getLoginId();
        String nickName = requestBody.getNickName();
        Integer sex = Integer.valueOf(requestBody.getSex());
        String mobileNo = requestBody.getMobileNo();
        String email = requestBody.getEmail();
        String condition = " and `loginName`='" + loginName + "' and `mobileNo`='"
                + mobileNo + "'";
        List<User> users = baseUserDao.queryForListCondition(condition);
        if (!users.isEmpty()) {
            throw new ServiceException(USER_ALREADY_EXISTS,
                    serviceMessageSource.getMessage(USER_ALREADY_EXISTS, null,
                            null));
        } else {
            Timestamp curTimestamp = new Timestamp(System.currentTimeMillis());
            User user = new User();
            user.setCreateTime(curTimestamp);
            user.setEmail(email);
            user.setLastModifyTime(curTimestamp);
            user.setLoginName(loginName);
            user.setLoginPwd(loginPwd);
            user.setMobileNo(mobileNo);
            user.setNickName(nickName);
            user.setSex(sex);
            user.setStatus("1");
            user.setLastRecordLoginErrTime(null);
            user.setLoginErrCount(new Integer("0"));
            baseUserDao.insert(user);
        }
    }

    /**
     * 登录
     */
    public com.fastjrun.sdkg.demo.packet.app.LoginRestResponseBody login(
            com.fastjrun.sdkg.demo.packet.app.LoginRestRequestBody requestBody) {
        com.fastjrun.sdkg.demo.packet.app.LoginRestResponseBody loginRestResponseBody0 =
                new com.fastjrun.sdkg.demo.packet.app.LoginRestResponseBody();
        loginRestResponseBody0.setNickName(MockHelper.geStringWithAscii(30));
        loginRestResponseBody0.setSex(MockHelper.geStringWithAscii(1));
        loginRestResponseBody0.setMobileNo(MockHelper.geStringWithAscii(20));
        loginRestResponseBody0.setUuid(MockHelper.geStringWithAscii(64));
        loginRestResponseBody0.setEmail(MockHelper.geStringWithAscii(30));
        return loginRestResponseBody0;
    }

    /**
     * 登录v1.1
     */
    public com.fastjrun.sdkg.demo.packet.app.LoginRestResponseBody loginv1_1(
            com.fastjrun.sdkg.demo.packet.app.LoginRestRequestBody requestBody) {
        com.fastjrun.sdkg.demo.packet.app.LoginRestResponseBody loginRestResponseBody0 =
                new com.fastjrun.sdkg.demo.packet.app.LoginRestResponseBody();
        loginRestResponseBody0.setNickName(MockHelper.geStringWithAscii(30));
        loginRestResponseBody0.setSex(MockHelper.geStringWithAscii(1));
        loginRestResponseBody0.setMobileNo(MockHelper.geStringWithAscii(20));
        loginRestResponseBody0.setUuid(MockHelper.geStringWithAscii(64));
        loginRestResponseBody0.setEmail(MockHelper.geStringWithAscii(30));
        return loginRestResponseBody0;
    }

    /**
     * 自动登录
     */
    public com.fastjrun.sdkg.demo.packet.app.LoginRestResponseBody autoLogin(AutoLoginRestRequestBody requestBody) {
        com.fastjrun.sdkg.demo.packet.app.LoginRestResponseBody loginRestResponseBody0 =
                new com.fastjrun.sdkg.demo.packet.app.LoginRestResponseBody();
        loginRestResponseBody0.setNickName(MockHelper.geStringWithAscii(30));
        loginRestResponseBody0.setSex(MockHelper.geStringWithAscii(1));
        loginRestResponseBody0.setMobileNo(MockHelper.geStringWithAscii(20));
        loginRestResponseBody0.setUuid(MockHelper.geStringWithAscii(64));
        loginRestResponseBody0.setEmail(MockHelper.geStringWithAscii(30));
        return loginRestResponseBody0;
    }

}
