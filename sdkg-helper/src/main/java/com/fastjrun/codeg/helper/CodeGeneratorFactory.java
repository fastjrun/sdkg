
package com.fastjrun.codeg.helper;

import com.fastjrun.codeg.common.CodeGConstants;
import com.fastjrun.codeg.common.CommonController;
import com.fastjrun.codeg.generator.ApiControllerGenerator;
import com.fastjrun.codeg.generator.AppControllerGenerator;
import com.fastjrun.codeg.generator.BaseHTTPGenerator;
import com.fastjrun.codeg.generator.BaseRPCGenerator;
import com.fastjrun.codeg.generator.DubboGenerator;
import com.fastjrun.codeg.generator.GenericControllerGenerator;

public abstract class CodeGeneratorFactory {
    public static BaseRPCGenerator createRPCGenerator(CodeGConstants.RpcType rpcType) {
        BaseRPCGenerator generator = null;
        if (rpcType == CodeGConstants.RpcType.RpcType_Dubbo) {
            generator = new DubboGenerator();
        }
        return generator;
    }

    public static BaseHTTPGenerator createHTTPGenerator(CommonController.ControllerType controllerType) {
        BaseHTTPGenerator generator = null;
        if (controllerType == CommonController.ControllerType.ControllerType_APP) {
            generator = new AppControllerGenerator();
        } else if (controllerType == CommonController.ControllerType.ControllerType_API) {
            generator = new ApiControllerGenerator();
        }
        if (controllerType == CommonController.ControllerType.ControllerType_GENERIC) {
            generator = new GenericControllerGenerator();
        }
        return generator;
    }
}
