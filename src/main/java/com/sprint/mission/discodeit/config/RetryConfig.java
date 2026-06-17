package com.sprint.mission.discodeit.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;

@Configuration
@EnableRetry // @Retryable, @Recover 기반의 재시도/복구 로직 활성화 (비동기 작업 실패 복구에 사용)
public class RetryConfig {

}
