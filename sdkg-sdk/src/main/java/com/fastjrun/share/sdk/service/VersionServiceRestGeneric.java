
package com.fastjrun.share.sdk.service;

import com.fastjrun.share.sdk.packet.generic.VersionListResponseBody;


/**
 * 注意：本内容仅限于公司内部传阅，禁止外泄以及用于其他的商业目的
 * 
 * @Copyright 2018 快嘉框架. All rights reserved.
 * @author cuiyingfeng
 */
public interface VersionServiceRestGeneric {


    /**
     * 最近版本列表
     * 
     */
    VersionListResponseBody latests();

    /**
     * 最近版本列表
     * 
     */
    VersionListResponseBody latestsv2(String appKey, Long accessTime, Integer pageNum, Integer pageIndex);

}
