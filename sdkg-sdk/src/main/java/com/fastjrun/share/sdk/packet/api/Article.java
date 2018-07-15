
package com.fastjrun.share.sdk.packet.api;

import java.io.Serializable;
import com.fastjrun.packet.BaseBody;


/**
 * 注意：本内容仅限于公司内部传阅，禁止外泄以及用于其他的商业目的
 * 
 * @Copyright 2018 快嘉框架. All rights reserved.
 * @author cuiyingfeng
 */
public class Article
    extends BaseBody
    implements Serializable
{

    /**
     * 文章Id
     * 
     */
    private Long id;
    /**
     * 文章详情地址
     * 
     */
    private String detailUrl;
    /**
     * 文章编号
     * 
     */
    private String title;
    private final static long serialVersionUID = 450711736L;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(("Article"+" ["));
        sb.append("field [");
        sb.append("id");
        sb.append("=");
        sb.append(this.id);
        sb.append(",");
        sb.append("detailUrl");
        sb.append("=");
        sb.append(this.detailUrl);
        sb.append(",");
        sb.append("title");
        sb.append("=");
        sb.append(this.title);
        sb.append("]");
        sb.append("]");
        return sb.toString();
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDetailUrl() {
        return this.detailUrl;
    }

    public void setDetailUrl(String detailUrl) {
        this.detailUrl = detailUrl;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
