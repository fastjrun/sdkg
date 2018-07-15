package com.fastjrun.codeg.bundle;

import org.apache.maven.plugin.MojoExecutionException;

/**
 * @goal bundleMockgc
 */
public class BundleMockGCMojo extends CommonMogo {
    /**
     * @parameter property="bmgc.skip" default-value="true"
     */

    protected boolean skip;
    /**
     * @parameter property="bmgc.bundleMockDir" default-value="BundleMock"
     */

    private String bundleMockDir;

    public void execute() throws MojoExecutionException {

        if (skip)
            return;

        getLog().info(bundleFiles);

        getLog().info(packagePrefix);

        getLog().info(bundleMockDir);

        getLog().info(author);

        getLog().info(appName);

        BundleGenerator bundleGenerator = new BundleGenerator();
        bundleGenerator.setModuleName(bundleMockDir);
        bundleGenerator.setBundleFiles(bundleFiles.split(","));
        bundleGenerator.setPackageNamePrefix(packagePrefix);
        bundleGenerator.setAuthor(author);
        bundleGenerator.setSkipAuthor(skipAuthor);
        bundleGenerator.setCompany(company);
        bundleGenerator.setYear_codeg_time(year_codeg_time);
        bundleGenerator.setSkipCopyright(skipCopyright);
        bundleGenerator.setNotice(notice);
        bundleGenerator.setSkipNotice(skipNotice);
        bundleGenerator.setMock(true);
        bundleGenerator.generate();

    }
}
