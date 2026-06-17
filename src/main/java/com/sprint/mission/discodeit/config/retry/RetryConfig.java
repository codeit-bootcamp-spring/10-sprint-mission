package com.sprint.mission.discodeit.config.retry;

// Spring Retry 기능을 켜는 설정 클래스

import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;

@Configuration
@EnableRetry
public class RetryConfig {
}
