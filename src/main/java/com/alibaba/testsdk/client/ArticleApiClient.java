
package com.alibaba.testsdk.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.alibaba.testsdk.packet.api.Article;
import com.alibaba.testsdk.packet.api.ArticleListResponseBody;
import com.alibaba.testsdk.service.ArticleServiceRestApi;
import com.fastjrun.client.BaseApiClient;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


/**
 * 注意：本内容仅限于公司内部传阅，禁止外泄以及用于其他的商业目的
 * 
 * @Copyright 2018 快嘉框架. All rights reserved.
 * @author cuiyingfeng
 */
public class ArticleApiClient
    extends BaseApiClient
    implements ArticleServiceRestApi
{


    /**
     * 是否有新文章检测
     * 
     */
    public void check() {
        StringBuilder sbUrlReq = new StringBuilder(this.apiUrlPre);
        sbUrlReq.append("/api/article/");
        sbUrlReq.append("check");
        sbUrlReq.append(this.generateUrlSuffix());
        Map<String, String> requestProperties = new HashMap<String, String>();
        requestProperties.put("Content-Type", "application/json;charset=UTF-8");
        requestProperties.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36");
        requestProperties.put("Accept", "*/*");
        this.process("", sbUrlReq.toString(), "GET", requestProperties);
    }

    /**
     * 最近版本列表
     * 
     */
    public ArticleListResponseBody latests() {
        StringBuilder sbUrlReq = new StringBuilder(this.apiUrlPre);
        sbUrlReq.append("/api/article/");
        sbUrlReq.append("latests");
        sbUrlReq.append(this.generateUrlSuffix());
        Map<String, String> requestProperties = new HashMap<String, String>();
        requestProperties.put("Content-Type", "application/json;charset=UTF-8");
        requestProperties.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36");
        requestProperties.put("Accept", "*/*");
        JSONObject responseBody = this.process("", sbUrlReq.toString(), "POST", requestProperties);
        ArticleListResponseBody articleListResponseBody = new ArticleListResponseBody();
        JSONArray articleListResponseBodyArticleJA = responseBody.getJSONArray("list");
        List<Article> articleListResponseBodyArticlelist = new ArrayList<Article>();
        for (int articleListResponseBodyI0 = 0; (articleListResponseBodyI0 <articleListResponseBodyArticleJA.size()); articleListResponseBodyI0 ++) {
            JSONObject articleListResponseBodyArticlejo = JSONObject.fromObject(articleListResponseBodyArticleJA.get(articleListResponseBodyI0));
            Article article = new Article();
            Long articleid = articleListResponseBodyArticlejo.getLong("id");
            if ((!(articleid == null))&&(!articleid.equals(""))) {
                article.setId(articleid);
            }
            String articledetailUrl = articleListResponseBodyArticlejo.getString("detailUrl");
            if ((!(articledetailUrl == null))&&(!articledetailUrl.equals(""))) {
                article.setDetailUrl(articledetailUrl);
            }
            String articletitle = articleListResponseBodyArticlejo.getString("title");
            if ((!(articletitle == null))&&(!articletitle.equals(""))) {
                article.setTitle(articletitle);
            }
            articleListResponseBodyArticlelist.add(article);
        }
        articleListResponseBody.setList(articleListResponseBodyArticlelist);
        return articleListResponseBody;
    }

}
