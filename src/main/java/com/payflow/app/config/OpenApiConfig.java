package com.payflow.app.config;

import java.util.List;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class OpenApiConfig {

  private static final String SECURITY_SCHEME_NAME = "bearer-jwt";

  @Bean
  public OpenAPI baseOpenAPI() {
    return new OpenAPI()
        .info(new Info()
            .title("Payflow API")
            .version("v1.0.0")
            .description("Payflow Backend API")
            .contact(new Contact().name("Your Team").email("team@example.com")))
        .servers(List.of(
            new Server().url("http://localhost:8080").description("Local")
        ))
        // --- JWT Bearer security ---
        .components(new Components().addSecuritySchemes(SECURITY_SCHEME_NAME,
            new SecurityScheme()
                .name(SECURITY_SCHEME_NAME)
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")))
        .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME));
  }

  // group endpoints (adjust package/path as needed)
  @Bean
  public GroupedOpenApi publicApi() {
    return GroupedOpenApi.builder()
        .group("payflow")
        .packagesToScan("com.payflow.app.controller") // your controllers package
        .pathsToMatch("/**")
        .build();
  }
}
