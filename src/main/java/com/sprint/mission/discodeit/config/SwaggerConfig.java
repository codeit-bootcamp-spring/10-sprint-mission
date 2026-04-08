package com.sprint.mission.discodeit.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .openapi("3.1.0")
                .info(new Info()
                        .title("Discodeit API 문서")
                        .description("Discodeit 프로젝트의 Swagger API 문서입니다.")
                        .version("1.2")) // 버전은 임의 지정

                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("로컬 서버")
                ))

                .tags(List.of(
                        new Tag().name("Channel").description("Channel API"),
                        new Tag().name("ReadStatus").description("Message 읽음 상태 API"),
                        new Tag().name("Message").description("Message API"),
                        new Tag().name("User").description("User API"),
                        new Tag().name("BinaryContent").description("첨부 파일 API"),
                        new Tag().name("Auth").description("인증 API")
                ));
    }
}
