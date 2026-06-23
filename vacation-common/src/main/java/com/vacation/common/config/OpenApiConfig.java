package com.vacation.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class OpenApiConfig {

    private static final String BEARER_AUTH = "bearerAuth";

    @Bean
    public OpenAPI vacationOpenApi(Environment environment) {
        String applicationName = environment.getProperty("spring.application.name", "vacation");

        return new OpenAPI()
                .info(new Info()
                        .title(toTitle(applicationName) + " API")
                        .version("v1")
                        .description("OpenAPI documentation for " + applicationName))
                .addSecurityItem(new SecurityRequirement().addList(BEARER_AUTH))
                .components(new Components()
                        .addSecuritySchemes(BEARER_AUTH, new SecurityScheme()
                                .name(BEARER_AUTH)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }

    @Bean
    @ConditionalOnProperty(prefix = "spring.application", name = "name", havingValue = "vacation-auth")
    public GroupedOpenApi authOpenApi() {
        return GroupedOpenApi.builder()
                .group("auth")
                .pathsToMatch("/mahe/vacation/auth/**")
                .build();
    }

    @Bean
    @ConditionalOnProperty(prefix = "spring.application", name = "name", havingValue = "vacation-core")
    public GroupedOpenApi coreOpenApi() {
        return GroupedOpenApi.builder()
                .group("core")
                .pathsToMatch("/mahe/vacation/core/**")
                .build();
    }

    private String toTitle(String applicationName) {
        String[] words = applicationName.replace('-', ' ').split("\\s+");
        StringBuilder title = new StringBuilder();

        for (String word : words) {
            if (word.isBlank()) {
                continue;
            }

            if (!title.isEmpty()) {
                title.append(' ');
            }

            title.append(Character.toUpperCase(word.charAt(0)));
            if (word.length() > 1) {
                title.append(word.substring(1));
            }
        }

        return title.isEmpty() ? "Vacation" : title.toString();
    }
}
