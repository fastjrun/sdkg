package com.fastjrun.codeg.helper;

import com.fastjrun.codeg.common.CodeGConstants;
import com.fastjrun.codeg.common.CodeGException;
import com.fastjrun.codeg.common.CodeGMsgContants;
import com.fastjrun.codeg.common.CommonController;
import com.fastjrun.codeg.generator.BaseControllerGenerator;
import com.fastjrun.codeg.generator.PacketGenerator;
import com.fastjrun.codeg.generator.ServiceGenerator;

public abstract class CodeGeneratorFactory implements CodeGConstants {

    private static final String GENERATO_RPACKAGE = "com.fastjrun.codeg.generator.";

    private static PacketGenerator packetGenerator;
    private static ServiceGenerator serviceGenerator;

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

    private static ServiceGenerator getServiceGeneratorInstance(String packageNamePrefix, MockModel mockModel, String
            author, String company) {
        if (serviceGenerator == null) {
            serviceGenerator = new ServiceGenerator();
            serviceGenerator.setPackageNamePrefix(packageNamePrefix);
            serviceGenerator.setMockModel(mockModel);
            serviceGenerator.setAuthor(author);
            serviceGenerator.setCompany(company);

        }
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
            throw new CodeGException(CodeGMsgContants.CODEG_NOT_SUPPORT, "不支持packetObject这个生成器", e);
        }

    }

    public static ServiceGenerator createServiceGenerator(String packageNamePrefix,
                                                          MockModel mockModel, String
                                                                  author, String company) {
        ServiceGenerator serviceGenerator = getServiceGeneratorInstance(packageNamePrefix, mockModel, author, company);
        try {
            ServiceGenerator serviceGeneratorTmp = (ServiceGenerator) serviceGenerator.clone();
            return serviceGeneratorTmp;
        } catch (CloneNotSupportedException e) {
            throw new CodeGException(CodeGMsgContants.CODEG_NOT_SUPPORT, "不支持ServiceGenerator这个生成器", e);
        }

    }

    public static BaseControllerGenerator createBaseControllerGenerator(String packageNamePrefix,
                                                                        MockModel mockModel, String
                                                                                author, String company,
                                                                        CommonController commonController) {
        BaseControllerGenerator baseControllerGenerator = null;
        try {
            baseControllerGenerator =
                    (BaseControllerGenerator) Class
                            .forName(GENERATO_RPACKAGE + commonController.getControllerType().generatorName)
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
