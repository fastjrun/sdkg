package com.fastjrun.mock;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
@SpringBootApplication(
        exclude = {DataSourceAutoConfiguration.class, ValidationAutoConfiguration.class},
        scanBasePackages = {"com.fastjrun.mock"}
)

public class ProviderMockStartApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(ProviderMockStartApplication.class)
                .bannerMode(Banner.Mode.CONSOLE)
                .run(args);
        log.info("{}", ProviderMockStartApplication.class.getResource("/"));

    }
}
