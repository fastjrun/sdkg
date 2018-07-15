
package com.alibaba.testsdk.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.alibaba.testsdk.packet.generic.VersionListResponseBody;
import com.alibaba.testsdk.service.VersionServiceRestGeneric;
import com.fastjrun.client.BaseGenericClient;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


/**
 * 注意：本内容仅限于公司内部传阅，禁止外泄以及用于其他的商业目的
 * 
 * @Copyright 2018 快嘉框架. All rights reserved.
 * @author cuiyingfeng
 */
public class AppVersionGenericClient
    extends BaseGenericClient
    implements VersionServiceRestGeneric
{


    /**
     * 最近版本列表
     * 
     */
    public VersionListResponseBody latests() {
        StringBuilder sbUrlReq = new StringBuilder(this.genericUrlPre);
        sbUrlReq.append("/generic/version/");
        sbUrlReq.append("latests");
        sbUrlReq.append(this.generateUrlSuffix());
        Map<String, String> requestProperties = new HashMap<String, String>();
        requestProperties.put("Content-Type", "application/json;charset=UTF-8");
        requestProperties.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36");
        requestProperties.put("Accept", "*/*");
        JSONObject responseBody = this.process("", sbUrlReq.toString(), "POST", requestProperties);
        VersionListResponseBody versionListResponseBody = new VersionListResponseBody();
        JSONArray versionListResponseBodyVersionJA = responseBody.getJSONArray("list");
        List<com.alibaba.testsdk.packet.generic.Version> versionListResponseBodyVersionlist = new ArrayList<com.alibaba.testsdk.packet.generic.Version>();
        for (int versionListResponseBodyI0 = 0; (versionListResponseBodyI0 <versionListResponseBodyVersionJA.size()); versionListResponseBodyI0 ++) {
            JSONObject versionListResponseBodyVersionjo = JSONObject.fromObject(versionListResponseBodyVersionJA.get(versionListResponseBodyI0));
            com.alibaba.testsdk.packet.generic.Version version = new com.alibaba.testsdk.packet.generic.Version();
            String versionversionNo = versionListResponseBodyVersionjo.getString("versionNo");
            if ((!(versionversionNo == null))&&(!versionversionNo.equals(""))) {
                version.setVersionNo(versionversionNo);
            }
            Long versionid = versionListResponseBodyVersionjo.getLong("id");
            if ((!(versionid == null))&&(!versionid.equals(""))) {
                version.setId(versionid);
            }
            String versionversionInfo = versionListResponseBodyVersionjo.getString("versionInfo");
            if ((!(versionversionInfo == null))&&(!versionversionInfo.equals(""))) {
                version.setVersionInfo(versionversionInfo);
            }
            versionListResponseBodyVersionlist.add(version);
        }
        versionListResponseBody.setList(versionListResponseBodyVersionlist);
        return versionListResponseBody;
    }

    /**
     * 最近版本列表
     * 
     */
    public VersionListResponseBody latestsv2(String appKey, Long accessTime, Integer pageNum, Integer pageIndex) {
        StringBuilder sbUrlReq = new StringBuilder(this.genericUrlPre);
        sbUrlReq.append("/generic/version/");
        sbUrlReq.append("latests/v2");
        sbUrlReq.append(this.generateUrlSuffix());
        Map<String, String> requestProperties = new HashMap<String, String>();
        requestProperties.put("Content-Type", "application/json;charset=UTF-8");
        requestProperties.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36");
        requestProperties.put("Accept", "*/*");
        sbUrlReq.append("/");
        sbUrlReq.append(appKey);
        sbUrlReq.append("/");
        sbUrlReq.append(accessTime);
        sbUrlReq.append("?");
        sbUrlReq.append("pageNum");
        sbUrlReq.append("=");
        sbUrlReq.append(pageNum);
        sbUrlReq.append("&");
        sbUrlReq.append("pageIndex");
        sbUrlReq.append("=");
        sbUrlReq.append(pageIndex);
        JSONObject responseBody = this.process("", sbUrlReq.toString(), "POST", requestProperties);
        VersionListResponseBody versionListResponseBody = new VersionListResponseBody();
        JSONArray versionListResponseBodyVersionJA = responseBody.getJSONArray("list");
        List<com.alibaba.testsdk.packet.generic.Version> versionListResponseBodyVersionlist = new ArrayList<com.alibaba.testsdk.packet.generic.Version>();
        for (int versionListResponseBodyI0 = 0; (versionListResponseBodyI0 <versionListResponseBodyVersionJA.size()); versionListResponseBodyI0 ++) {
            JSONObject versionListResponseBodyVersionjo = JSONObject.fromObject(versionListResponseBodyVersionJA.get(versionListResponseBodyI0));
            com.alibaba.testsdk.packet.generic.Version version = new com.alibaba.testsdk.packet.generic.Version();
            String versionversionNo = versionListResponseBodyVersionjo.getString("versionNo");
            if ((!(versionversionNo == null))&&(!versionversionNo.equals(""))) {
                version.setVersionNo(versionversionNo);
            }
            Long versionid = versionListResponseBodyVersionjo.getLong("id");
            if ((!(versionid == null))&&(!versionid.equals(""))) {
                version.setId(versionid);
            }
            String versionversionInfo = versionListResponseBodyVersionjo.getString("versionInfo");
            if ((!(versionversionInfo == null))&&(!versionversionInfo.equals(""))) {
                version.setVersionInfo(versionversionInfo);
            }
            versionListResponseBodyVersionlist.add(version);
        }
        versionListResponseBody.setList(versionListResponseBodyVersionlist);
        return versionListResponseBody;
    }

}
