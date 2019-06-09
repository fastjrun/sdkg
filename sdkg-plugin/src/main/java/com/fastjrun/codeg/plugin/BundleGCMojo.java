/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.codeg.plugin;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import com.fastjrun.codeg.common.CodeGConstants;

@Mojo(name = "bundleGc", defaultPhase = LifecyclePhase.INITIALIZE)
public class BundleGCMojo extends CodeGMogo {

    @Parameter(property = "bdgc.skip", defaultValue = "true")
    private boolean skip;


    @Override
    public void execute() {

        if (skip) {
            return;
        }

        this.executeInternal(CodeGConstants.CodeGCommand.BundleG);

    }
}