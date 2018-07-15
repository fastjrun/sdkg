
package com.fastjrun.share.sdk.client;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import com.fastjrun.share.sdk.client.VersionServiceRPCRPCClient;
import com.fastjrun.share.sdk.packet.generic.VersionListResponseBody;
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
public class VersionServiceRPCRPCClientTest {

    final Logger log = LogManager.getLogger(this.getClass());
    VersionServiceRPCRPCClient versionServiceRPCRPCClient = new VersionServiceRPCRPCClient();
    Properties propParams = new Properties();

    @BeforeTest
    @org.testng.annotations.Parameters({
        "envName"
    })
    public void init(String envName) {
        versionServiceRPCRPCClient.initSDKConfig("apiworld");
        try {
            InputStream inParam = VersionServiceRPCRPCClient.class.getResourceAsStream((("/testdata/"+ envName)+".properties"));
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
            if (key.startsWith((("VersionServiceRPCRPCClient"+".")+(method.getName()+".")))) {
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
    public void testLatests(String reqParamsJsonStr) {
        log.info(reqParamsJsonStr);
        JSONObject reqParamsJson = JSONObject.fromObject(reqParamsJsonStr);
        try {
            VersionListResponseBody responseBody = versionServiceRPCRPCClient.latests();
            log.info(responseBody);
        } catch (Exception _x) {
            _x.printStackTrace();
        }
    }

    @org.testng.annotations.Test(dataProvider = "loadParam")
    @org.testng.annotations.Parameters({
        "reqParamsJsonStr"
    })
    public void testLatestsv2(String reqParamsJsonStr) {
        log.info(reqParamsJsonStr);
        JSONObject reqParamsJson = JSONObject.fromObject(reqParamsJsonStr);
        Integer pageNum = reqParamsJson.getInt("pageNum");
        Integer pageIndex = reqParamsJson.getInt("pageIndex");
        try {
            VersionListResponseBody responseBody = versionServiceRPCRPCClient.latestsv2(pageNum, pageIndex);
            log.info(responseBody);
        } catch (Exception _x) {
            _x.printStackTrace();
        }
    }

}
