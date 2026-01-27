package com.sprint.mission.discodeit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class DiscodeitApplication {

    public static void main(String[] args) {
        // Spring Container 구동
        ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);

        System.out.println("=== Spring Boot 구동 성공 ===");
    }
}