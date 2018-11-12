/*
 * Copyright (C) 2018 Fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.sdkg.demo.service.impl;

import java.sql.Timestamp;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.fastjrun.common.ServiceException;
import com.fastjrun.sdkg.demo.dao.BaseUserDao;
import com.fastjrun.sdkg.demo.entity.User;
import com.fastjrun.sdkg.demo.packet.generic.AutoLoginRestRequestBody;
import com.fastjrun.sdkg.demo.packet.generic.LoginRestRequestBody;
import com.fastjrun.sdkg.demo.packet.generic.LoginRestResponseBody;
import com.fastjrun.sdkg.demo.packet.generic.RegistserRestRequestBody;
import com.fastjrun.sdkg.demo.service.UserServiceRPC;
import com.fastjrun.service.BaseService;

@Service
public class UserServiceRPCImpl extends BaseService implements UserServiceRPC {

    @Resource
    private BaseUserDao baseUserDao;

    private final static String USER_ALREADY_EXISTS = "0001";

    @Override
    public void register(RegistserRestRequestBody requestBody) {
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

    @Override
    public LoginRestResponseBody login(LoginRestRequestBody requestBody) {
        return null;
    }

    @Override
    public LoginRestResponseBody loginv1_1(LoginRestRequestBody requestBody) {
        return null;
    }

    @Override
    public LoginRestResponseBody autoLogin(AutoLoginRestRequestBody requestBody) {
        return null;
    }
}
