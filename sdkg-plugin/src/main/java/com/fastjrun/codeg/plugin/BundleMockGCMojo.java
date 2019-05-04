/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.codeg.plugin;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

@Mojo(name = "bundleMockGc", defaultPhase = LifecyclePhase.INITIALIZE)
public class BundleMockGCMojo extends CodeGMogo {

    @Parameter(property = "bdmgc.skip", defaultValue = "true")
    private boolean skip;

    @Override
    public void execute() {

        if (skip) {
            return;
        }

        this.executeInternal(CodeGCommand.BundleMockG);

    }
}