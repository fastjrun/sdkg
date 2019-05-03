/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.codeg.service;

import com.fastjrun.codeg.common.CodeGConstants;

public interface CodeGService extends CodeGConstants {

    boolean generateAPI(String bundleFiles, String moduleName);

    boolean generateClient(String bundleFiles, String moduleName);

    boolean generateProvider(String bundleFiles, String moduleName, boolean supportServiceTest);

    boolean generateProviderMock(String bundleFiles, String moduleName, MockModel mockModel);

    boolean generateBase(String sqlFile, String moduleName, boolean supportTest,
      boolean supportController);

}
