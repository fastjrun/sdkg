package com.alibaba.testsdk.service;


/**
 * 注意：本内容仅限于公司内部传阅，禁止外泄以及用于其他的商业目的
 *
 * @author cuiyingfeng
 * @Copyright 2018 快嘉框架. All rights reserved.
 */
public interface VersionServiceRestGeneric {


    public com.alibaba.testsdk.packet.generic.VersionListResponseBody latests();

    public com.alibaba.testsdk.packet.generic.VersionListResponseBody latestsv2(String appKey, Long accessTime, Integer pageIndex, Integer pageNum);

}
