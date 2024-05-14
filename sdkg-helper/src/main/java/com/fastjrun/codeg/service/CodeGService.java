/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.codeg.service;

import com.fastjrun.codeg.common.CodeGConstants;

/**
 * @author fastjrun
 */
public interface CodeGService extends CodeGConstants {

    boolean generateAPI(String bundleFiles, String moduleName);

    boolean generateProvider(String bundleFiles, String moduleName, SwaggerVersion swaggerVersion);

    boolean generateProviderMock(String bundleFiles, String moduleName, SwaggerVersion swaggerVersion);

    boolean generateMybatisPlus(String sqlFile, String moduleName, SwaggerVersion swaggerVersion);
}
