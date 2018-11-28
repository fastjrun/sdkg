/*
 * Copyright (C) 2018 Fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.mock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

import com.fastjrun.BaseProviderApplication;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class}, scanBasePackages = "com.fastjrun.mock")
@EnableSwagger2
@ImportResource({"classpath:applicationContext.xml"})
public class ProviderMockStartApplication extends BaseProviderApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProviderMockStartApplication.class, args);
    }

    @Bean
    public Docket customDocket() {
        return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo()).select()
                .apis(RequestHandlerSelectors.basePackage("com.fastjrun.mock.web.controller"))
                .paths(PathSelectors.any()).build();

    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title("快嘉").description("示例模板系统").license("MIT")
                .licenseUrl("http://opensource.org/licenses/MIT")
                .contact(
                        new Contact("fastjrun", "http://github.com/fastjrun", "fastjrun@139.com"))
                .version("1.0")
                .build();
    }

}
