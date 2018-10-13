package com.fastjrun.codeg.plugin;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Parameter;

import com.fastjrun.codeg.common.CodeGConstants;
import com.fastjrun.codeg.service.impl.DefaultCodeGService;

public abstract class CodeGMogo extends AbstractMojo implements CodeGConstants {

    @Parameter(property = "codeg.bundleFiles", defaultValue = "demo_bundle.xml,demo_bundle1.xml")
    protected String bundleFiles;

    @Parameter(property = "codeg.packagePrefix", defaultValue = "com.fastjrun.demo.")
    protected String packagePrefix;

    @Parameter(property = "codeg.module", defaultValue = "Demo")
    protected String module;

    /**
     * 0:common;1:swagger
     */
    @Parameter(property = "codeg.mockModel", defaultValue = "swagger2")
    protected String mockModel;

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

        getLog().info(bundleFiles);

        getLog().info(packagePrefix);

        getLog().info(module);

        DefaultCodeGService codeGService = new DefaultCodeGService();
        codeGService.setBundleFiles(bundleFiles.split(","));
        codeGService.setPackageNamePrefix(packagePrefix);
        codeGService.setAuthor(author);
        codeGService.setCompany(company);

        switch (codeGCommand) {
            case BundleMockG:
                getLog().info(mockModel);
                MockModel mockModelTemp = MockModel.MockModel_Swagger;
                switch (mockModel) {
                    case "swagger2":
                        break;
                    default:
                        break;
                }
                codeGService.generateBundle(module, mockModelTemp);
                break;
            case BundleG:
                codeGService.generateProvider(module);
                break;
            case ApiG:
                codeGService.generateAPI(module);
                break;
            default:
                break;
        }
    }
}