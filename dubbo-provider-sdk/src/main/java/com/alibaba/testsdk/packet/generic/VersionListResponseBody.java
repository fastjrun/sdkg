package com.alibaba.testsdk.packet.generic;

import com.fastjrun.packet.BaseResponseBody;

import java.io.Serializable;
import java.util.List;


/**
 * 注意：本内容仅限于公司内部传阅，禁止外泄以及用于其他的商业目的
 *
 * @author cuiyingfeng
 * @Copyright 2018 快嘉框架. All rights reserved.
 */
public class VersionListResponseBody
        extends BaseResponseBody
        implements Serializable {

    private final static long serialVersionUID = 454033750L;
    private List<Version> list;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(("VersionListResponseBody" + " ["));
        sb.append("list [");
        sb.append("list");
        sb.append("=");
        sb.append(this.list);
        sb.append("]");
        sb.append("]");
        return sb.toString();
    }

    public List<Version> getList() {
        return this.list;
    }

    public void setList(List<Version> list) {
        this.list = list;
    }

}
