/*
 * Copyright (C) 2018 Fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.sdkg.demo.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.fastjrun.sdkg.demo.dao.BaseUserLoginDao;
import com.fastjrun.sdkg.demo.packet.generic.AutoLoginRestRequestBody;
import com.fastjrun.sdkg.demo.packet.generic.LoginRestRequestBody;
import com.fastjrun.sdkg.demo.packet.generic.LoginRestResponseBody;
import com.fastjrun.sdkg.demo.packet.generic.RegistserRestRequestBody;
import com.fastjrun.sdkg.demo.service.UserServiceRPC;

@Service
public class UserServiceRPCImpl implements UserServiceRPC {
    @Resource
    BaseUserLoginDao baseUserLoginDao;

    @Override
    public void register(RegistserRestRequestBody requestBody) {

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
