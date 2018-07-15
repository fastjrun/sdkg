
package com.fastjrun.share.sdk.client;

import java.util.HashMap;
import java.util.Map;
import com.fastjrun.client.BaseAppClient;
import com.fastjrun.share.sdk.packet.app.AutoLoginRestRequestBody;
import com.fastjrun.share.sdk.packet.app.LoginRestRequestBody;
import com.fastjrun.share.sdk.packet.app.LoginRestResponseBody;
import com.fastjrun.share.sdk.packet.app.RegistserRestRequestBody;
import com.fastjrun.share.sdk.service.UserServiceRestApp;
import net.sf.json.JSONObject;


/**
 * 注意：本内容仅限于公司内部传阅，禁止外泄以及用于其他的商业目的
 * 
 * @Copyright 2018 快嘉框架. All rights reserved.
 * @author cuiyingfeng
 */
public class UserAppClient
    extends BaseAppClient
    implements UserServiceRestApp
{


    /**
     * 注册
     * 
     */
    public void registerv2(RegistserRestRequestBody requestBody) {
        StringBuilder sbUrlReq = new StringBuilder(this.appUrlPre);
        sbUrlReq.append("/app/user/");
        sbUrlReq.append("register/v2");
        sbUrlReq.append(this.generateUrlSuffix());
        Map<String, String> requestProperties = new HashMap<String, String>();
        requestProperties.put("Content-Type", "application/json;charset=UTF-8");
        requestProperties.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36");
        requestProperties.put("Accept", "*/*");
        String requestBodyStr = JSONObject.fromObject(requestBody).toString();
        log.info(requestBodyStr);
        this.process(requestBodyStr, sbUrlReq.toString(), "POST", requestProperties);
    }

    /**
     * 登录
     * 
     */
    public LoginRestResponseBody login(LoginRestRequestBody requestBody) {
        StringBuilder sbUrlReq = new StringBuilder(this.appUrlPre);
        sbUrlReq.append("/app/user/");
        sbUrlReq.append("login");
        sbUrlReq.append(this.generateUrlSuffix());
        Map<String, String> requestProperties = new HashMap<String, String>();
        requestProperties.put("Content-Type", "application/json;charset=UTF-8");
        requestProperties.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36");
        requestProperties.put("Accept", "*/*");
        String requestBodyStr = JSONObject.fromObject(requestBody).toString();
        log.info(requestBodyStr);
        JSONObject responseBody = this.process(requestBodyStr, sbUrlReq.toString(), "POST", requestProperties);
        LoginRestResponseBody loginRestResponseBody = new LoginRestResponseBody();
        String loginRestResponseBodynickName = responseBody.getString("nickName");
        if ((!(loginRestResponseBodynickName == null))&&(!loginRestResponseBodynickName.equals(""))) {
            loginRestResponseBody.setNickName(loginRestResponseBodynickName);
        }
        String loginRestResponseBodysex = responseBody.getString("sex");
        if ((!(loginRestResponseBodysex == null))&&(!loginRestResponseBodysex.equals(""))) {
            loginRestResponseBody.setSex(loginRestResponseBodysex);
        }
        String loginRestResponseBodymobileNo = responseBody.getString("mobileNo");
        if ((!(loginRestResponseBodymobileNo == null))&&(!loginRestResponseBodymobileNo.equals(""))) {
            loginRestResponseBody.setMobileNo(loginRestResponseBodymobileNo);
        }
        String loginRestResponseBodyuuid = responseBody.getString("uuid");
        if ((!(loginRestResponseBodyuuid == null))&&(!loginRestResponseBodyuuid.equals(""))) {
            loginRestResponseBody.setUuid(loginRestResponseBodyuuid);
        }
        String loginRestResponseBodyemail = responseBody.getString("email");
        if ((!(loginRestResponseBodyemail == null))&&(!loginRestResponseBodyemail.equals(""))) {
            loginRestResponseBody.setEmail(loginRestResponseBodyemail);
        }
        return loginRestResponseBody;
    }

    /**
     * 登录v1.1
     * 
     */
    public LoginRestResponseBody loginv1_1(LoginRestRequestBody requestBody) {
        StringBuilder sbUrlReq = new StringBuilder(this.appUrlPre);
        sbUrlReq.append("/app/user/");
        sbUrlReq.append("login/v1_1");
        sbUrlReq.append(this.generateUrlSuffix());
        Map<String, String> requestProperties = new HashMap<String, String>();
        requestProperties.put("Content-Type", "application/json;charset=UTF-8");
        requestProperties.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36");
        requestProperties.put("Accept", "*/*");
        String requestBodyStr = JSONObject.fromObject(requestBody).toString();
        log.info(requestBodyStr);
        JSONObject responseBody = this.process(requestBodyStr, sbUrlReq.toString(), "POST", requestProperties);
        LoginRestResponseBody loginRestResponseBody = new LoginRestResponseBody();
        String loginRestResponseBodynickName = responseBody.getString("nickName");
        if ((!(loginRestResponseBodynickName == null))&&(!loginRestResponseBodynickName.equals(""))) {
            loginRestResponseBody.setNickName(loginRestResponseBodynickName);
        }
        String loginRestResponseBodysex = responseBody.getString("sex");
        if ((!(loginRestResponseBodysex == null))&&(!loginRestResponseBodysex.equals(""))) {
            loginRestResponseBody.setSex(loginRestResponseBodysex);
        }
        String loginRestResponseBodymobileNo = responseBody.getString("mobileNo");
        if ((!(loginRestResponseBodymobileNo == null))&&(!loginRestResponseBodymobileNo.equals(""))) {
            loginRestResponseBody.setMobileNo(loginRestResponseBodymobileNo);
        }
        String loginRestResponseBodyuuid = responseBody.getString("uuid");
        if ((!(loginRestResponseBodyuuid == null))&&(!loginRestResponseBodyuuid.equals(""))) {
            loginRestResponseBody.setUuid(loginRestResponseBodyuuid);
        }
        String loginRestResponseBodyemail = responseBody.getString("email");
        if ((!(loginRestResponseBodyemail == null))&&(!loginRestResponseBodyemail.equals(""))) {
            loginRestResponseBody.setEmail(loginRestResponseBodyemail);
        }
        return loginRestResponseBody;
    }

    /**
     * 自动登录
     * 
     */
    public LoginRestResponseBody autoLogin(AutoLoginRestRequestBody requestBody) {
        StringBuilder sbUrlReq = new StringBuilder(this.appUrlPre);
        sbUrlReq.append("/app/user/");
        sbUrlReq.append("autoLogin");
        sbUrlReq.append(this.generateUrlSuffix());
        Map<String, String> requestProperties = new HashMap<String, String>();
        requestProperties.put("Content-Type", "application/json;charset=UTF-8");
        requestProperties.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36");
        requestProperties.put("Accept", "*/*");
        String requestBodyStr = JSONObject.fromObject(requestBody).toString();
        log.info(requestBodyStr);
        JSONObject responseBody = this.process(requestBodyStr, sbUrlReq.toString(), "POST", requestProperties);
        LoginRestResponseBody loginRestResponseBody = new LoginRestResponseBody();
        String loginRestResponseBodynickName = responseBody.getString("nickName");
        if ((!(loginRestResponseBodynickName == null))&&(!loginRestResponseBodynickName.equals(""))) {
            loginRestResponseBody.setNickName(loginRestResponseBodynickName);
        }
        String loginRestResponseBodysex = responseBody.getString("sex");
        if ((!(loginRestResponseBodysex == null))&&(!loginRestResponseBodysex.equals(""))) {
            loginRestResponseBody.setSex(loginRestResponseBodysex);
        }
        String loginRestResponseBodymobileNo = responseBody.getString("mobileNo");
        if ((!(loginRestResponseBodymobileNo == null))&&(!loginRestResponseBodymobileNo.equals(""))) {
            loginRestResponseBody.setMobileNo(loginRestResponseBodymobileNo);
        }
        String loginRestResponseBodyuuid = responseBody.getString("uuid");
        if ((!(loginRestResponseBodyuuid == null))&&(!loginRestResponseBodyuuid.equals(""))) {
            loginRestResponseBody.setUuid(loginRestResponseBodyuuid);
        }
        String loginRestResponseBodyemail = responseBody.getString("email");
        if ((!(loginRestResponseBodyemail == null))&&(!loginRestResponseBodyemail.equals(""))) {
            loginRestResponseBody.setEmail(loginRestResponseBodyemail);
        }
        return loginRestResponseBody;
    }

}
