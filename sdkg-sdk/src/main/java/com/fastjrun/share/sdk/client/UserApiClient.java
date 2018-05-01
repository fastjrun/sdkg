
package com.fastjrun.share.sdk.client;

import java.util.HashMap;
import java.util.Map;
import com.fastjrun.sdkg.client.BaseApiClient;
import com.fastjrun.sdkg.helper.EncryptHelper;
import com.fastjrun.share.sdk.packet.api.AutoLoginRestRequestBody;
import com.fastjrun.share.sdk.packet.api.RegistserRestRequestBody;
import net.sf.json.JSONObject;


/**
 * 
 * @author fastjrun
 */
public class UserApiClient
    extends BaseApiClient
{


    public void register(RegistserRestRequestBody requestBody, String accessKey) {
        StringBuilder sbUrlReq = new StringBuilder(this.apiUrlPre);
        sbUrlReq.append("/api/user/");
        sbUrlReq.append("register");
        sbUrlReq.append("/");
        sbUrlReq.append(accessKey);
        sbUrlReq.append("/");
        long txTime = System.currentTimeMillis();
        sbUrlReq.append(txTime);
        sbUrlReq.append("/");
        try {
            String md5Hash = EncryptHelper.md5Digest((this.getAccessKeySn()+ txTime));
            sbUrlReq.append(md5Hash);
        } catch (Exception e) {
            this.log.warn("", e);
        }
        Map<java.lang.String, java.lang.String> requestProperties = new HashMap<java.lang.String, java.lang.String>();
        requestProperties.put("Content-Type", "application/json");
        requestProperties.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36");
        requestProperties.put("Accept", "*/*");
        String requestStr = JSONObject.fromObject(requestBody).toString();
        log.info(requestStr);
        this.process(requestStr, sbUrlReq.toString(), "POST", requestProperties);
    }

    public com.fastjrun.share.sdk.packet.api.LoginRestResponseBody login(com.fastjrun.share.sdk.packet.api.LoginRestRequestBody requestBody, String accessKey) {
        StringBuilder sbUrlReq = new StringBuilder(this.apiUrlPre);
        sbUrlReq.append("/api/user/");
        sbUrlReq.append("login");
        sbUrlReq.append("/");
        sbUrlReq.append(accessKey);
        sbUrlReq.append("/");
        long txTime = System.currentTimeMillis();
        sbUrlReq.append(txTime);
        sbUrlReq.append("/");
        try {
            String md5Hash = EncryptHelper.md5Digest((this.getAccessKeySn()+ txTime));
            sbUrlReq.append(md5Hash);
        } catch (Exception e) {
            this.log.warn("", e);
        }
        Map<java.lang.String, java.lang.String> requestProperties = new HashMap<java.lang.String, java.lang.String>();
        requestProperties.put("Content-Type", "application/json");
        requestProperties.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36");
        requestProperties.put("Accept", "*/*");
        String requestStr = JSONObject.fromObject(requestBody).toString();
        log.info(requestStr);
        JSONObject responseBody = this.process(requestStr, sbUrlReq.toString(), "POST", requestProperties);
        com.fastjrun.share.sdk.packet.api.LoginRestResponseBody loginRestResponseBody = new com.fastjrun.share.sdk.packet.api.LoginRestResponseBody();
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

    public com.fastjrun.share.sdk.packet.api.LoginRestResponseBody loginv1_1(com.fastjrun.share.sdk.packet.api.LoginRestRequestBody requestBody, String accessKey) {
        StringBuilder sbUrlReq = new StringBuilder(this.apiUrlPre);
        sbUrlReq.append("/api/user/");
        sbUrlReq.append("login/v1_1");
        sbUrlReq.append("/");
        sbUrlReq.append(accessKey);
        sbUrlReq.append("/");
        long txTime = System.currentTimeMillis();
        sbUrlReq.append(txTime);
        sbUrlReq.append("/");
        try {
            String md5Hash = EncryptHelper.md5Digest((this.getAccessKeySn()+ txTime));
            sbUrlReq.append(md5Hash);
        } catch (Exception e) {
            this.log.warn("", e);
        }
        Map<java.lang.String, java.lang.String> requestProperties = new HashMap<java.lang.String, java.lang.String>();
        requestProperties.put("Content-Type", "application/json");
        requestProperties.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36");
        requestProperties.put("Accept", "*/*");
        String requestStr = JSONObject.fromObject(requestBody).toString();
        log.info(requestStr);
        JSONObject responseBody = this.process(requestStr, sbUrlReq.toString(), "POST", requestProperties);
        com.fastjrun.share.sdk.packet.api.LoginRestResponseBody loginRestResponseBody = new com.fastjrun.share.sdk.packet.api.LoginRestResponseBody();
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

    public com.fastjrun.share.sdk.packet.api.LoginRestResponseBody autoLogin(AutoLoginRestRequestBody requestBody, String accessKey) {
        StringBuilder sbUrlReq = new StringBuilder(this.apiUrlPre);
        sbUrlReq.append("/api/user/");
        sbUrlReq.append("autoLogin");
        sbUrlReq.append("/");
        sbUrlReq.append(accessKey);
        sbUrlReq.append("/");
        long txTime = System.currentTimeMillis();
        sbUrlReq.append(txTime);
        sbUrlReq.append("/");
        try {
            String md5Hash = EncryptHelper.md5Digest((this.getAccessKeySn()+ txTime));
            sbUrlReq.append(md5Hash);
        } catch (Exception e) {
            this.log.warn("", e);
        }
        Map<java.lang.String, java.lang.String> requestProperties = new HashMap<java.lang.String, java.lang.String>();
        requestProperties.put("Content-Type", "application/json");
        requestProperties.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36");
        requestProperties.put("Accept", "*/*");
        String requestStr = JSONObject.fromObject(requestBody).toString();
        log.info(requestStr);
        JSONObject responseBody = this.process(requestStr, sbUrlReq.toString(), "POST", requestProperties);
        com.fastjrun.share.sdk.packet.api.LoginRestResponseBody loginRestResponseBody = new com.fastjrun.share.sdk.packet.api.LoginRestResponseBody();
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
