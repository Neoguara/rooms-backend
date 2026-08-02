package com.neoguara.rooms.shared.infrastructure.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI roomsOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Rooms API")
                        .description("""
                                REST API for managing room reservations and events. It covers buildings, \
                                rooms and room types, room resources, users, and the event lifecycle \
                                (including event requests and approvals).

                                Most endpoints require authentication. Obtain a JWT via the `/auth` \
                                endpoints and send it as a Bearer token using the "Authorize" button.""")
                        .version("0.0.1-SNAPSHOT")
                        .contact(new Contact()
                                .name("Neoguara")
                                .email("joseronaldosantus@gmail.com"))
                        .license(new License()
                                .name("GNU General Public License v3.0")
                                .url("https://www.gnu.org/licenses/gpl-3.0.html")))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME, new SecurityScheme()
                                .name(SECURITY_SCHEME_NAME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT bearer token obtained from the /auth endpoints.")));
    }
}
