package com.fastjrun.mock;

import com.alibaba.testsdk.packet.api.Article;
import com.alibaba.testsdk.packet.api.ArticleListResponseBody;
import com.alibaba.testsdk.service.ArticleServiceRestApi;
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
@Service("articleServiceRestApi")
public class ArticleServiceRestApiMock
        implements ArticleServiceRestApi {


    @Override
    public BaseResponse<BaseDefaultResponseBody> check() {
        BaseResponse<BaseDefaultResponseBody> response = BaseResponseHelper.getSuccessResult();
        return response;
    }

    @Override
    public BaseResponse<ArticleListResponseBody> latests() {
        BaseResponse<ArticleListResponseBody> response = new BaseResponse<ArticleListResponseBody>();
        BaseResponseHead responseHead = new BaseResponseHead();
        responseHead.setCode("0000");
        responseHead.setMsg("Mock.");
        response.setHead(responseHead);
        ArticleListResponseBody articleListResponseBody = new ArticleListResponseBody();
        List<Article> articlelist = new ArrayList<Article>();
        int iSize0 = MockHelper.geInteger(10).intValue();
        for (int i1 = 0; (i1 < iSize0); i1++) {
            Article article = new Article();
            article.setId(MockHelper.geLong(100));
            article.setDetailUrl(MockHelper.geStringWithAscii(128));
            article.setTitle(MockHelper.geStringWithAscii(64));
            articlelist.add(article);
        }
        articleListResponseBody.setList(articlelist);
        response.setBody(articleListResponseBody);
        return response;
    }

}
