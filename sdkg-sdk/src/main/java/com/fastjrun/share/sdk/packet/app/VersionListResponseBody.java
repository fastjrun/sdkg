
package com.fastjrun.share.sdk.packet.app;

import java.io.Serializable;
import java.util.List;
import com.fastjrun.packet.BaseResponseBody;


/**
 * 注意：本内容仅限于公司内部传阅，禁止外泄以及用于其他的商业目的
 * 
 * @Copyright 2018 快嘉框架. All rights reserved.
 * @author cuiyingfeng
 */
public class VersionListResponseBody
    extends BaseResponseBody
    implements Serializable
{

    private List<Version> list;
    private final static long serialVersionUID = 454033750L;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(("VersionListResponseBody"+" ["));
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
