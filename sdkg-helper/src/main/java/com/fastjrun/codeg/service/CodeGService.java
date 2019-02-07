package com.fastjrun.codeg.service;

import com.fastjrun.codeg.common.CodeGConstants;

public interface CodeGService extends CodeGConstants {

    boolean generateAPI(String moduleName);

    boolean generateClient(String moduleName);

    boolean generateProvider(String moduleName);

    boolean generateBundle(String moduleName, MockModel mockModel);

    boolean generateBase(String moduleName);

}
