package com.fastjrun.demo;

import com.alibaba.testsdk.packet.api.ArticleListResponseBody;
import com.alibaba.testsdk.service.ArticleServiceRestApi;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Consumer {
    public static void main(String[] args) throws Exception {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
                new String[]{"spring-dubbo-consumer.xml"});
        ArticleServiceRestApi demoService = (ArticleServiceRestApi) context.getBean("articleServiceRestApi");
        // execute remote invocation
        demoService.check();
        ArticleListResponseBody articleListResponseBody = demoService.latests();
        // show the result
        System.out.println("hello world");
        System.out.println("hello world");
        System.out.println("hello world");
        System.out.println("hello world");
        System.out.println("hello world");
        System.out.println(articleListResponseBody);
        context.close();
    }
}