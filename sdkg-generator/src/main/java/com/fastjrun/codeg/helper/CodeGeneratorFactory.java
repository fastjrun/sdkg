/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.codeg.helper;

import com.fastjrun.codeg.common.CodeGConstants;
import com.fastjrun.codeg.common.CodeGException;
import com.fastjrun.codeg.common.CodeGMsgContants;
import com.fastjrun.codeg.common.CommonController;
import com.fastjrun.codeg.generator.BaseServiceGenerator;
import com.fastjrun.codeg.generator.PacketGenerator;
import com.fastjrun.codeg.generator.common.BaseControllerGenerator;

public abstract class CodeGeneratorFactory implements CodeGConstants {

    private static PacketGenerator packetGenerator;

    private static PacketGenerator getPacketGeneratorInstance(String packageNamePrefix, MockModel mockModel, String
            author, String company) {
        if (packetGenerator == null) {
            packetGenerator = new PacketGenerator();
            packetGenerator.setPackageNamePrefix(packageNamePrefix);
            packetGenerator.setMockModel(mockModel);
            packetGenerator.setAuthor(author);
            packetGenerator.setCompany(company);
        }
        return packetGenerator;
    }

    private static BaseServiceGenerator getServiceGeneratorInstance(String packageNamePrefix, MockModel mockModel, String
            author, String company,
                                                                    CommonController commonController) {
        BaseServiceGenerator serviceGenerator;
        try {
            serviceGenerator =
                    (BaseServiceGenerator) Class
                            .forName(commonController.getControllerType().serviceGeneratorName)
                            .newInstance();

        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            throw new CodeGException(CodeGMsgContants.CODEG_NOT_SUPPORT,
                    "不支持这个生成器" + commonController.getControllerType().generatorName, e);
        }
        serviceGenerator.setPackageNamePrefix(packageNamePrefix);
        serviceGenerator.setMockModel(mockModel);
        serviceGenerator.setAuthor(author);
        serviceGenerator.setCompany(company);
        return serviceGenerator;
    }

    public static PacketGenerator createPacketGenerator(String packageNamePrefix,
                                                        MockModel mockModel, String
                                                                author, String company) {
        PacketGenerator packetGenerator = getPacketGeneratorInstance(packageNamePrefix, mockModel, author, company);
        try {
            PacketGenerator packetGeneratorTmp = (PacketGenerator) packetGenerator.clone();
            return packetGeneratorTmp;
        } catch (CloneNotSupportedException e) {
            throw new CodeGException(CodeGMsgContants.CODEG_NOT_SUPPORT, "不支持PacketObject这个生成器", e);
        }

    }

    public static BaseServiceGenerator createServiceGenerator(String packageNamePrefix,
                                                              MockModel mockModel, String
                                                                      author, String company,
                                                              CommonController commonController) {
        BaseServiceGenerator serviceGenerator = getServiceGeneratorInstance(packageNamePrefix, mockModel, author, company, commonController);
        try {
            BaseServiceGenerator serviceGeneratorTmp = (BaseServiceGenerator) serviceGenerator.clone();
            return serviceGeneratorTmp;
        } catch (CloneNotSupportedException e) {
            throw new CodeGException(CodeGMsgContants.CODEG_NOT_SUPPORT, "不支持ServiceGenerator这个生成器", e);
        }

    }

    public static BaseControllerGenerator createBaseControllerGenerator(String packageNamePrefix,
                                                                        MockModel mockModel, String
                                                                                author, String company,
                                                                        CommonController commonController) {
        BaseControllerGenerator baseControllerGenerator;
        try {
            baseControllerGenerator =
                    (BaseControllerGenerator) Class
                            .forName(commonController.getControllerType().generatorName)
                            .newInstance();

        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            throw new CodeGException(CodeGMsgContants.CODEG_NOT_SUPPORT,
                    "不支持这个生成器" + commonController.getControllerType().generatorName, e);
        }
        baseControllerGenerator.setCommonController(commonController);
        baseControllerGenerator.setPackageNamePrefix(packageNamePrefix);
        baseControllerGenerator.setMockModel(mockModel);
        baseControllerGenerator.setAuthor(author);
        baseControllerGenerator.setCompany(company);
        return baseControllerGenerator;
    }
}
