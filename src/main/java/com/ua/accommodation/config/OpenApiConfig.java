package com.ua.accommodation.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    public static final String BEARER_AUTH = "BearerAuth";
    public static final String BEARER = "bearer";
    public static final String JWT = "JWT";

    @Bean
    public OpenAPI customOpenApi() {
        return new OpenAPI()
          .components(new Components().addSecuritySchemes(BEARER_AUTH,
              new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme(BEARER)
                .bearerFormat(JWT)))
              .addSecurityItem(new SecurityRequirement().addList(BEARER_AUTH));
    }
}