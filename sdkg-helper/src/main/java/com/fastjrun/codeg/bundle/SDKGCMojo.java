package com.fastjrun.codeg.bundle;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

/**
 * @goal sdkgc
 */
public class SDKGCMojo extends AbstractMojo {
	/**
	 * 
	 * @parameter property="sdkg.bundleFiles"
	 *            default-value="demo_bundle.xml,demo_bundle1.xml"
	 */

	private String bundleFiles;
	/**
	 * 
	 * @parameter property="sdkg.packagePrefix" default-value="com.fastjrun.demo."
	 */

	private String packagePrefix;
	/**
	 * 
	 * @parameter property="sdkg.sdkDir" default-value="SDK"
	 */

	private String sdkDir;
	/**
	 * 
	 * @parameter property="sdkg.author" default-value="cuiyingfeng"
	 */

	private String author;
	/**
	 * 
	 * @parameter property="sdkg.skipAuthor" default-value="false"
	 */

	private boolean skipAuthor;
	/**
	 * 
	 * @parameter property="sdkg.company" default-value="快嘉框架"
	 */

	private String company;
	/**
	 * 
	 * @parameter property="sdkg.year_codeg_time" default-value=""
	 */

	private String year_codeg_time;
	/**
	 * 
	 * @parameter property="sdkg.skipCopyright" default-value="false"
	 */

	private boolean skipCopyright;
	/**
	 * 
	 * @parameter property="sdkg.notice" default-value="注意：本内容仅限于公司内部传阅，禁止外泄以及用于其他的商业目的"
	 */

	private String notice;
	/**
	 * 
	 * @parameter property="sdkg.skipNotice" default-value="false"
	 */

	private boolean skipNotice;
	/**
	 * 
	 * @parameter property="sdkg.appName" default-value="apiWorld"
	 */

	private String appName;

	/**
	 * 
	 * @parameter property="sdkg.skip" default-value="true"
	 */

	protected boolean skip;

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
