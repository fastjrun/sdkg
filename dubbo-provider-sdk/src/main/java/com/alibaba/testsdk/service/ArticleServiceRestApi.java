package com.alibaba.testsdk.service;

import com.alibaba.testsdk.packet.api.ArticleListResponseBody;
import com.fastjrun.packet.BaseDefaultResponseBody;
import com.fastjrun.packet.BaseResponse;


/**
 * 注意：本内容仅限于公司内部传阅，禁止外泄以及用于其他的商业目的
 *
 * @author cuiyingfeng
 * @Copyright 2018 快嘉框架. All rights reserved.
 */
public interface ArticleServiceRestApi {


    public BaseResponse<BaseDefaultResponseBody> check();

    public BaseResponse<ArticleListResponseBody> latests();

}
