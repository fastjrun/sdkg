/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.codeg.common;

public interface CodeGConstants {

  String JacksonUtilsClassName = "com.fastjrun.common.utils.JacksonUtils";

  ControllerType ControllerType_GENERIC =
      new ControllerType(
          "Generic",
          "",
          "com.fastjrun.example.client.DefaultHTTPGenericClient",
          "",
          "",
          "com.fastjrun.example.web.controller.BaseController",
          "com.fastjrun.example.codeg.generator.ExampleHTTPGeneriGenerator");

  enum CodeGCommand {
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

  class ControllerType {

    public String name;

    public String clientSuffix;

    public String baseClient;

    public String providerSuffix;

    public String apiParentName;

    public String providerParentName;

    public String generatorName;

    public String baseControllerName;

    public ControllerType(
        String name,
        String clientSuffix,
        String baseClient,
        String providerSuffix,
        String apiParentName,
        String providerParentName,
        String generatorName) {
      this(
          name,
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
        String clientSuffix,
        String baseClient,
        String providerSuffix,
        String apiParentName,
        String providerParentName,
        String generatorName,
        String baseControllerName) {
      this.name = name;
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
