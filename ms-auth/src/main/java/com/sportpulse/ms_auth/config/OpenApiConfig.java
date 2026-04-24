package com.sportpulse.ms_auth.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("SportPulse Auth API")
                        .version("1.0.0")
                        .description("Microservicio de autenticación para SportPulse. " +
                                "Maneja registro de usuarios, login y validación de tokens JWT.")
                        .contact(new Contact()
                                .name("Equipo SportPulse")
                                .email("")));
    }
}