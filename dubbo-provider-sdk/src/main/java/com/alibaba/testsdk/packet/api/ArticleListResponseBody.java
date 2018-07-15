package com.alibaba.testsdk.packet.api;

import com.fastjrun.packet.BaseResponseBody;

import java.io.Serializable;
import java.util.List;


/**
 * 注意：本内容仅限于公司内部传阅，禁止外泄以及用于其他的商业目的
 *
 * @author cuiyingfeng
 * @Copyright 2018 快嘉框架. All rights reserved.
 */
public class ArticleListResponseBody
        extends BaseResponseBody
        implements Serializable {

    private final static long serialVersionUID = 454033750L;
    private List<Article> list;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(("ArticleListResponseBody" + " ["));
        sb.append("list [");
        sb.append("list");
        sb.append("=");
        sb.append(this.list);
        sb.append("]");
        sb.append("]");
        return sb.toString();
    }

    public List<Article> getList() {
        return this.list;
    }

    public void setList(List<Article> list) {
        this.list = list;
    }

}
