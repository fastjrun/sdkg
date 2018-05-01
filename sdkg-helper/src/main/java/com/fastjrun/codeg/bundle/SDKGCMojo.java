package com.fastjrun.codeg.bundle;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

/**
 * @goal sdkgc
 */
public class SDKGCMojo extends AbstractMojo {
    /**
     * 
     * @parameter property="sdkg.bundleFiles" default-value="demo_bundle.xml,demo_bundle1.xml"
     */

    private String bundleFiles;
    /**
     * 
     * @parameter property="sdkg.packagePrefix"
     *            default-value="com.fastjrun.demo."
     */

    private String packagePrefix;
    /**
     * 
     * @parameter property="sdkg.sdkDir" default-value="SDK"
     */

    private String sdkDir;
    /**
     * 
     * @parameter property="sdkg.appName"
     *            default-value="apiWorld"
     */

    private String appName;
    
    /**
     * 
     * @parameter property="sdkg.skip" default-value="true"
     */

    protected boolean skip;

    public void execute() throws MojoExecutionException {

        getLog().info(bundleFiles);

        getLog().info(packagePrefix);

        getLog().info(sdkDir);

        getLog().info(appName);
        
        if (skip)
            return;

        SDKGenerator sdkGenerator = new SDKGenerator();
        sdkGenerator.setModuleName(sdkDir);
        sdkGenerator.setBundleFiles(bundleFiles.split(","));
        sdkGenerator.setPackageNamePrefix(packagePrefix);
        sdkGenerator.setAppName(appName);
        sdkGenerator.generate();

    }
}
