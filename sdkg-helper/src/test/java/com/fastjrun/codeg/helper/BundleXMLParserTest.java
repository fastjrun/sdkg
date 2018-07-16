package com.fastjrun.codeg.helper;

import java.util.Map;

import org.dom4j.Element;
import org.junit.Test;

import com.fastjrun.codeg.bundle.common.CommonService;
import com.fastjrun.codeg.bundle.common.PacketObject;

public class BundleXMLParserTest {

    @Test
    public void testCheckClassNameRepeatStringArray() {
        Map<String, Element> classMap = BundleXMLParser.checkClassNameRepeat(
                new String[] {"../app-client.xml", "../generic-client.xml", "../api-client.xml"});
        for (String value : classMap.keySet()) {
            System.out.println(value + ":" + classMap.get(value).getName());
        }
    }

    @Test
    public void testCheckClassNameRepeatString() {
        Map<String, Element> classMap = BundleXMLParser.checkClassNameRepeat("../generic-client.xml");
        for (String value : classMap.keySet()) {
            System.out.println(value);
        }
    }

    @Test
    public void testProcessServiceAll() {
        String[] bundleFiles = new String[] {"../app-client.xml", "../generic-client.xml", "../api-client.xml"};
        for (String bundleFile : bundleFiles) {
            Map<String, PacketObject> restPacketMap = BundleXMLParser.processPacket(bundleFile);
            Map<String, CommonService> serviceMap = BundleXMLParser.processService(bundleFile, restPacketMap);
            for (String value : serviceMap.keySet()) {
                System.out.println(value);
            }
        }
    }

    @Test
    public void testProcessService() {
        Map<String, PacketObject> restPacketMap = BundleXMLParser.processPacket("../generic-client.xml");
        Map<String, CommonService> serviceMap = BundleXMLParser
                .processService("../generic-client.xml", restPacketMap);
        for (String value : serviceMap.keySet()) {
            System.out.println(value);
        }
    }

}
