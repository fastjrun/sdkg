/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.codeg.plugin;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

@Mojo(name = "mpGc", defaultPhase = LifecyclePhase.INITIALIZE)
public class MabatisPlusGCMojo extends CodeGMogo {

    @Parameter(property = "mpgc.skip", defaultValue = "true")
    private boolean skip;

    @Override
    public void execute() {

        if (skip) {
            return;
        }

        executeInternal(CodeGCommand.MpG);

    }
}