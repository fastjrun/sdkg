/*
 * Copyright (C) 2018 Fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.codeg.plugin;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

@Mojo(name = "clientGc", defaultPhase = LifecyclePhase.INITIALIZE)
public class ClientGCMojo extends CodeGMogo {

    @Parameter(property = "clientgc.skip", defaultValue = "true")
    private boolean skip;

    public void execute() {

        if (skip) {
            return;
        }

        executeInternal(CodeGCommand.ClientG);

    }
}