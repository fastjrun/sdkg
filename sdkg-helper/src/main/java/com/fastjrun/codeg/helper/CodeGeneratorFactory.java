package com.fastjrun.codeg.helper;

import java.util.HashMap;

import com.fastjrun.codeg.common.CodeGConstants;
import com.fastjrun.codeg.common.CodeGException;
import com.fastjrun.codeg.common.CodeGMsgContants;
import com.fastjrun.codeg.generator.BaseControllerGenerator;
import com.fastjrun.codeg.generator.PacketGenerator;
import com.fastjrun.codeg.generator.method.BaseControllerMethodGenerator;
import com.fastjrun.codeg.generator.method.DefaultHTTPMethodGenerator;
import com.fastjrun.codeg.generator.method.DefaultRPCMethodGenerator;
import com.fastjrun.codeg.processer.ApiRequestProcessor;
import com.fastjrun.codeg.processer.AppRequestProcessor;
import com.fastjrun.codeg.processer.DefaultExchangeProcessor;
import com.fastjrun.codeg.processer.DefaultRequestWithoutHeadProcessor;
import com.fastjrun.codeg.processer.DefaultResponseProcessor;
import com.fastjrun.codeg.processer.DefaultResponseWithoutHeadProcessor;
import com.fastjrun.codeg.processer.ExchangeProcessor;

public abstract class CodeGeneratorFactory implements CodeGConstants {

    private static final String GENERATO_RPACKAGE = "com.fastjrun.codeg.generator.";

    private static PacketGenerator packetGenerator;
    private static HashMap<String, BaseControllerGenerator> controllerGeneratorList = new HashMap<>();

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

    private static BaseControllerGenerator getBaseControllerGeneratorInstance(ControllerType controllerType, String
            packageNamePrefix,
                                                                              MockModel mockModel, String
                                                                                      author, String company) {

        BaseControllerGenerator baseControllerGenerator = controllerGeneratorList.get(controllerType.name);
        if (baseControllerGenerator == null) {
            try {
                baseControllerGenerator =
                        (BaseControllerGenerator) Class.forName(GENERATO_RPACKAGE + controllerType.generatorName)
                                .newInstance();
                BaseControllerMethodGenerator baseControllerMethodGenerator;
                ExchangeProcessor exchangeProcessor = new DefaultExchangeProcessor();
                switch (controllerType.name) {
                    case "Dubbo":
                        baseControllerMethodGenerator = new DefaultRPCMethodGenerator();
                        ((DefaultExchangeProcessor) exchangeProcessor)
                                .setRequestProcessor(new DefaultRequestWithoutHeadProcessor());
                        ((DefaultExchangeProcessor) exchangeProcessor)
                                .setResponseProcessor(new DefaultResponseProcessor());
                        break;
                    case "Api":
                        baseControllerMethodGenerator = new DefaultHTTPMethodGenerator();
                        ((DefaultExchangeProcessor) exchangeProcessor).setRequestProcessor(new ApiRequestProcessor());
                        ((DefaultExchangeProcessor) exchangeProcessor)
                                .setResponseProcessor(new DefaultResponseProcessor());
                        break;
                    case "App":
                        baseControllerMethodGenerator = new DefaultHTTPMethodGenerator();
                        ((DefaultExchangeProcessor) exchangeProcessor).setRequestProcessor(new AppRequestProcessor());
                        ((DefaultExchangeProcessor) exchangeProcessor)
                                .setResponseProcessor(new DefaultResponseProcessor());
                        break;
                    case "Generic":
                        baseControllerMethodGenerator = new DefaultHTTPMethodGenerator();
                        ((DefaultExchangeProcessor) exchangeProcessor)
                                .setRequestProcessor(new DefaultRequestWithoutHeadProcessor());
                        ((DefaultExchangeProcessor) exchangeProcessor)
                                .setResponseProcessor(new DefaultResponseWithoutHeadProcessor());
                        break;
                    default:
                        baseControllerMethodGenerator = new DefaultHTTPMethodGenerator();
                        ((DefaultExchangeProcessor) exchangeProcessor)
                                .setRequestProcessor(new DefaultRequestWithoutHeadProcessor());
                        ((DefaultExchangeProcessor) exchangeProcessor)
                                .setResponseProcessor(new DefaultResponseProcessor());
                        break;
                }
                baseControllerMethodGenerator.setExchangeProcessor(exchangeProcessor);
                baseControllerMethodGenerator.setPackageNamePrefix(packageNamePrefix);
                baseControllerMethodGenerator.setMockModel(mockModel);
                baseControllerMethodGenerator.setExchangeProcessor(exchangeProcessor);
                baseControllerGenerator.setBaseControllerMethodGenerator(baseControllerMethodGenerator);
            } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                throw new CodeGException(CodeGMsgContants.CODEG_NOT_SUPPORT, "不支持这个生成器", e);
            }
        }
        baseControllerGenerator.setPackageNamePrefix(packageNamePrefix);
        baseControllerGenerator.setMockModel(mockModel);
        baseControllerGenerator.setAuthor(author);
        baseControllerGenerator.setCompany(company);
        controllerGeneratorList.put(controllerType.name, baseControllerGenerator);
        return baseControllerGenerator;

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

    public static BaseControllerGenerator createBaseControllerGenerator(ControllerType controllerType,
                                                                        String packageNamePrefix,
                                                                        MockModel mockModel, String
                                                                                author, String company) {
        BaseControllerGenerator baseControllerGenerator =
                getBaseControllerGeneratorInstance(controllerType, packageNamePrefix, mockModel, author,
                        company);
        try {
            BaseControllerGenerator baseControllerGeneratorTmp =
                    (BaseControllerGenerator) baseControllerGenerator.clone();
            return baseControllerGeneratorTmp;
        } catch (CloneNotSupportedException e) {
            throw new CodeGException(CodeGMsgContants.CODEG_NOT_SUPPORT, "不支持packetObject这个生成器", e);
        }
    }
}
