package com.fastjrun.codeg.common;

public interface CodeGConstants {
    ControllerType ControllerType_GENERIC =
            new ControllerType("Generic", ControllerProtocol.ControllerProtocol_HTTP, "",
                    "com.fastjrun.client.DefaultHTTPGenericClient", "",
                    "",
                    "com.fastjrun.web.controller.BaseController", "DefaultHTTPGeneriGenerator");
    ControllerType ControllerType_APP =
            new ControllerType("App", ControllerProtocol.ControllerProtocol_HTTP, "",
                    "com.fastjrun.client.DefaultHTTPAppClient", "", "",
                    "com.fastjrun.web.controller.BaseAppController", "DefaultHTTPAPPGenerator");
    ControllerType ControllerType_API =
            new ControllerType("Api", ControllerProtocol.ControllerProtocol_HTTP, "",
                    "com.fastjrun.client.DefaultHTTPApiClient", "", "",
                    "com.fastjrun.web.controller.BaseApiController", "DefaultHTTPAPIGenerator");
    ControllerType ControllerType_DUBBO =
            new ControllerType("Dubbo", ControllerProtocol.ControllerProtocol_DUBBO, "DubboClient",
                    "com.fastjrun.client.DefaultDubboClient", "DubboController", "",
                    "com.fastjrun.web.controller.BaseRPCController", "DefaultDubboGenerator");

    enum CodeGCommand {
        BaseG, ApiG, ClientG, BundleG, BundleMockG;
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
        ControllerProtocol_DUBBO("dubbo");

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

        public ControllerType(String name, ControllerProtocol controllerProtocol, String clientSuffix, String
                baseClient, String providerSuffix,
                              String apiParentName, String providerParentName, String generatorName) {
            this.name = name;
            this.controllerProtocol = controllerProtocol;
            this.clientSuffix = clientSuffix;
            this.baseClient = baseClient;
            this.providerSuffix = providerSuffix;
            this.apiParentName = apiParentName;
            this.providerParentName = providerParentName;
            this.generatorName = generatorName;
        }

    }

}
