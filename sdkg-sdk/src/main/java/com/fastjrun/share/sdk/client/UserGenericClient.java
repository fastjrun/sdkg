
package com.fastjrun.share.sdk.client;

import java.util.HashMap;
import java.util.Map;
import com.fastjrun.client.BaseGenericClient;
import com.fastjrun.share.sdk.packet.generic.AutoLoginRestRequestBody;
import com.fastjrun.share.sdk.packet.generic.RegistserRestRequestBody;
import net.sf.json.JSONObject;

/**
 * 注意：本内容仅限于公司内部传阅，禁止外泄以及用于其他的商业目的
 * 
 * @Copyright 2018 快嘉框架. All rights reserved.
 * @author cuiyingfeng
 */
public class UserGenericClient extends BaseGenericClient {

	public void register(RegistserRestRequestBody request) {
		StringBuilder sbUrlReq = new StringBuilder(this.genericUrlPre);
		sbUrlReq.append("/generic/user/");
		sbUrlReq.append("register");
		Map<java.lang.String, java.lang.String> requestProperties = new HashMap<java.lang.String, java.lang.String>();
		requestProperties.put("Content-Type", "application/json");
		requestProperties.put("User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36");
		requestProperties.put("Accept", "*/*");
		String requestStr = JSONObject.fromObject(request).toString();
		log.info(requestStr);
		this.process(requestStr, sbUrlReq.toString(), "POST", requestProperties);
	}

	public com.fastjrun.share.sdk.packet.generic.LoginRestResponseBody login(
			com.fastjrun.share.sdk.packet.generic.LoginRestRequestBody request) {
		StringBuilder sbUrlReq = new StringBuilder(this.genericUrlPre);
		sbUrlReq.append("/generic/user/");
		sbUrlReq.append("login");
		Map<java.lang.String, java.lang.String> requestProperties = new HashMap<java.lang.String, java.lang.String>();
		requestProperties.put("Content-Type", "application/json");
		requestProperties.put("User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36");
		requestProperties.put("Accept", "*/*");
		String requestStr = JSONObject.fromObject(request).toString();
		log.info(requestStr);
		JSONObject response = this.process(requestStr, sbUrlReq.toString(), "POST", requestProperties);
		com.fastjrun.share.sdk.packet.generic.LoginRestResponseBody loginRestResponseBody = new com.fastjrun.share.sdk.packet.generic.LoginRestResponseBody();
		String loginRestResponseBodynickName = response.getString("nickName");
		if ((!(loginRestResponseBodynickName == null)) && (!loginRestResponseBodynickName.equals(""))) {
			loginRestResponseBody.setNickName(loginRestResponseBodynickName);
		}
		String loginRestResponseBodysex = response.getString("sex");
		if ((!(loginRestResponseBodysex == null)) && (!loginRestResponseBodysex.equals(""))) {
			loginRestResponseBody.setSex(loginRestResponseBodysex);
		}
		String loginRestResponseBodymobileNo = response.getString("mobileNo");
		if ((!(loginRestResponseBodymobileNo == null)) && (!loginRestResponseBodymobileNo.equals(""))) {
			loginRestResponseBody.setMobileNo(loginRestResponseBodymobileNo);
		}
		String loginRestResponseBodyuuid = response.getString("uuid");
		if ((!(loginRestResponseBodyuuid == null)) && (!loginRestResponseBodyuuid.equals(""))) {
			loginRestResponseBody.setUuid(loginRestResponseBodyuuid);
		}
		String loginRestResponseBodyemail = response.getString("email");
		if ((!(loginRestResponseBodyemail == null)) && (!loginRestResponseBodyemail.equals(""))) {
			loginRestResponseBody.setEmail(loginRestResponseBodyemail);
		}
		return loginRestResponseBody;
	}

	public com.fastjrun.share.sdk.packet.generic.LoginRestResponseBody loginv1_1(
			com.fastjrun.share.sdk.packet.generic.LoginRestRequestBody request) {
		StringBuilder sbUrlReq = new StringBuilder(this.genericUrlPre);
		sbUrlReq.append("/generic/user/");
		sbUrlReq.append("login/v1_1");
		Map<java.lang.String, java.lang.String> requestProperties = new HashMap<java.lang.String, java.lang.String>();
		requestProperties.put("Content-Type", "application/json");
		requestProperties.put("User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36");
		requestProperties.put("Accept", "*/*");
		String requestStr = JSONObject.fromObject(request).toString();
		log.info(requestStr);
		JSONObject response = this.process(requestStr, sbUrlReq.toString(), "POST", requestProperties);
		com.fastjrun.share.sdk.packet.generic.LoginRestResponseBody loginRestResponseBody = new com.fastjrun.share.sdk.packet.generic.LoginRestResponseBody();
		String loginRestResponseBodynickName = response.getString("nickName");
		if ((!(loginRestResponseBodynickName == null)) && (!loginRestResponseBodynickName.equals(""))) {
			loginRestResponseBody.setNickName(loginRestResponseBodynickName);
		}
		String loginRestResponseBodysex = response.getString("sex");
		if ((!(loginRestResponseBodysex == null)) && (!loginRestResponseBodysex.equals(""))) {
			loginRestResponseBody.setSex(loginRestResponseBodysex);
		}
		String loginRestResponseBodymobileNo = response.getString("mobileNo");
		if ((!(loginRestResponseBodymobileNo == null)) && (!loginRestResponseBodymobileNo.equals(""))) {
			loginRestResponseBody.setMobileNo(loginRestResponseBodymobileNo);
		}
		String loginRestResponseBodyuuid = response.getString("uuid");
		if ((!(loginRestResponseBodyuuid == null)) && (!loginRestResponseBodyuuid.equals(""))) {
			loginRestResponseBody.setUuid(loginRestResponseBodyuuid);
		}
		String loginRestResponseBodyemail = response.getString("email");
		if ((!(loginRestResponseBodyemail == null)) && (!loginRestResponseBodyemail.equals(""))) {
			loginRestResponseBody.setEmail(loginRestResponseBodyemail);
		}
		return loginRestResponseBody;
	}

	public com.fastjrun.share.sdk.packet.generic.LoginRestResponseBody autoLogin(AutoLoginRestRequestBody request) {
		StringBuilder sbUrlReq = new StringBuilder(this.genericUrlPre);
		sbUrlReq.append("/generic/user/");
		sbUrlReq.append("autoLogin");
		Map<java.lang.String, java.lang.String> requestProperties = new HashMap<java.lang.String, java.lang.String>();
		requestProperties.put("Content-Type", "application/json");
		requestProperties.put("User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36");
		requestProperties.put("Accept", "*/*");
		String requestStr = JSONObject.fromObject(request).toString();
		log.info(requestStr);
		JSONObject response = this.process(requestStr, sbUrlReq.toString(), "POST", requestProperties);
		com.fastjrun.share.sdk.packet.generic.LoginRestResponseBody loginRestResponseBody = new com.fastjrun.share.sdk.packet.generic.LoginRestResponseBody();
		String loginRestResponseBodynickName = response.getString("nickName");
		if ((!(loginRestResponseBodynickName == null)) && (!loginRestResponseBodynickName.equals(""))) {
			loginRestResponseBody.setNickName(loginRestResponseBodynickName);
		}
		String loginRestResponseBodysex = response.getString("sex");
		if ((!(loginRestResponseBodysex == null)) && (!loginRestResponseBodysex.equals(""))) {
			loginRestResponseBody.setSex(loginRestResponseBodysex);
		}
		String loginRestResponseBodymobileNo = response.getString("mobileNo");
		if ((!(loginRestResponseBodymobileNo == null)) && (!loginRestResponseBodymobileNo.equals(""))) {
			loginRestResponseBody.setMobileNo(loginRestResponseBodymobileNo);
		}
		String loginRestResponseBodyuuid = response.getString("uuid");
		if ((!(loginRestResponseBodyuuid == null)) && (!loginRestResponseBodyuuid.equals(""))) {
			loginRestResponseBody.setUuid(loginRestResponseBodyuuid);
		}
		String loginRestResponseBodyemail = response.getString("email");
		if ((!(loginRestResponseBodyemail == null)) && (!loginRestResponseBodyemail.equals(""))) {
			loginRestResponseBody.setEmail(loginRestResponseBodyemail);
		}
		return loginRestResponseBody;
	}

}
