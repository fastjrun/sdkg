
package com.fastjrun.share.sdk.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
{


    public com.fastjrun.share.sdk.packet.generic.VersionListResponseBody latests() {
        StringBuilder sbUrlReq = new StringBuilder(this.genericUrlPre);
        sbUrlReq.append("/generic/version/");
        sbUrlReq.append("latests");
        Map<java.lang.String, java.lang.String> requestProperties = new HashMap<java.lang.String, java.lang.String>();
        requestProperties.put("Content-Type", "application/json");
        requestProperties.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36");
        requestProperties.put("Accept", "*/*");
        JSONObject response = this.process("", sbUrlReq.toString(), "POST", requestProperties);
        com.fastjrun.share.sdk.packet.generic.VersionListResponseBody versionListResponseBody = new com.fastjrun.share.sdk.packet.generic.VersionListResponseBody();
        JSONArray versionListResponseBodyVersionJA = response.getJSONArray("list");
        List<com.fastjrun.share.sdk.packet.generic.Version> versionListResponseBodyVersionlist = new ArrayList<com.fastjrun.share.sdk.packet.generic.Version>();
        for (int versionListResponseBodyI0 = 0; (versionListResponseBodyI0 <versionListResponseBodyVersionJA.size()); versionListResponseBodyI0 ++) {
            JSONObject versionListResponseBodyVersionjo = JSONObject.fromObject(versionListResponseBodyVersionJA.get(versionListResponseBodyI0));
            com.fastjrun.share.sdk.packet.generic.Version version = new com.fastjrun.share.sdk.packet.generic.Version();
            String versionversionNo = versionListResponseBodyVersionjo.getString("versionNo");
            if ((!(versionversionNo == null))&&(!versionversionNo.equals(""))) {
                version.setVersionNo(versionversionNo);
            }
            Long versionid = Long.valueOf(versionListResponseBodyVersionjo.getLong("id"));
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

    public com.fastjrun.share.sdk.packet.generic.VersionListResponseBody latestsv2(String appKey, Long accessTime, Integer pageIndex, Integer pageNum) {
        StringBuilder sbUrlReq = new StringBuilder(this.genericUrlPre);
        sbUrlReq.append("/generic/version/");
        sbUrlReq.append("latests/v2");
        sbUrlReq.append("/");
        sbUrlReq.append(appKey);
        sbUrlReq.append("/");
        sbUrlReq.append(accessTime);
        sbUrlReq.append("?");
        sbUrlReq.append("pageIndex");
        sbUrlReq.append("=");
        sbUrlReq.append(pageIndex);
        sbUrlReq.append("&");
        sbUrlReq.append("pageNum");
        sbUrlReq.append("=");
        sbUrlReq.append(pageNum);
        Map<java.lang.String, java.lang.String> requestProperties = new HashMap<java.lang.String, java.lang.String>();
        requestProperties.put("Content-Type", "application/json");
        requestProperties.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36");
        requestProperties.put("Accept", "*/*");
        JSONObject response = this.process("", sbUrlReq.toString(), "POST", requestProperties);
        com.fastjrun.share.sdk.packet.generic.VersionListResponseBody versionListResponseBody = new com.fastjrun.share.sdk.packet.generic.VersionListResponseBody();
        JSONArray versionListResponseBodyVersionJA = response.getJSONArray("list");
        List<com.fastjrun.share.sdk.packet.generic.Version> versionListResponseBodyVersionlist = new ArrayList<com.fastjrun.share.sdk.packet.generic.Version>();
        for (int versionListResponseBodyI0 = 0; (versionListResponseBodyI0 <versionListResponseBodyVersionJA.size()); versionListResponseBodyI0 ++) {
            JSONObject versionListResponseBodyVersionjo = JSONObject.fromObject(versionListResponseBodyVersionJA.get(versionListResponseBodyI0));
            com.fastjrun.share.sdk.packet.generic.Version version = new com.fastjrun.share.sdk.packet.generic.Version();
            String versionversionNo = versionListResponseBodyVersionjo.getString("versionNo");
            if ((!(versionversionNo == null))&&(!versionversionNo.equals(""))) {
                version.setVersionNo(versionversionNo);
            }
            Long versionid = Long.valueOf(versionListResponseBodyVersionjo.getLong("id"));
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
