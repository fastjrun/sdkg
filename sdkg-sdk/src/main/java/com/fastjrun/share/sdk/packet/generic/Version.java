
package com.fastjrun.share.sdk.packet.generic;



/**
 * 
 * @author fastjrun
 */
public class Version {

    /**
     * 版本号
     * 
     */
    private String versionNo;
    /**
     * 版本Id
     * 
     */
    private Long id;
    /**
     * 版本信息
     * 
     */
    private String versionInfo;
    private final static long serialVersionUID = 450711736L;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(("Version"+" ["));
        sb.append("field [");
        sb.append("versionNo");
        sb.append("=");
        sb.append(this.versionNo);
        sb.append(",");
        sb.append("id");
        sb.append("=");
        sb.append(this.id);
        sb.append(",");
        sb.append("versionInfo");
        sb.append("=");
        sb.append(this.versionInfo);
        sb.append("]");
        sb.append("]");
        return sb.toString();
    }

    public String getVersionNo() {
        return this.versionNo;
    }

    public void setVersionNo(String versionNo) {
        this.versionNo = versionNo;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVersionInfo() {
        return this.versionInfo;
    }

    public void setVersionInfo(String versionInfo) {
        this.versionInfo = versionInfo;
    }

}
