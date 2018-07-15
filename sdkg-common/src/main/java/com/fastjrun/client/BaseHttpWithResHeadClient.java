package com.fastjrun.client;

import com.fastjrun.common.CodeException;
import net.sf.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public abstract class BaseHttpWithResHeadClient extends BaseHttpClient {
    @Override
    protected JSONObject process(String reqStr, String urlReq, String method,
                                 Map<String, String> requestProperties) {
        JSONObject responseJsonObject = this.processInternal(reqStr, urlReq,
                method, requestProperties);
        JSONObject responseJsonHead = responseJsonObject.getJSONObject("head");
        if (responseJsonHead == null) {
            throw new CodeException("603", "返回数据head为空");
        }
        String code = responseJsonHead.getString("code");
        if (code == null || code.equals("")) {
            throw new CodeException("604", "返回数据head中code为空");
        }
        if (code.equals("0000")) {
            return responseJsonObject
                    .getJSONObject("body");

        }
        String msg = responseJsonHead.getString("msg");
        if (msg == null) {
            msg = "";
        }
        throw new CodeException(code, msg);
    }

    @Override
    protected JSONObject process(String reqStr, String urlReq, String method) {
        Map<String, String> requestProperties = new HashMap<>();
        requestProperties.put("Content-Type", "application/json");
        requestProperties
                .put("User-Agent",
                        "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36");

        requestProperties.put("Accept", "*/*");
        return this.process(reqStr, urlReq, method, requestProperties);
    }
}
