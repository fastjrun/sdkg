
package com.fastjrun.share.sdk.client;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import com.fastjrun.share.sdk.client.UserApiClient;
import com.fastjrun.share.sdk.packet.api.AutoLoginRestRequestBody;
import com.fastjrun.share.sdk.packet.api.RegistserRestRequestBody;
import net.sf.json.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;


/**
 * 注意：本内容仅限于公司内部传阅，禁止外泄以及用于其他的商业目的
 * 
 * @Copyright 2018 快嘉框架. All rights reserved.
 * @author cuiyingfeng
 */
public class UserApiClientTest {

    final Logger log = LogManager.getLogger(this.getClass());
    UserApiClient userApiClient = new UserApiClient();
    Properties propParams = new Properties();

    @BeforeTest
    @org.testng.annotations.Parameters({
        "envName"
    })
    public void init(String envName) {
        userApiClient.initSDKConfig("apiworld");
        try {
            InputStream inParam = UserApiClient.class.getResourceAsStream((("/testdata/"+ envName)+".properties"));
            propParams.load(inParam);
        } catch (IOException _x) {
            _x.printStackTrace();
        }
    }

    @DataProvider(name = "loadParam")
    public Object[][] loadParam(Method method) {
        Set<String> keys = propParams.stringPropertyNames();
        List<String[]> parameters = new ArrayList<String[]>();
        for (String key: keys) {
            if (key.startsWith((("UserApiClient"+".")+(method.getName()+".")))) {
                String value = propParams.getProperty(key);
                parameters.add(new String[] {value });
            }
        }
        Object[][] object = new Object[parameters.size()][] ;
        for (int i = 0; (i<object.length); i ++) {
            String[] str = parameters.get(i);
            object[i] = new String[str.length] ;
            for (int j = 0; (j<str.length); j ++) {
                object[i][j] = str[j];
            }
        }
        return object;
    }

    @org.testng.annotations.Test(dataProvider = "loadParam")
    @org.testng.annotations.Parameters({
        "reqParamsJsonStr"
    })
    public void register(String reqParamsJsonStr) {
        JSONObject reqParamsJson = JSONObject.fromObject(reqParamsJsonStr);
        JSONObject reqJsonRequest = reqParamsJson.getJSONObject("request");
        RegistserRestRequestBody registserRestRequestBody = new RegistserRestRequestBody();
        String registserRestRequestBodyloginId = reqJsonRequest.getString("loginId");
        if ((!(registserRestRequestBodyloginId == null))&&(!registserRestRequestBodyloginId.equals(""))) {
            registserRestRequestBody.setLoginId(registserRestRequestBodyloginId);
        }
        String registserRestRequestBodyloginpwd = reqJsonRequest.getString("loginpwd");
        if ((!(registserRestRequestBodyloginpwd == null))&&(!registserRestRequestBodyloginpwd.equals(""))) {
            registserRestRequestBody.setLoginpwd(registserRestRequestBodyloginpwd);
        }
        String registserRestRequestBodynickName = reqJsonRequest.getString("nickName");
        if ((!(registserRestRequestBodynickName == null))&&(!registserRestRequestBodynickName.equals(""))) {
            registserRestRequestBody.setNickName(registserRestRequestBodynickName);
        }
        String registserRestRequestBodysex = reqJsonRequest.getString("sex");
        if ((!(registserRestRequestBodysex == null))&&(!registserRestRequestBodysex.equals(""))) {
            registserRestRequestBody.setSex(registserRestRequestBodysex);
        }
        String registserRestRequestBodymobileNo = reqJsonRequest.getString("mobileNo");
        if ((!(registserRestRequestBodymobileNo == null))&&(!registserRestRequestBodymobileNo.equals(""))) {
            registserRestRequestBody.setMobileNo(registserRestRequestBodymobileNo);
        }
        String registserRestRequestBodyemail = reqJsonRequest.getString("email");
        if ((!(registserRestRequestBodyemail == null))&&(!registserRestRequestBodyemail.equals(""))) {
            registserRestRequestBody.setEmail(registserRestRequestBodyemail);
        }
        String accessKey = reqParamsJson.optString("accessKey");
        try {
            userApiClient.register(registserRestRequestBody, accessKey);
        } catch (Exception _x) {
            _x.printStackTrace();
        }
    }

    @org.testng.annotations.Test(dataProvider = "loadParam")
    @org.testng.annotations.Parameters({
        "reqParamsJsonStr"
    })
    public void login(String reqParamsJsonStr) {
        JSONObject reqParamsJson = JSONObject.fromObject(reqParamsJsonStr);
        JSONObject reqJsonRequest = reqParamsJson.getJSONObject("request");
        com.fastjrun.share.sdk.packet.api.LoginRestRequestBody loginRestRequestBody = new com.fastjrun.share.sdk.packet.api.LoginRestRequestBody();
        String loginRestRequestBodyloginpwd = reqJsonRequest.getString("loginpwd");
        if ((!(loginRestRequestBodyloginpwd == null))&&(!loginRestRequestBodyloginpwd.equals(""))) {
            loginRestRequestBody.setLoginpwd(loginRestRequestBodyloginpwd);
        }
        String loginRestRequestBodyloginName = reqJsonRequest.getString("loginName");
        if ((!(loginRestRequestBodyloginName == null))&&(!loginRestRequestBodyloginName.equals(""))) {
            loginRestRequestBody.setLoginName(loginRestRequestBodyloginName);
        }
        String accessKey = reqParamsJson.optString("accessKey");
        try {
            com.fastjrun.share.sdk.packet.api.LoginRestResponseBody responseBody = userApiClient.login(loginRestRequestBody, accessKey);
            log.info(responseBody);
        } catch (Exception _x) {
            _x.printStackTrace();
        }
    }

    @org.testng.annotations.Test(dataProvider = "loadParam")
    @org.testng.annotations.Parameters({
        "reqParamsJsonStr"
    })
    public void loginv1_1(String reqParamsJsonStr) {
        JSONObject reqParamsJson = JSONObject.fromObject(reqParamsJsonStr);
        JSONObject reqJsonRequest = reqParamsJson.getJSONObject("request");
        com.fastjrun.share.sdk.packet.api.LoginRestRequestBody loginRestRequestBody = new com.fastjrun.share.sdk.packet.api.LoginRestRequestBody();
        String loginRestRequestBodyloginpwd = reqJsonRequest.getString("loginpwd");
        if ((!(loginRestRequestBodyloginpwd == null))&&(!loginRestRequestBodyloginpwd.equals(""))) {
            loginRestRequestBody.setLoginpwd(loginRestRequestBodyloginpwd);
        }
        String loginRestRequestBodyloginName = reqJsonRequest.getString("loginName");
        if ((!(loginRestRequestBodyloginName == null))&&(!loginRestRequestBodyloginName.equals(""))) {
            loginRestRequestBody.setLoginName(loginRestRequestBodyloginName);
        }
        String accessKey = reqParamsJson.optString("accessKey");
        try {
            com.fastjrun.share.sdk.packet.api.LoginRestResponseBody responseBody = userApiClient.loginv1_1(loginRestRequestBody, accessKey);
            log.info(responseBody);
        } catch (Exception _x) {
            _x.printStackTrace();
        }
    }

    @org.testng.annotations.Test(dataProvider = "loadParam")
    @org.testng.annotations.Parameters({
        "reqParamsJsonStr"
    })
    public void autoLogin(String reqParamsJsonStr) {
        JSONObject reqParamsJson = JSONObject.fromObject(reqParamsJsonStr);
        JSONObject reqJsonRequest = reqParamsJson.getJSONObject("request");
        AutoLoginRestRequestBody autoLoginRestRequestBody = new AutoLoginRestRequestBody();
        String autoLoginRestRequestBodyuuidOld = reqJsonRequest.getString("uuidOld");
        if ((!(autoLoginRestRequestBodyuuidOld == null))&&(!autoLoginRestRequestBodyuuidOld.equals(""))) {
            autoLoginRestRequestBody.setUuidOld(autoLoginRestRequestBodyuuidOld);
        }
        String accessKey = reqParamsJson.optString("accessKey");
        try {
            com.fastjrun.share.sdk.packet.api.LoginRestResponseBody responseBody = userApiClient.autoLogin(autoLoginRestRequestBody, accessKey);
            log.info(responseBody);
        } catch (Exception _x) {
            _x.printStackTrace();
        }
    }

}
