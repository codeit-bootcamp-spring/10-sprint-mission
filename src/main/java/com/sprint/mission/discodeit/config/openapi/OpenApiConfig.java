package com.sprint.mission.discodeit.config.openapi;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        Server localServer = new Server()
                .url("http://localhost:8080")
                .description("로컬 서버");

        return new OpenAPI()
                .info(new Info()
                        .title("Discodeit API 문서")
                        .description("Discodeit 프로젝트의 Swagger API 문서입니다.")
                        .version("1.2"))
                .servers(List.of(localServer));
    }
}
