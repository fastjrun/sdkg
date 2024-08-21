package com.fastjrun.codeg.generator;


import com.helger.jcodemodel.JDefinedClass;
import lombok.Setter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.List;

@Setter
public class MybatisPlusMapperXmlGenerator {

    static String PACKAGE_MAPPER_MAPPING_DIR = "mapper/mapping.";
    private String moduleName;
    private String packageNamePrefix;

    private List<JDefinedClass> mapperClassList;


    public void generate() {
        String mybatisMappingDir = this.moduleName+File.separator+"src/main/java/"+
                this.packageNamePrefix.replace(".", File.separator)+PACKAGE_MAPPER_MAPPING_DIR;

        new File(mybatisMappingDir).mkdirs();

        // 将DOM对象转换为XML文件
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = null;
        try {
            transformer = transformerFactory.newTransformer();
        } catch (TransformerConfigurationException e) {
            throw new RuntimeException(e);
        }

        // 创建一个DocumentType对象来定义DOCTYPE
        String publicId = "-//mybatis.org//DTD Mapper 3.0//EN";
        String systemId = "http://mybatis.org/dtd/mybatis-3-mapper.dtd";

        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, systemId);
        transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, publicId);
        for (JDefinedClass mapperClass : mapperClassList) {
            try {
                DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

                // 启用命名空间
                docFactory.setNamespaceAware(true);

                // 创建文档
                Document doc = docBuilder.newDocument();

                // 创建根节点
                Element rootElement = doc.createElement("mapper");
                rootElement.setAttribute("namespace", mapperClass.fullName());
                doc.appendChild(rootElement);

                DOMSource source = new DOMSource(doc);
                StreamResult result = new StreamResult(new File(mybatisMappingDir+File.separator+mapperClass.name()+".xml"));

                // 输出XML到文件
                transformer.transform(source, result);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
