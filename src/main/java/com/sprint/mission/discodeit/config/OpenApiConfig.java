package com.sprint.mission.discodeit.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI discodeitOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Discodeit API")
                        .version("v1")
                        .description("Discodeit API 문서"));
    }
}
