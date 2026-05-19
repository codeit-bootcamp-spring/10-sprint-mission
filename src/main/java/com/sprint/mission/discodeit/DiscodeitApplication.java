package com.sprint.mission.discodeit;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Slf4j
@SpringBootApplication
@EnableJpaAuditing
public class DiscodeitApplication {

    public static void main(String[] args) {
        log.info("애플리케이션 시작 중");
        SpringApplication.run(DiscodeitApplication.class, args);
        log.info("애플리케이션이 성공적으로 시작됨");
    }

}
