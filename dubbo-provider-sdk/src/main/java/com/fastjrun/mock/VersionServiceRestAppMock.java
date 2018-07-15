package com.fastjrun.mock;

import com.alibaba.testsdk.packet.app.Version;
import com.alibaba.testsdk.packet.app.VersionListResponseBody;
import com.alibaba.testsdk.service.VersionServiceRestApp;
import com.fastjrun.helper.BaseResponseHelper;
import com.fastjrun.helper.MockHelper;
import com.fastjrun.packet.BaseDefaultResponseBody;
import com.fastjrun.packet.BaseResponse;
import com.fastjrun.packet.BaseResponseHead;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


/**
 * 注意：本内容仅限于公司内部传阅，禁止外泄以及用于其他的商业目的
 *
 * @author cuiyingfeng
 * @Copyright 2018 快嘉框架. All rights reserved.
 */
@Service("versionServiceRestApp")
public class VersionServiceRestAppMock
        implements VersionServiceRestApp {


    @Override
    public BaseResponse<BaseDefaultResponseBody> check() {
        BaseResponse<BaseDefaultResponseBody> response = BaseResponseHelper.getSuccessResult();
        return response;
    }

    @Override
    public BaseResponse<VersionListResponseBody> latests() {
        BaseResponse<VersionListResponseBody> response = new BaseResponse<VersionListResponseBody>();
        BaseResponseHead responseHead = new BaseResponseHead();
        responseHead.setCode("0000");
        responseHead.setMsg("Mock.");
        response.setHead(responseHead);
        VersionListResponseBody versionListResponseBody = new VersionListResponseBody();
        List<Version> versionlist = new ArrayList<Version>();
        int iSize0 = MockHelper.geInteger(10).intValue();
        for (int i1 = 0; (i1 < iSize0); i1++) {
            Version version = new Version();
            version.setVersionNo(MockHelper.geStringWithAscii(64));
            version.setId(MockHelper.geLong(100));
            version.setVersionInfo(MockHelper.geStringWithAscii(400));
            versionlist.add(version);
        }
        versionListResponseBody.setList(versionlist);
        response.setBody(versionListResponseBody);
        return response;
    }

}
