package com.sprint.mission.discodeit.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

/*
    AppConfig
    -------------
    애플리케이션 전역 설정
 */
@Configuration
@EnableJpaAuditing      // 엔티티 생성 / 수정 시간 자동화
@EnableScheduling       // 주기적 만료 토큰 삭제 작업을 위한 스케줄러 활성화
public class AppConfig {
}
