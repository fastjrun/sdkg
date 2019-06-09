/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.codeg.plugin;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import com.fastjrun.codeg.common.CodeGConstants;

@Mojo(name = "apiGc", defaultPhase = LifecyclePhase.INITIALIZE)
public class ApiGCMojo extends CodeGMogo {

    @Parameter(property = "apigc.skip", defaultValue = "true")
    private boolean skip;

    @Override
    public void execute() {

        if (skip) {
            return;
        }

        executeInternal(CodeGConstants.CodeGCommand.ApiG);

    }
}