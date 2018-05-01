
package com.fastjrun.share.sdk.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fastjrun.sdkg.client.BaseAppClient;
import com.fastjrun.share.sdk.packet.app.Version;
import com.fastjrun.share.sdk.packet.app.VersionListResponseBody;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


/**
 * 
 * @author fastjrun
 */
public class AppVersionAppClient
    extends BaseAppClient
{


    public void check(String appKey, String appVersion, String appSource, String deviceId) {
        StringBuilder sbUrlReq = new StringBuilder(this.appUrlPre);
        sbUrlReq.append("/app/version/");
        sbUrlReq.append("check");
        sbUrlReq.append("/");
        sbUrlReq.append(appKey);
        sbUrlReq.append("/");
        sbUrlReq.append(appVersion);
        sbUrlReq.append("/");
        sbUrlReq.append(appSource);
        sbUrlReq.append("/");
        sbUrlReq.append(deviceId);
        sbUrlReq.append("/");
        long txTime = System.currentTimeMillis();
        sbUrlReq.append(txTime);
        Map<java.lang.String, java.lang.String> requestProperties = new HashMap<java.lang.String, java.lang.String>();
        requestProperties.put("Content-Type", "application/json");
        requestProperties.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36");
        requestProperties.put("Accept", "*/*");
        this.process("", sbUrlReq.toString(), "GET", requestProperties);
    }

    public VersionListResponseBody latests(String appKey, String appVersion, String appSource, String deviceId) {
        StringBuilder sbUrlReq = new StringBuilder(this.appUrlPre);
        sbUrlReq.append("/app/version/");
        sbUrlReq.append("latests");
        sbUrlReq.append("/");
        sbUrlReq.append(appKey);
        sbUrlReq.append("/");
        sbUrlReq.append(appVersion);
        sbUrlReq.append("/");
        sbUrlReq.append(appSource);
        sbUrlReq.append("/");
        sbUrlReq.append(deviceId);
        sbUrlReq.append("/");
        long txTime = System.currentTimeMillis();
        sbUrlReq.append(txTime);
        Map<java.lang.String, java.lang.String> requestProperties = new HashMap<java.lang.String, java.lang.String>();
        requestProperties.put("Content-Type", "application/json");
        requestProperties.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36");
        requestProperties.put("Accept", "*/*");
        JSONObject responseBody = this.process("", sbUrlReq.toString(), "GET", requestProperties);
        VersionListResponseBody versionListResponseBody = new VersionListResponseBody();
        JSONArray versionListResponseBodyVersionJA = responseBody.getJSONArray("list");
        List<Version> versionListResponseBodyVersionlist = new ArrayList<Version>();
        for (int versionListResponseBodyI0 = 0; (versionListResponseBodyI0 <versionListResponseBodyVersionJA.size()); versionListResponseBodyI0 ++) {
            JSONObject versionListResponseBodyVersionjo = JSONObject.fromObject(versionListResponseBodyVersionJA.get(versionListResponseBodyI0));
            Version version = new Version();
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
