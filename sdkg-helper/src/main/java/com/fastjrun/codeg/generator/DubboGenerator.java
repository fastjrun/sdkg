package com.fastjrun.codeg.generator;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.fastjrun.codeg.common.CommonController;
import com.fastjrun.codeg.helper.StringHelper;

public class DubboGenerator extends BaseRPCGenerator {

    @Override
    protected void generateClientXml() {
        Document document = DocumentHelper.createDocument();
        Element rootNode = DocumentHelper.createElement("beans");
        rootNode.addNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
        rootNode.addNamespace("context", "http://www.springframework.org/schema/context");
        rootNode.addNamespace("dubbo", "http://code.alibabatech.com/schema/dubbo");
        rootNode.addNamespace("", "http://www.springframework.org/schema/beans");
        rootNode.addAttribute("xsi:schemaLocation",
                "http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans"
                        + ".xsd http://www.springframework.org/schema/context http://www.springframework"
                        + ".org/schema/context/spring-context.xsd http://code.alibabatech.com/schema/dubbo "
                        + "http://code.alibabatech.com/schema/dubbo/dubbo.xsd");
        document.add(rootNode);
        for (String key : this.controllerMap.keySet()) {
            CommonController commonController = this.controllerMap.get(key);
            CommonController.ControllerType controllerType = commonController.getControllerType();
            Element referenceNode = DocumentHelper.createElement("dubbo:reference");
            referenceNode.addAttribute("id", StringHelper.toLowerCaseFirstOne(commonController.getClientName()));
            referenceNode
                    .addAttribute("interface",
                            this.packageNamePrefix + this.rpcApi + "." + commonController.getClientName
                                    ());

            rootNode.add(referenceNode);

        }

        this.clientXml = document;
    }

    protected void generateServerXml() {

        Document document = DocumentHelper.createDocument();
        Element rootNode = DocumentHelper.createElement("beans");
        rootNode.addNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
        rootNode.addNamespace("context", "http://www.springframework.org/schema/context");
        rootNode.addNamespace("dubbo", "http://code.alibabatech.com/schema/dubbo");
        rootNode.addNamespace("", "http://www.springframework.org/schema/beans");
        rootNode.addAttribute("xsi:schemaLocation",
                "http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans"
                        + ".xsd http://www.springframework.org/schema/context http://www.springframework"
                        + ".org/schema/context/spring-context.xsd http://code.alibabatech.com/schema/dubbo "
                        + "http://code.alibabatech.com/schema/dubbo/dubbo.xsd");
        document.add(rootNode);
        for (String key : this.controllerMap.keySet()) {
            CommonController commonController = this.controllerMap.get(key);
            CommonController.ControllerType controllerType = commonController.getControllerType();
            Element serviceNode = DocumentHelper.createElement("dubbo:service");
            serviceNode.addAttribute("ref", StringHelper.toLowerCaseFirstOne(commonController.getClientName()));
            serviceNode
                    .addAttribute("interface",
                            this.packageNamePrefix + this.rpcApi + "." + commonController.getClientName
                                    ());
            rootNode.add(serviceNode);

        }
        Element contextComponentScan = DocumentHelper.createElement("context:component-scan");
        contextComponentScan.addAttribute("base-package", this.packageNamePrefix + this.rpcBiz);

        rootNode.add(contextComponentScan);

        this.serverXml = document;

    }

}