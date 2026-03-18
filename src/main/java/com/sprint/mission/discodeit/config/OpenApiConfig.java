package com.sprint.mission.discodeit.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Discodeit API 문서",
        description = "Discodeit 프로젝트의 Swagger API 문서입니다."
    ),
    servers = {
        @Server(url = "http://localhost:8080", description = "로컬 서버")
    },
    tags = {
        @Tag(name = "Channel", description = "Channel API"),
        @Tag(name = "ReadStatus", description = "Message 읽음 상태 API"),
        @Tag(name = "Message", description = "Message API"),
        @Tag(name = "User", description = "User API"),
        @Tag(name = "BinaryContent", description = "첨부 파일 API"),
        @Tag(name = "Auth", description = "인증 API")
    }
)
public class OpenApiConfig {

}