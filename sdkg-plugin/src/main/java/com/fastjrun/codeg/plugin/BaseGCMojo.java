/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.codeg.plugin;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

@Mojo(name = "baseGc", defaultPhase = LifecyclePhase.INITIALIZE)
public class BaseGCMojo extends CodeGMogo {

    @Parameter(property = "basegc.skip", defaultValue = "true")
    private boolean skip;

    public void execute() {

        if (skip) {
            return;
        }

        executeInternal(CodeGCommand.BaseG);

    }
}