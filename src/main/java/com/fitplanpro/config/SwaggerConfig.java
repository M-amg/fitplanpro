package com.fitplanpro.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "FitPlan Pro API",
                version = "1.0",
                description = "API for FitPlan Pro - Personalized Weight Loss & Fitness Management System",
                contact = @Contact(
                        name = "FitPlan Pro Team",
                        email = "support@fitplanpro.com",
                        url = "https://fitplanpro.com"
                ),
                license = @License(
                        name = "Proprietary",
                        url = "https://fitplanpro.com/terms"
                )
        ),
        servers = {
                @Server(
                        url = "http://localhost:8080",
                        description = "Development Server"
                ),
                @Server(
                        url = "https://api.fitplanpro.com",
                        description = "Production Server"
                )
        }
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
public class SwaggerConfig {
    // Configuration is done via annotations
}