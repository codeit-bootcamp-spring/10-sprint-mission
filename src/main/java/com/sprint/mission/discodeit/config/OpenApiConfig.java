package com.sprint.mission.discodeit.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
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

    //
    SecurityScheme jwtScheme = new SecurityScheme()
        .type(SecurityScheme.Type.HTTP)
        .scheme("bearer")
        .bearerFormat("JWT") // 안내 문구용
        .in(SecurityScheme.In.HEADER)
        .name("Authorization");

    // 2. 보안 요구사항 정의
    SecurityRequirement securityRequirement = new SecurityRequirement()
        .addList("csrfToken")
        .addList("jwtToken");

    return new OpenAPI()
        .info(new Info()
            .title("Discodeit API 문서")
            .description("Discodeit 프로젝트의 Swagger API 문서입니다.")
        )
        .addSecurityItem(securityRequirement)
        .components(new Components()
            .addSecuritySchemes("csrfToken", csrfScheme)
            .addSecuritySchemes("jwtToken", jwtScheme)
        );
  }
}
