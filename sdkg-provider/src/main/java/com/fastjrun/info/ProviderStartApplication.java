/*
 * Copyright (C) 2019 fastjrun, Inc. All Rights Reserved.
 */
package com.fastjrun.info;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

import com.fastjrun.BaseProviderApplication;

@Configuration
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@ImportResource({"classpath:applicationContext.xml"})
public class ProviderStartApplication extends BaseProviderApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProviderStartApplication.class, args);
    }
}
