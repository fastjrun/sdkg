
package com.alibaba.testsdk.service;

import com.alibaba.testsdk.packet.app.VersionListResponseBody;


/**
 * 注意：本内容仅限于公司内部传阅，禁止外泄以及用于其他的商业目的
 * 
 * @Copyright 2018 快嘉框架. All rights reserved.
 * @author cuiyingfeng
 */
public interface VersionServiceRestApp {


    /**
     * 版本检测
     * 
     */
    void check();

    /**
     * 最近版本列表
     * 
     */
    VersionListResponseBody latests();

}
