package com.taskproject.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configures the OpenAPI specification exposed at /v3/api-docs and the
 * Swagger UI available at /swagger-ui/index.html.
 *
 * <p>A global "Bearer Token" security scheme is registered so that all
 * protected endpoints can be tested directly from the Swagger UI by
 * pasting the JWT returned from {@code POST /api/auth/login}.</p>
 */
@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME = "BearerAuth";

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Task Management REST API")
                        .description("""
                                A production-style, JWT-secured REST API for managing personal tasks.

                                **Authentication flow:**
                                1. Register → `POST /api/auth/register`
                                2. Login → `POST /api/auth/login` (returns a Bearer token)
                                3. Click **Authorize** at the top right and paste your token.
                                4. All `/api/tasks/**` endpoints are now unlocked.
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Task Management API")
                                .email("api@taskproject.com"))
                        .license(new License()
                                .name("MIT")
                                .url("https://opensource.org/licenses/MIT")))
                // Register a global Bearer security requirement so every endpoint shows the padlock icon.
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME,
                                new SecurityScheme()
                                        .name(SECURITY_SCHEME_NAME)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Paste the JWT token from `POST /api/auth/login` here.")));
    }
}
