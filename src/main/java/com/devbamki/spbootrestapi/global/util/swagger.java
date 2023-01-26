package com.devbamki.spbootrestapi.global.util;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.servlet.ServletContext;
import java.util.HashSet;
import java.util.Set;

@Configuration
@EnableSwagger2
public class swagger {
    @Bean
    public Docket SwaggerApi(ServletContext servletContext){
        return new Docket(DocumentationType.SWAGGER_2)
                .consumes(getConsumeContentTypes())
                .produces(getProduceContentTypes())
                .apiInfo(swaggerInfo())
                .groupName("Test")
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.devbamki.spbootrestapi.web.controller"))
                .paths(PathSelectors.ant("/**"))
                .build()
                .useDefaultResponseMessages(false);
    }

    private Set<String> getConsumeContentTypes() {
        Set<String> consumes = new HashSet<>();
        consumes.add("application/json;charset=UTF-8");
        consumes.add("application/x-www-form-urlencoded");
        return consumes;
    }

    private Set<String> getProduceContentTypes() {
        Set<String> produces = new HashSet<>();
        produces.add("application/json;charset=UTF-8");
        return produces;
    }

    private ApiInfo swaggerInfo() {
        return new ApiInfoBuilder().title("Test API Documentation")
                .description("Swagger UI")
                .license("devbamki")
                .licenseUrl("https://ninefive.com")
                .version("1.0")
                .build();
    }
}
