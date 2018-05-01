
package com.fastjrun.share.sdk.client;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import com.fastjrun.share.sdk.client.AppVersionGenericClient;
import net.sf.json.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;

public class AppVersionGenericClientTest {

    final Logger log = LogManager.getLogger(this.getClass());
    AppVersionGenericClient appVersionGenericClient = new AppVersionGenericClient();
    Properties propParams = new Properties();

    @BeforeTest
    @org.testng.annotations.Parameters({
        "envName"
    })
    public void init(String envName) {
        appVersionGenericClient.initSDKConfig("apiworld");
        try {
            InputStream inParam = AppVersionGenericClient.class.getResourceAsStream((("/testdata/"+ envName)+".properties"));
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
            if (key.startsWith((("AppVersionGenericClient"+".")+(method.getName()+".")))) {
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
    public void latests(String reqParamsJsonStr) {
        JSONObject reqParamsJson = JSONObject.fromObject(reqParamsJsonStr);
        try {
            com.fastjrun.share.sdk.packet.generic.VersionListResponseBody response = appVersionGenericClient.latests();
            log.info(response);
        } catch (Exception _x) {
            _x.printStackTrace();
        }
    }

    @org.testng.annotations.Test(dataProvider = "loadParam")
    @org.testng.annotations.Parameters({
        "reqParamsJsonStr"
    })
    public void latestsv2(String reqParamsJsonStr) {
        JSONObject reqParamsJson = JSONObject.fromObject(reqParamsJsonStr);
        String appKey = String.valueOf(reqParamsJson.getString("appKey"));
        Long accessTime = Long.valueOf(reqParamsJson.getLong("accessTime"));
        Integer pageIndex = Integer.valueOf(reqParamsJson.getInt("pageIndex"));
        Integer pageNum = Integer.valueOf(reqParamsJson.getInt("pageNum"));
        try {
            com.fastjrun.share.sdk.packet.generic.VersionListResponseBody response = appVersionGenericClient.latestsv2(appKey, accessTime, pageIndex, pageNum);
            log.info(response);
        } catch (Exception _x) {
            _x.printStackTrace();
        }
    }

}
