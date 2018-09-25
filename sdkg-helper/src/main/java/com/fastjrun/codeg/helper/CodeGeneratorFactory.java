package com.fastjrun.codeg.helper;

import com.fastjrun.codeg.common.CodeGConstants;
import com.fastjrun.codeg.common.CodeGException;
import com.fastjrun.codeg.common.CodeGMsgContants;
import com.fastjrun.codeg.common.CommonController;
import com.fastjrun.codeg.generator.BaseProviderGenerator;

public abstract class CodeGeneratorFactory implements CodeGConstants {
    public static BaseProviderGenerator createBaseGenerator(CommonController commonController, MockModel mockModel) {
        ControllerType controllerType = commonController.getControllerType();
        BaseProviderGenerator baseProviderGenerator;
        try {
            baseProviderGenerator = (BaseProviderGenerator) Class.forName("").newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            throw new CodeGException(CodeGMsgContants.CODEG_NOT_SUPPORT, "不支持这个生成器", e);
        }
        baseProviderGenerator.setMockModel(mockModel);
        return baseProviderGenerator;
    }
}
