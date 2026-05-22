package com.sprint.mission.discodeit.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

  @Bean
  public OpenAPI openAPI() {
    return new OpenAPI()
        .info(new Info()
            .title("Discodeit API 문서")
            .description("Discodeit 프로젝트의 Swagger API 문서입니다.")
            .version("v2.1-M10")
        )
        .servers(List.of(
            new Server().url("/").description("API 서버")
        ));
  }
}
