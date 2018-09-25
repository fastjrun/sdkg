package com.fastjrun.codeg.common;

public interface CodeGConstants {

    MockModel MOCK_MODEL_DEFAULT = MockModel.MockModel_Common;

    ControllerType CONTROLLER_TYPE_DEFAULT = ControllerType.ControllerType_GENERIC;

    // 0:common;1:swagger
    public enum MockModel {
        MockModel_Common(0),
        MockModel_Swagger(1);
        public int value;

        MockModel(int value) {
            this.value = value;
        }
    }

    public enum ControllerProtocol {
        ControllerProtocol_HTTP("http"),
        ControllerProtocol_RPC("rpc");

        public String value;

        ControllerProtocol(String value) {
            this.value = value;
        }

    }

    public enum ControllerType {
        ControllerType_GENERIC("Generic", ControllerProtocol.ControllerProtocol_HTTP, "", "", "", "com.fastjrun.web"
                + ".controller.BaseController", "DefaultHTTPGenericGenerator"),
        ControllerType_APP("App", ControllerProtocol.ControllerProtocol_HTTP, "", "", "",
                "com.fastjrun.web.controller.BaseAppController", "DefaultHTTPAppGenerator"),
        ControllerType_API("Api", ControllerProtocol.ControllerProtocol_HTTP, "", "", "",
                "com.fastjrun.web.controller.BaseApiController", "DefaultHTTPApiGenerator"),
        ControllerType_DUBBO("Dubbo", ControllerProtocol.ControllerProtocol_RPC, "DubboClient", "DubboController", "",
                "com.fastjrun.web.controller.BaseRPCController", "DefaultRPCGenerator");

        public String name;

        public ControllerProtocol controllerProtocol;

        public String clientSuffix;

        public String providerSuffix;

        public String apiParentName;

        public String providerParentName;

        public String generatorName;

        ControllerType(String name, ControllerProtocol controllerProtocol, String clientSuffix, String providerSuffix,
                       String apiParentName, String providerParentName, String generatorName) {
            this.name = name;
            this.controllerProtocol = controllerProtocol;
            this.clientSuffix = clientSuffix;
            this.providerSuffix = providerSuffix;
            this.apiParentName = apiParentName;
            this.providerParentName = providerParentName;
            this.generatorName = generatorName;
        }

    }

}
