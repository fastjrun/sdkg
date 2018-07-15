package com.alibaba.testsdk.packet.generic;


/**
 * 注意：本内容仅限于公司内部传阅，禁止外泄以及用于其他的商业目的
 *
 * @author cuiyingfeng
 * @Copyright 2018 快嘉框架. All rights reserved.
 */
public class Version {

    private final static long serialVersionUID = 450711736L;
    /**
     * 版本号
     */
    @io.swagger.annotations.ApiModelProperty(value = "\u7248\u672c\u53f7", required = true)
    private String versionNo;
    /**
     * 版本Id
     */
    @io.swagger.annotations.ApiModelProperty(value = "\u7248\u672cId", required = true)
    private Long id;
    /**
     * 版本信息
     */
    @io.swagger.annotations.ApiModelProperty(value = "\u7248\u672c\u4fe1\u606f", required = true)
    private String versionInfo;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(("Version" + " ["));
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
