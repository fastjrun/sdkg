package com.fastjrun.mock;

import com.alibaba.testsdk.service.VersionServiceRestGeneric;
import com.fastjrun.helper.MockHelper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


/**
 * 注意：本内容仅限于公司内部传阅，禁止外泄以及用于其他的商业目的
 *
 * @author cuiyingfeng
 * @Copyright 2018 快嘉框架. All rights reserved.
 */
@Service("versionServiceRestGeneric")
public class VersionServiceRestGenericMock
        implements VersionServiceRestGeneric {


    @Override
    public com.alibaba.testsdk.packet.generic.VersionListResponseBody latests() {
        com.alibaba.testsdk.packet.generic.VersionListResponseBody versionListResponseBody = new com.alibaba.testsdk.packet.generic.VersionListResponseBody();
        List<com.alibaba.testsdk.packet.generic.Version> versionlist = new ArrayList<com.alibaba.testsdk.packet.generic.Version>();
        int iSize0 = MockHelper.geInteger(10).intValue();
        for (int i1 = 0; (i1 < iSize0); i1++) {
            com.alibaba.testsdk.packet.generic.Version version = new com.alibaba.testsdk.packet.generic.Version();
            version.setVersionNo(MockHelper.geStringWithAscii(64));
            version.setId(MockHelper.geLong(100));
            version.setVersionInfo(MockHelper.geStringWithAscii(400));
            versionlist.add(version);
        }
        versionListResponseBody.setList(versionlist);
        return versionListResponseBody;
    }

    @Override
    public com.alibaba.testsdk.packet.generic.VersionListResponseBody latestsv2(String appKey, Long accessTime, Integer pageIndex, Integer pageNum) {
        com.alibaba.testsdk.packet.generic.VersionListResponseBody versionListResponseBody = new com.alibaba.testsdk.packet.generic.VersionListResponseBody();
        List<com.alibaba.testsdk.packet.generic.Version> versionlist = new ArrayList<com.alibaba.testsdk.packet.generic.Version>();
        int iSize0 = MockHelper.geInteger(10).intValue();
        for (int i1 = 0; (i1 < iSize0); i1++) {
            com.alibaba.testsdk.packet.generic.Version version = new com.alibaba.testsdk.packet.generic.Version();
            version.setVersionNo(MockHelper.geStringWithAscii(64));
            version.setId(MockHelper.geLong(100));
            version.setVersionInfo(MockHelper.geStringWithAscii(400));
            versionlist.add(version);
        }
        versionListResponseBody.setList(versionlist);
        return versionListResponseBody;
    }

}
