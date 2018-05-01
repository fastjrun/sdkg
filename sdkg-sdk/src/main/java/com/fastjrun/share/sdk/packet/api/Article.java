
package com.fastjrun.share.sdk.packet.api;



/**
 * 
 * @author fastjrun
 */
public class Article {

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
