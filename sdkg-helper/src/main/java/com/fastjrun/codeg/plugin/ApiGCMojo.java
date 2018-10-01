package com.fastjrun.codeg.plugin;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

@Mojo(name = "apiGc", defaultPhase = LifecyclePhase.INITIALIZE)
public class ApiGCMojo extends CodeGMogo {

    @Parameter(property = "apigc.skip", defaultValue = "true")
    private boolean skip;

    public void execute() {

        if (skip) {
            return;
        }

        executeInternal(CodeGCommand.ApiG);

    }
}