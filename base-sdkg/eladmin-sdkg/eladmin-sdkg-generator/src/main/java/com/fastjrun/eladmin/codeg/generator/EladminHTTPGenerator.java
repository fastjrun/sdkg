/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.eladmin.codeg.generator;

import com.fastjrun.codeg.generator.BaseHTTPGenerator;
import com.fastjrun.codeg.generator.method.BaseControllerMethodGenerator;
import com.fastjrun.codeg.generator.method.BaseHTTPMethodGenerator;
import com.fastjrun.codeg.generator.method.BaseServiceMethodGenerator;
import com.fastjrun.codeg.generator.method.DefaultHTTPMethodGenerator;
import com.fastjrun.codeg.processor.*;
import com.fastjrun.eladmin.codeg.processer.EladminRequestProcessor;
import com.fastjrun.eladmin.codeg.processer.EladminResponseProcessor;

public class EladminHTTPGenerator extends BaseHTTPGenerator {

  @Override
  public BaseControllerMethodGenerator prepareBaseControllerMethodGenerator(
          BaseServiceMethodGenerator serviceMethodGenerator) {
    BaseHTTPMethodGenerator baseHTTPMethodGenerator = new DefaultHTTPMethodGenerator();
    baseHTTPMethodGenerator.setClient(this.isClient());
    baseHTTPMethodGenerator.setPackageNamePrefix(this.packageNamePrefix);
    baseHTTPMethodGenerator.setMockModel(this.mockModel);
    baseHTTPMethodGenerator.setServiceMethodGenerator(serviceMethodGenerator);
    baseHTTPMethodGenerator.setBaseControllerGenerator(this);
    BaseRequestProcessor baseRequestProcessor = new EladminRequestProcessor();
    BaseResponseProcessor baseResponseProcessor = new EladminResponseProcessor();
    DefaultExchangeProcessor<EladminRequestProcessor, EladminResponseProcessor>
        exchangeProcessor =
            new DefaultExchangeProcessor(baseRequestProcessor, baseResponseProcessor);
    exchangeProcessor.doParse(serviceMethodGenerator, this.packageNamePrefix);
    baseHTTPMethodGenerator.setExchangeProcessor(exchangeProcessor);
    return baseHTTPMethodGenerator;
  }
}
