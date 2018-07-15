package com.fastjrun.codeg.bundle;

import org.apache.maven.plugin.MojoExecutionException;

/**
 * @goal sdkgc
 */
public class SDKGCMojo extends CommonMogo {
    /**
     * @parameter property="sdkg.skip" default-value="true"
     */

    protected boolean skip;
    /**
     * @parameter property="sdkg.sdkDir" default-value="SDK"
     */

    private String sdkDir;

    public void execute() throws MojoExecutionException {

        if (skip)
            return;

        getLog().info(bundleFiles);

        getLog().info(packagePrefix);

        getLog().info(sdkDir);

        getLog().info(author);

        getLog().info(appName);

        SDKGenerator sdkGenerator = new SDKGenerator();
        sdkGenerator.setModuleName(sdkDir);
        sdkGenerator.setBundleFiles(bundleFiles.split(","));
        sdkGenerator.setPackageNamePrefix(packagePrefix);
        sdkGenerator.setAppName(appName);
        sdkGenerator.setAuthor(author);
        sdkGenerator.setSkipAuthor(skipAuthor);
        sdkGenerator.setCompany(company);
        sdkGenerator.setYear_codeg_time(year_codeg_time);
        sdkGenerator.setSkipCopyright(skipCopyright);
        sdkGenerator.setNotice(notice);
        sdkGenerator.setSkipNotice(skipNotice);
        sdkGenerator.generate();

    }
}