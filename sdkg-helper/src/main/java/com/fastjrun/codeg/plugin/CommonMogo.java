
package com.fastjrun.codeg.plugin;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Parameter;

public abstract class CommonMogo extends AbstractMojo {

    @Parameter(property = "codeg.bundleFiles", defaultValue = "demo_bundle.xml,demo_bundle1.xml")
    protected String bundleFiles;

    @Parameter(property = "codeg.packagePrefix", defaultValue = "com.fastjrun.demo.")
    protected String packagePrefix;

    @Parameter(property = "codeg.author", defaultValue = "cuiyingfeng")
    protected String author;

    @Parameter(property = "codeg.skipAuthor", defaultValue = "false")
    protected boolean skipAuthor;

    @Parameter(property = "codeg.company", defaultValue = "fastjrun")
    protected String company;

    @Parameter(property = "codeg.yearCodegTime")
    protected String yearCodegTime;

    @Parameter(property = "codeg.skipCopyright", defaultValue = "false")
    protected boolean skipCopyright;

    @Parameter(property = "codeg.notice", defaultValue = "注意：本内容仅限于公司内部传阅，禁止外泄以及用于其他的商业目的")
    protected String notice;

    @Parameter(property = "codeg.skipNotice", defaultValue = "false")
    protected boolean skipNotice;

    public CommonMogo() {
        super();
    }

}