package com.fastjrun.codeg.service;

import com.fastjrun.codeg.common.CodeGConstants;

public interface CodeGService extends CodeGConstants {

    boolean generateAPI(String moduleName, RpcType rpcType, ControllerType controllerType);

    boolean generateProvider(String moduleName, RpcType rpcType, ControllerType controllerType);

    boolean generateBundle(String moduleName, RpcType rpcType, ControllerType controllerType, MockModel mockModel);

}
