package com.kanflow.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI kanflowOpenAPI(@Value("${server.port:9090}") int serverPort) {
        String localhost = "http://localhost:" + serverPort;
        String loopback = "http://127.0.0.1:" + serverPort;
        return new OpenAPI()
                .info(new Info()
                        .title("Kanflow API")
                        .version("1.0.0")
                        .description("""
                                API REST do Kanflow: usuários, cards, itens de checklist, comentários e sprints.

                                **Banco:** PostgreSQL local (`kanflow`). Credenciais por defeito: utilizador `postgres` (ver `application.yml`). Para outra senha sem alterar o ficheiro versionado, usa `config/application.yml` (exemplo em `config/application.example.yml`).

                                **Documentação interativa:** [Swagger UI](/swagger-ui.html) · [OpenAPI JSON](/api-docs).
                                """)
                        .contact(new Contact().name("Kanflow")))
                .servers(List.of(
                        new Server().url(localhost).description("Local (use o mesmo host que abriu o Swagger)"),
                        new Server().url(loopback).description("Local via 127.0.0.1")));
    }
}
