package com.sprint.mission.discodeit;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;

@OpenAPIDefinition(
    servers = {
        @Server(url = "http://localhost:8080", description = "로컬 서버")
    }
)

@EnableJpaAuditing
@SpringBootApplication
@EnableScheduling
@EnableRetry
public class DiscodeitApplication {

  public static void main(String[] args) {
    ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class,
        args);

  }

}
