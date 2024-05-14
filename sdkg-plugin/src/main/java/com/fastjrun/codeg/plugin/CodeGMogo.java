/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.codeg.plugin;

import com.fastjrun.codeg.common.CodeGConstants;
import com.fastjrun.codeg.service.impl.DefaultCodeGService;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Parameter;

public abstract class CodeGMogo extends AbstractMojo implements CodeGConstants {

    @Parameter(property = "codeg.sqlFile", defaultValue = "fast-demo.sql")
    protected String sqlFile;

    @Parameter(property = "codeg.bundleFiles", defaultValue = "demo_bundle.xml,demo_bundle1.xml")
    protected String bundleFiles;

    @Parameter(property = "codeg.packagePrefix", defaultValue = "com.fastjrun.demo.")
    protected String packagePrefix;

    @Parameter(property = "codeg.module", defaultValue = "Demo")
    protected String module;

    /**
     * 0:common;1:mack
     */
    @Parameter(property = "codeg.mock", defaultValue = "false")
    protected boolean mock;

    /**
     * swagger2;swagger3
     */
    @Parameter(property = "codeg.swaggerVersion", defaultValue = "swagger2")
    protected String swaggerVersion;

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

    protected void executeInternal(CodeGCommand codeGCommand) {

        getLog().info(author);

        getLog().info(company);

        switch (codeGCommand) {
            case BundleMockG:
                getLog().info(bundleFiles);
                break;
            case BundleG:
                getLog().info(bundleFiles);
                break;
            case ApiG:
                getLog().info(bundleFiles);
                break;
            case MpG:
                getLog().info(sqlFile);
            default:
                break;
        }

        getLog().info(packagePrefix);
        getLog().info(module);
        getLog().info(swaggerVersion);

        DefaultCodeGService codeGService = new DefaultCodeGService();
        codeGService.setPackageNamePrefix(packagePrefix);
        codeGService.setAuthor(author);
        codeGService.setCompany(company);


        SwaggerVersion swaggerVersion1 = SwaggerVersion.Swagger3;
        switch (swaggerVersion) {
            case "swagger2":
                swaggerVersion1 = SwaggerVersion.Swagger2;
                break;
            default:
                break;
        }

        switch (codeGCommand) {
            case BundleMockG:
                codeGService.generateProviderMock(bundleFiles, module, swaggerVersion1);
                break;
            case BundleG:
                codeGService.generateProvider(bundleFiles, module, swaggerVersion1);
                break;
            case ApiG:
                codeGService.generateAPI(bundleFiles, module);
                break;
            case MpG:
                codeGService.generateMybatisPlus(sqlFile, module, swaggerVersion1);
                break;
            default:
                break;
        }
    }
}
