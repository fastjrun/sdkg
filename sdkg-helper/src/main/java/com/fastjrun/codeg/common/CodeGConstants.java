package com.fastjrun.codeg.common;

public interface CodeGConstants {
    ControllerType ControllerType_GENERIC =
            new ControllerType("Generic", ControllerProtocol.ControllerProtocol_HTTP, "",
                    "com.fastjrun.client.DefaultHttpGenericClient", "",
                    "",
                    "com"
                            + ".fastjrun.web"
                            + ".controller.BaseController", "DefaultHTTPGenerator",
                    "DefaultHTTPApiMethodGenerator");
    ControllerType ControllerType_APP =
            new ControllerType("App", ControllerProtocol.ControllerProtocol_HTTP, "",
                    "com.fastjrun.client.DefaultHttpAppClient", "", "",
                    "com.fastjrun.web.controller.BaseAppController", "DefaultHTTPGenerator",
                    "DefaultHTTPAppMethodGenerator");
    ControllerType ControllerType_API =
            new ControllerType("Api", ControllerProtocol.ControllerProtocol_HTTP, "",
                    "com.fastjrun.client.DefaultHttpApiClient", "", "",
                    "com.fastjrun.web.controller.BaseApiController", "DefaultHTTPGenerator",
                    "DefaultHTTPGenericMethodGenerator");
    ControllerType ControllerType_DUBBO =
            new ControllerType("Dubbo", ControllerProtocol.ControllerProtocol_RPC, "DubboClient",
                    "com.fastjrun.client.DefaultDubboClient", "DubboController", "",
                    "com.fastjrun.web.controller.BaseRPCController", "DefaultRPCGenerator",
                    "DefaultRPCMethodGenerator");

    enum CodeGCommand {
        ApiG, BundleG, BundleMockG;
    }

    // 0:common;1:swagger
    enum MockModel {
        MockModel_Common(0),
        MockModel_Swagger(1);
        public int value;

        MockModel(int value) {
            this.value = value;
        }
    }

    enum ControllerProtocol {
        ControllerProtocol_HTTP("http"),
        ControllerProtocol_RPC("rpc");

        public String value;

        ControllerProtocol(String value) {
            this.value = value;
        }

    }

    class ControllerType {

        public String name;

        public ControllerProtocol controllerProtocol;

        public String clientSuffix;

        public String baseClient;

        public String providerSuffix;

        public String apiParentName;

        public String providerParentName;

        public String generatorName;

        public String methodGeneratorName;

        public ControllerType(String name, ControllerProtocol controllerProtocol, String clientSuffix, String
                baseClient, String providerSuffix,
                              String apiParentName, String providerParentName, String generatorName,
                              String methodGeneratorName) {
            this.name = name;
            this.controllerProtocol = controllerProtocol;
            this.clientSuffix = clientSuffix;
            this.baseClient = baseClient;
            this.providerSuffix = providerSuffix;
            this.apiParentName = apiParentName;
            this.providerParentName = providerParentName;
            this.generatorName = generatorName;
            this.methodGeneratorName = methodGeneratorName;
        }

    }

}
