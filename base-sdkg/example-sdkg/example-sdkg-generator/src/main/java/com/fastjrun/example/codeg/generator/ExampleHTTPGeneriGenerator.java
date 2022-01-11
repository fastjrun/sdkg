/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.example.codeg.generator;

import com.fastjrun.codeg.generator.BaseHTTPGenerator;
import com.fastjrun.codeg.generator.method.BaseControllerMethodGenerator;
import com.fastjrun.codeg.generator.method.BaseHTTPMethodGenerator;
import com.fastjrun.example.codeg.generator.method.ExampleHTTPMethodGenerator;
import com.fastjrun.codeg.generator.method.ServiceMethodGenerator;
import com.fastjrun.codeg.processer.*;

public class ExampleHTTPGeneriGenerator extends BaseHTTPGenerator {

  @Override
  public BaseControllerMethodGenerator prepareBaseControllerMethodGenerator(
      ServiceMethodGenerator serviceMethodGenerator) {
    BaseHTTPMethodGenerator baseHTTPMethodGenerator = new ExampleHTTPMethodGenerator();
    baseHTTPMethodGenerator.setClient(this.isClient());
    baseHTTPMethodGenerator.setPackageNamePrefix(this.packageNamePrefix);
    baseHTTPMethodGenerator.setMockModel(this.mockModel);
    baseHTTPMethodGenerator.setServiceMethodGenerator(serviceMethodGenerator);
    baseHTTPMethodGenerator.setBaseControllerGenerator(this);
    BaseRequestProcessor baseRequestProcessor = new GenericRequestProcessor();
    BaseResponseProcessor baseResponseProcessor = new DefaultResponseWithoutHeadProcessor();
    DefaultExchangeProcessor<GenericRequestProcessor, DefaultResponseWithoutHeadProcessor>
        exchangeProcessor =
            new DefaultExchangeProcessor(baseRequestProcessor, baseResponseProcessor);
    exchangeProcessor.doParse(serviceMethodGenerator, this.packageNamePrefix);
    baseHTTPMethodGenerator.setExchangeProcessor(exchangeProcessor);
    return baseHTTPMethodGenerator;
  }
}
