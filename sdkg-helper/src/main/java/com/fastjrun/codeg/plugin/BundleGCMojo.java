package com.fastjrun.codeg.plugin;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import com.fastjrun.codeg.common.CodeGConstants;
import com.fastjrun.codeg.service.impl.DefaultCodeGService;

@Mojo(name = "bundleGc", defaultPhase = LifecyclePhase.INITIALIZE)
public class BundleGCMojo extends CodeGMogo {

    @Parameter(property = "bdgc.skip", defaultValue = "true")
    private boolean skip;

    public void execute() throws MojoExecutionException {

        if (skip) {
            return;
        }

        getLog().info(author);

        getLog().info(bundleFiles);

        getLog().info(packagePrefix);

        getLog().info(module);

        getLog().info(String.valueOf(rpcType));

        getLog().info(String.valueOf(controllerType));

        CodeGConstants.RpcType rpcType1 = CodeGConstants.RPC_TYPE_DEFAULT;
        if (this.rpcType == 0) {
            rpcType1 = CodeGConstants.RpcType.RpcType_Dubbo;
        } else if (this.rpcType == 1) {
            rpcType1 = CodeGConstants.RpcType.RpcType_Grpc;
        } else if (this.rpcType == -1) {
            rpcType1 = null;
        }
        CodeGConstants.ControllerType controllerType1 = CodeGConstants.CONTROLLER_TYPE_DEFAULT;
        if (this.controllerType == 1) {
            controllerType1 = CodeGConstants.ControllerType.ControllerType_APP;
        } else if (this.controllerType == 2) {
            controllerType1 = CodeGConstants.ControllerType.ControllerType_API;
        } else if (this.controllerType == -1) {
            controllerType1 = null;
        }
        if (rpcType1 == null && controllerType1 == null) {
            return;
        }

        DefaultCodeGService codeGService = new DefaultCodeGService();
        codeGService.setBundleFiles(bundleFiles.split(","));
        codeGService.setPackageNamePrefix(packagePrefix);
        if (rpcType1 == null && controllerType1 == null) {
            return;
        }
        codeGService.generateProvider(module, rpcType1, controllerType1);

    }
}