package com.sprint.mission.discodeit.event.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.event.S3UploadFailedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 알림 서비스를 별도 서버로 분리했다고 가정하고, 도메인 이벤트를 Kafka 토픽으로 외부 발행한다.
 * Message/Role 이벤트는 서비스 트랜잭션 커밋 후 발행하고,
 * S3 업로드 실패 이벤트는 트랜잭션 없는 @Recover에서 발행되므로 일반 @EventListener로 받는다.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaProduceRequiredEventListener {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Async("eventTaskExecutor")
    @TransactionalEventListener
    public void on(MessageCreatedEvent event) {
        publish(KafkaTopics.MESSAGE_CREATED, event);
    }

    @Async("eventTaskExecutor")
    @TransactionalEventListener
    public void on(RoleUpdatedEvent event) {
        publish(KafkaTopics.ROLE_UPDATED, event);
    }

    @Async("eventTaskExecutor")
    @EventListener
    public void on(S3UploadFailedEvent event) {
        publish(KafkaTopics.S3_UPLOAD_FAILED, event);
    }

    private void publish(String topic, Object event) {
        String payload;
        try {
            payload = objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            log.error("Kafka 직렬화 실패: topic={}, event={}", topic, event, e);
            return;
        }
        kafkaTemplate.send(topic, payload).whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Kafka 발행 실패: topic={}, payload={}", topic, payload, ex);
            } else {
                log.info("Kafka 발행 성공: topic={}, partition={}, offset={}",
                        topic,
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            }
        });
    }
}
