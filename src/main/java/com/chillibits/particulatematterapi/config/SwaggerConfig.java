/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved
 */

package com.chillibits.particulatematterapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.ServletContextAware;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.paths.RelativePathProvider;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.servlet.ServletContext;
import java.util.Collections;

@Configuration
@EnableSwagger2
public class SwaggerConfig implements ServletContextAware {
    // Variables as objects
    private ServletContext context;

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_12)
                .host("api.pm.chillibits.com")
                .enableUrlTemplating(true)
                .pathProvider(new RelativePathProvider(context) {
                    @Override
                    public String getApplicationBasePath() {
                        return "/";
                    }
                })
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.chillibits.particulatematterapi.controller"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo());
    }

    private ApiInfo apiInfo() {
        return new ApiInfo(
                "Particulate Matter API",
                "Official Particulate Matter REST API. For more information, please visit https://github.com/chillibits/particulate-matter-api",
                "1.0.0",
                "https://chillibits.com/pmapp?p=privacy",
                new Contact("ChilliBits", "https://www.chillibits.com", "contact@chillibits.com"),
                "ODC DbCL v1.0",
                "https://opendatacommons.org/licenses/dbcl/1.0/",
                Collections.emptyList()
        );
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        context = servletContext;
    }
}