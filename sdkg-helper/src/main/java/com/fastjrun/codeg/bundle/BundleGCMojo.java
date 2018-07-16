package com.fastjrun.codeg.bundle;

import org.apache.maven.plugin.MojoExecutionException;

/**
 * @goal bundlegc
 */
public class BundleGCMojo extends CommonMogo {
    /**
     * @parameter property="bgc.skip" default-value="true"
     */

    protected boolean skip;
    /**
     * @parameter property="bgc.bundleDir" default-value="Bundle"
     */

    private String bundleDir;

    public void execute() throws MojoExecutionException {

        if (skip) {
            return;
        }

        getLog().info(bundleFiles);

        getLog().info(packagePrefix);

        getLog().info(bundleDir);

        getLog().info(author);

        getLog().info(appName);

        BundleGenerator bundleGenerator = new BundleGenerator();
        bundleGenerator.setModuleName(bundleDir);
        bundleGenerator.setBundleFiles(bundleFiles.split(","));
        bundleGenerator.setPackageNamePrefix(packagePrefix);
        bundleGenerator.setAuthor(author);
        bundleGenerator.setSkipAuthor(skipAuthor);
        bundleGenerator.setCompany(company);
        bundleGenerator.setYear_codeg_time(year_codeg_time);
        bundleGenerator.setSkipCopyright(skipCopyright);
        bundleGenerator.setNotice(notice);
        bundleGenerator.setSkipNotice(skipNotice);
        bundleGenerator.generate();

    }
}
