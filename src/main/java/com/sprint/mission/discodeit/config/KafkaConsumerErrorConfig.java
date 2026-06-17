package com.sprint.mission.discodeit.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

/**
 * Kafka 컨슈머 공통 에러 처리.
 * 역직렬화/알림 생성 실패 시 고정 백오프로 재시도하고,
 * 모두 실패하면 원본 토픽의 ".DLT" 토픽으로 메시지를 보내 무한 재처리를 방지한다.
 */
@Configuration
@Slf4j
public class KafkaConsumerErrorConfig {

    private static final long RETRY_INTERVAL_MS = 1000L;
    private static final long MAX_RETRIES = 2L;

    @Bean
    public DefaultErrorHandler kafkaErrorHandler(KafkaTemplate<String, String> kafkaTemplate) {
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(kafkaTemplate);
        DefaultErrorHandler errorHandler =
                new DefaultErrorHandler(recoverer, new FixedBackOff(RETRY_INTERVAL_MS, MAX_RETRIES));
        errorHandler.setRetryListeners((record, ex, deliveryAttempt) ->
                log.warn("Kafka 소비 재시도: topic={}, attempt={}, message={}",
                        record.topic(), deliveryAttempt, ex.getMessage()));
        return errorHandler;
    }
}
