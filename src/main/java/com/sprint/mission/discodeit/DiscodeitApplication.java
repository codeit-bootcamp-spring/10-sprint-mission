package com.sprint.mission.discodeit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class DiscodeitApplication {
    public static void main(String[] args) {
        // 스프링 컨테이너 실행
        ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);
    }
}