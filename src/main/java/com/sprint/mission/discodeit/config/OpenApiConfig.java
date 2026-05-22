package com.sprint.mission.discodeit.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  @Bean
  public OpenAPI openAPI() {
    SecurityScheme csrfScheme = new SecurityScheme()
        .type(SecurityScheme.Type.APIKEY) // API 키 방식
        .in(SecurityScheme.In.HEADER)     // 헤더에 담기
        .name("X-XSRF-TOKEN");            // 스프링 시큐리티 기본 CSRF 헤더 이름

    // 2. 보안 요구사항 정의
    SecurityRequirement securityRequirement = new SecurityRequirement()
        .addList("csrfToken");

    return new OpenAPI()
        .info(new Info()
            .title("Discodeit API 문서")
            .description("Discodeit 프로젝트의 Swagger API 문서입니다.")
        )
        .addSecurityItem(securityRequirement)
        .components(new Components()
            .addSecuritySchemes("csrfToken", csrfScheme)
        );
  }
}
