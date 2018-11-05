package com.fastjrun.sdkg.demo.service;

import java.util.Date;

import com.fastjrun.sdkg.demo.entity.User;

public interface CoreUserService {

    void checkLoign(String uuid, String deviceId);

    User autoLogin(String deviceId, String uuidOld, String uuidNew);

    User login(String loginName, String loginPwd, String deviceId, String uuid);

    void logOut(String uuid, String deviceId);

    void unlockUserPwd(Date date);

    void inValidUserLoginCredential(Date date);
}
