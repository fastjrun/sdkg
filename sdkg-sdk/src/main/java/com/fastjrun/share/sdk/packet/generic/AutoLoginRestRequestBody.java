
package com.fastjrun.share.sdk.packet.generic;

import java.io.Serializable;
import com.fastjrun.sdkg.packet.BaseRequestBody;


/**
 * 
 * @author fastjrun
 */
public class AutoLoginRestRequestBody
    extends BaseRequestBody
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
