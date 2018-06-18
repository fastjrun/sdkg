package com.fastjrun.codeg.bundle;

import org.apache.maven.plugin.MojoExecutionException;

/**
 * @goal bundleMockgc
 */
public class BundleMockGCMojo extends CommonMogo {
	/**
	 * 
	 * @parameter property="bmgc.bundleMockDir" default-value="BundleMock"
	 */

	private String bundleMockDir;
	/**
	 * 
	 * @parameter property="bmgc.skip" default-value="true"
	 */

	protected boolean skip;

	public void execute() throws MojoExecutionException {

		if (skip)
			return;

		getLog().info(bundleFiles);

		getLog().info(packagePrefix);

		getLog().info(bundleMockDir);

		getLog().info(author);

		getLog().info(appName);

		BundleMockGenerator bundleMockGenerator = new BundleMockGenerator();
		bundleMockGenerator.setModuleName(bundleMockDir);
		bundleMockGenerator.setBundleFiles(bundleFiles.split(","));
		bundleMockGenerator.setPackageNamePrefix(packagePrefix);
		bundleMockGenerator.setAuthor(author);
		bundleMockGenerator.setSkipAuthor(skipAuthor);
		bundleMockGenerator.setCompany(company);
		bundleMockGenerator.setYear_codeg_time(year_codeg_time);		
		bundleMockGenerator.setSkipCopyright(skipCopyright);
		bundleMockGenerator.setNotice(notice);
		bundleMockGenerator.setSkipNotice(skipNotice);
		bundleMockGenerator.generate();

	}
}
