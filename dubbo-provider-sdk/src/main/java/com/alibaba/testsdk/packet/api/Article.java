package com.alibaba.testsdk.packet.api;


/**
 * 注意：本内容仅限于公司内部传阅，禁止外泄以及用于其他的商业目的
 *
 * @author cuiyingfeng
 * @Copyright 2018 快嘉框架. All rights reserved.
 */
public class Article {

    private final static long serialVersionUID = 450711736L;
    /**
     * 文章Id
     */
    @io.swagger.annotations.ApiModelProperty(value = "\u6587\u7ae0Id", required = true)
    private Long id;
    /**
     * 文章详情地址
     */
    @io.swagger.annotations.ApiModelProperty(value = "\u6587\u7ae0\u8be6\u60c5\u5730\u5740", required = true)
    private String detailUrl;
    /**
     * 文章编号
     */
    @io.swagger.annotations.ApiModelProperty(value = "\u6587\u7ae0\u7f16\u53f7", required = true)
    private String title;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(("Article" + " ["));
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
