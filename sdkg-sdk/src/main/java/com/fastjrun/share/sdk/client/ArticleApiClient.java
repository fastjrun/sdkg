
package com.fastjrun.share.sdk.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fastjrun.sdkg.client.BaseApiClient;
import com.fastjrun.sdkg.helper.EncryptHelper;
import com.fastjrun.share.sdk.packet.api.Article;
import com.fastjrun.share.sdk.packet.api.ArticleListResponseBody;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


/**
 * 
 * @author fastjrun
 */
public class ArticleApiClient
    extends BaseApiClient
{


    public void check(String accessKey) {
        StringBuilder sbUrlReq = new StringBuilder(this.apiUrlPre);
        sbUrlReq.append("/api/article/");
        sbUrlReq.append("check");
        sbUrlReq.append("/");
        sbUrlReq.append(accessKey);
        sbUrlReq.append("/");
        long txTime = System.currentTimeMillis();
        sbUrlReq.append(txTime);
        sbUrlReq.append("/");
        try {
            String md5Hash = EncryptHelper.md5Digest((this.getAccessKeySn()+ txTime));
            sbUrlReq.append(md5Hash);
        } catch (Exception e) {
            this.log.warn("", e);
        }
        Map<java.lang.String, java.lang.String> requestProperties = new HashMap<java.lang.String, java.lang.String>();
        requestProperties.put("Content-Type", "application/json");
        requestProperties.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36");
        requestProperties.put("Accept", "*/*");
        this.process("", sbUrlReq.toString(), "GET", requestProperties);
    }

    public ArticleListResponseBody latests(String accessKey) {
        StringBuilder sbUrlReq = new StringBuilder(this.apiUrlPre);
        sbUrlReq.append("/api/article/");
        sbUrlReq.append("latests");
        sbUrlReq.append("/");
        sbUrlReq.append(accessKey);
        sbUrlReq.append("/");
        long txTime = System.currentTimeMillis();
        sbUrlReq.append(txTime);
        sbUrlReq.append("/");
        try {
            String md5Hash = EncryptHelper.md5Digest((this.getAccessKeySn()+ txTime));
            sbUrlReq.append(md5Hash);
        } catch (Exception e) {
            this.log.warn("", e);
        }
        Map<java.lang.String, java.lang.String> requestProperties = new HashMap<java.lang.String, java.lang.String>();
        requestProperties.put("Content-Type", "application/json");
        requestProperties.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36");
        requestProperties.put("Accept", "*/*");
        JSONObject responseBody = this.process("", sbUrlReq.toString(), "POST", requestProperties);
        ArticleListResponseBody articleListResponseBody = new ArticleListResponseBody();
        JSONArray articleListResponseBodyArticleJA = responseBody.getJSONArray("list");
        List<Article> articleListResponseBodyArticlelist = new ArrayList<Article>();
        for (int articleListResponseBodyI0 = 0; (articleListResponseBodyI0 <articleListResponseBodyArticleJA.size()); articleListResponseBodyI0 ++) {
            JSONObject articleListResponseBodyArticlejo = JSONObject.fromObject(articleListResponseBodyArticleJA.get(articleListResponseBodyI0));
            Article article = new Article();
            Long articleid = Long.valueOf(articleListResponseBodyArticlejo.getLong("id"));
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
