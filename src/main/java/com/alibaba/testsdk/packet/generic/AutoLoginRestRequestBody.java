
package com.alibaba.testsdk.packet.generic;

import java.io.Serializable;
import com.fastjrun.packet.BaseBody;


/**
 * 注意：本内容仅限于公司内部传阅，禁止外泄以及用于其他的商业目的
 * 
 * @Copyright 2018 快嘉框架. All rights reserved.
 * @author cuiyingfeng
 */
public class AutoLoginRestRequestBody
    extends BaseBody
    implements Serializable
{

    /**
     * 旧登录凭证
     * 
     */
    private String uuidOld;
    private final static long serialVersionUID = 450711736L;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(("AutoLoginRestRequestBody"+" ["));
        sb.append("field [");
        sb.append("uuidOld");
        sb.append("=");
        sb.append(this.uuidOld);
        sb.append("]");
        sb.append("]");
        return sb.toString();
    }

    public String getUuidOld() {
        return this.uuidOld;
    }

    public void setUuidOld(String uuidOld) {
        this.uuidOld = uuidOld;
    }

}
