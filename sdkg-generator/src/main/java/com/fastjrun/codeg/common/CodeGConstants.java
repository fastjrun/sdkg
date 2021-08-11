/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.codeg.common;

public interface CodeGConstants {

  String JacksonUtilsClassName = "com.fastjrun.utils.JacksonUtils";

  ControllerType ControllerType_WEB =
      new ControllerType(
          "Web",
          ControllerProtocol.ControllerProtocol_HTTP,
          "",
          "com.fastjrun.client.DefaultHTTPWebClient",
          "",
          "",
          "com.fastjrun.example.web.controller.BaseWebController",
          "com.fastjrun.codeg.generator.DefaultHTTPWebGenerator",
          "org.springframework.stereotype.Controller");

  ControllerType ControllerType_GENERIC =
      new ControllerType(
          "Generic",
          ControllerProtocol.ControllerProtocol_HTTP,
          "",
          "com.fastjrun.client.DefaultHTTPGenericClient",
          "",
          "",
          "com.fastjrun.web.controller.BaseController",
          "com.fastjrun.codeg.generator.DefaultHTTPGeneriGenerator");
  ControllerType ControllerType_DUBBO =
      new ControllerType(
          "Dubbo",
          ControllerProtocol.ControllerProtocol_DUBBO,
          "DubboClient",
          "com.fastjrun.client.DefaultDubboClient",
          "DubboController",
          "",
          "com.fastjrun.example.biz.BaseDefaultApiManager",
          "com.fastjrun.codeg.generator.DefaultDubboGenerator");

  enum CodeGCommand {
    BaseG,
    ApiG,
    ClientG,
    BundleG,
    BundleMockG;
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

    public String baseControllerName;

    public ControllerType(
        String name,
        ControllerProtocol controllerProtocol,
        String clientSuffix,
        String baseClient,
        String providerSuffix,
        String apiParentName,
        String providerParentName,
        String generatorName) {
      this(
          name,
          controllerProtocol,
          clientSuffix,
          baseClient,
          providerSuffix,
          apiParentName,
          providerParentName,
          generatorName,
          "org.springframework.web.bind.annotation.RestController");
    }

    public ControllerType(
        String name,
        ControllerProtocol controllerProtocol,
        String clientSuffix,
        String baseClient,
        String providerSuffix,
        String apiParentName,
        String providerParentName,
        String generatorName,
        String baseControllerName) {
      this.name = name;
      this.controllerProtocol = controllerProtocol;
      this.clientSuffix = clientSuffix;
      this.baseClient = baseClient;
      this.providerSuffix = providerSuffix;
      this.apiParentName = apiParentName;
      this.providerParentName = providerParentName;
      this.generatorName = generatorName;
      this.baseControllerName = baseControllerName;
    }
  }
}
