package com.osbah.thycase.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI thyOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("THY Case API")
                        .description("You can filter endpoints from the search field on the right and send a live request with 'Try it out'.")
                        .version("v1")
                        .contact(new Contact()
                                .name("Osman Bahtiyar")
                                .email("osman.bahtiyar@outlook.com")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Local"),
                        new Server().url("/").description("Reverse proxy / Inside Docker")
                ))
                .externalDocs(new ExternalDocumentation()
                        .description("Project README")
                        .url("https://github.com/osmanbahtiyar/THY_CASE/blob/dev/README.md"));
    }

    @Bean
    public GroupedOpenApi transportationGroup() {
        return GroupedOpenApi.builder()
                .group("transportation")
                .packagesToScan("com.osbah.thycase.infra.transportation")
                .build();
    }

    @Bean
    public GroupedOpenApi locationGroup() {
        return GroupedOpenApi.builder()
                .group("location")
                .packagesToScan("com.osbah.thycase.infra.location")
                .build();
    }

    @Bean
    public GroupedOpenApi routeGroup() {
        return GroupedOpenApi.builder()
                .group("route")
                .packagesToScan("com.osbah.thycase.infra.route")
                .build();
    }

    @Bean
    public GroupedOpenApi allGroup() {
        return GroupedOpenApi.builder()
                .group("all")
                .pathsToMatch("/**")
                .build();
    }
}