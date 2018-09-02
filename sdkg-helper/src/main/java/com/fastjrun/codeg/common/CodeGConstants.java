
package com.fastjrun.codeg.common;

public interface CodeGConstants {

    MockModel MOCK_MODEL_DEFAULT = MockModel.MockModel_Common;

    ControllerType CONTROLLER_TYPE_DEFAULT = ControllerType.ControllerType_GENERIC;

    RpcType RPC_TYPE_DEFAULT = RpcType.RpcType_Dubbo;

    // 0:dubbo;1:grpc
    public enum RpcType {
        RpcType_Dubbo(0),
        RpcType_Grpc(1);
        public int value;

        RpcType(int value) {
            this.value = value;
        }
    }

    // 0:common;1:swagger
    public enum MockModel {
        MockModel_Common(0),
        MockModel_Swagger(1);
        public int value;

        MockModel(int value) {
            this.value = value;
        }
    }

    public enum ControllerType {
        ControllerType_GENERIC(0, "Generic", "", "", "com.fastjrun.web.controller.BaseController"),
        ControllerType_APP(1, "App", "", "", "com.fastjrun.web.controller.BaseAppController"),
        ControllerType_API(2, "Api", "", "", "com.fastjrun.web.controller.BaseApiController"),
        ControllerType_DUBBO(3, "Dubbo", "DubboClient", "DubboController",
                "com.fastjrun.web.controller.BaseRPCController"),
        ControllerType_GRPC(4, "Grpc", "GrpcClient", "GrpcController", "com.fastjrun.web.controller"
                + ".BaseRPCController");

        public int value;

        public String controllerType;

        public String clientSuffix;

        public String controllerSuffix;

        public String parentName;

        ControllerType(int value, String controllerType, String clientSuffix, String controllerSuffix,
                       String parentName) {
            this.value = value;
            this.controllerType = controllerType;
            this.clientSuffix = clientSuffix;
            this.controllerSuffix = controllerSuffix;
            this.parentName = parentName;
        }

    }

}
