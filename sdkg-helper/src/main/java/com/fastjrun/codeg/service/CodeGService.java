package com.fastjrun.codeg.service;

import com.fastjrun.codeg.common.CodeGConstants;

public interface CodeGService extends CodeGConstants {

    boolean generateAPI(String bundleFiles, String moduleName);

    boolean generateClient(String bundleFiles, String moduleName);

    boolean generateProvider(String bundleFiles, String moduleName);

    boolean generateBundle(String bundleFiles, String moduleName, MockModel mockModel);

    boolean generateBase(String sqlFile, boolean supportController, boolean supportTest, String moduleName);

}
