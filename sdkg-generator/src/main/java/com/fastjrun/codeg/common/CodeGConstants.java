/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.codeg.common;

/**
 * @author fastjrun
 */
public interface CodeGConstants {

  String JacksonUtilsClassName = "com.fastjrun.common.utils.JacksonUtils";

  ControllerType ControllerType_EXAMPLE =
      new ControllerType(
          "Example",
          "com.fastjrun.example.client.ExampleClient",
          "",
          "com.fastjrun.example.web.controller.BaseController",
          "com.fastjrun.example.codeg.generator.ExampleServiceGenerator",
          "com.fastjrun.example.codeg.generator.ExampleHTTPGenerator");

  enum CodeGCommand {
    MpG,
    ApiG,
    BundleG,
    BundleMockG;
  }

  // 0:common;1:swagger

  enum SwaggerVersion {
    Swagger2("swagger2"),
    Swagger3("swagger3");
    public String value;

    SwaggerVersion(String value) {
      this.value = value;
    }
  }

  class ControllerType {

    public String name;

    public String baseClient;

    public String apiParentName;

    public String providerParentName;

    public String serviceGeneratorName;

    public String generatorName;

    public String baseControllerName;

    public ControllerType(
        String name,
        String baseClient,
        String apiParentName,
        String providerParentName,
        String serviceGeneratorName,
        String generatorName) {
      this(
          name,
          baseClient,
          apiParentName,
          providerParentName,
              serviceGeneratorName,
          generatorName,
          "org.springframework.web.bind.annotation.RestController");
    }

    public ControllerType(
        String name,
        String baseClient,
        String apiParentName,
        String providerParentName,
        String serviceGeneratorName,
        String generatorName,
        String baseControllerName) {
      this.name = name;
      this.baseClient = baseClient;
      this.apiParentName = apiParentName;
      this.providerParentName = providerParentName;
      this.serviceGeneratorName = serviceGeneratorName;
      this.generatorName = generatorName;
      this.baseControllerName = baseControllerName;
    }
  }
}
