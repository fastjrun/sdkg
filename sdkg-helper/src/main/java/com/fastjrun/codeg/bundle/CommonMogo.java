package com.fastjrun.codeg.bundle;

import org.apache.maven.plugin.AbstractMojo;

public abstract class CommonMogo extends AbstractMojo {

    /**
     * @parameter property="sdkg.bundleFiles"
     * default-value="demo_bundle.xml,demo_bundle1.xml"
     */
    protected String bundleFiles;
    /**
     * @parameter property="sdkg.packagePrefix" default-value="com.fastjrun.demo."
     */
    protected String packagePrefix;
    /**
     * @parameter property="sdkg.author" default-value="cuiyingfeng"
     */
    protected String author;
    /**
     * @parameter property="sdkg.skipAuthor" default-value="false"
     */
    protected boolean skipAuthor;
    /**
     * @parameter property="sdkg.company" default-value="快嘉框架"
     */
    protected String company;
    /**
     * @parameter property="sdkg.year_codeg_time" default-value=""
     */
    protected String year_codeg_time;
    /**
     * @parameter property="sdkg.skipCopyright" default-value="false"
     */
    protected boolean skipCopyright;
    /**
     * @parameter property="sdkg.notice" default-value="注意：本内容仅限于公司内部传阅，禁止外泄以及用于其他的商业目的"
     */
    protected String notice;
    /**
     * @parameter property="sdkg.skipNotice" default-value="false"
     */
    protected boolean skipNotice;
    /**
     * @parameter property="sdkg.appName" default-value="apiWorld"
     */
    protected String appName;

    public CommonMogo() {
        super();
    }

}