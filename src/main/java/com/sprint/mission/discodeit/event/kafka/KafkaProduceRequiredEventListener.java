package com.sprint.mission.discodeit.event.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.event.S3UploadFailedEvent;
import com.sprint.mission.discodeit.exception.event.EventSerializationFailedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

// Spring Event를 받아서 Kafka topic으로 발행하는 Listener
@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaProduceRequiredEventListener {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Async("eventTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(MessageCreatedEvent event) {
        // MessageCreatedEvent를 String으로 변환 후 payload 변수에 할당
        sendEvent("discodeit.MessageCreatedEvent", event);
    }

    @Async("eventTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(RoleUpdatedEvent event) {
        // RoleUpdatedEvent를 String으로 변환 후 payload 변수에 할당
        sendEvent("discodeit.RoleUpdatedEvent", event);
    }

    @Async("eventTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(S3UploadFailedEvent event) {
        // RoleUpdatedEvent를 String으로 변환 후 payload 변수에 할당
        sendEvent("discodeit.S3UploadFailedEvent", event);
    }

    private void sendEvent(String topic, Object event) {
        try {
            // Event를 String으로 변환 후 payload 변수에 할당
            String payload = objectMapper.writeValueAsString(event);

            kafkaTemplate.send(topic, payload)
                    .whenComplete((result, e) -> {
                        if (e != null) {
                            log.error("[KAFKA_EVENT_PUBLISH_FAULED] Kafka 이벤트 발행 실패: topic={}, event={}",
                                    topic, event.getClass().getSimpleName(), e);
                        } else {
                            log.debug("[KAFKA_EVENT_PUBLISH_SUCCESS] Kafka 이벤트 발행 성공: topic={}, event={}",
                                    topic, event.getClass().getSimpleName());
                        }
                    });
        } catch (JsonProcessingException e) {
            log.error("[EVENT_SERIALIZATION_FAILED] 이벤트 직렬화 실패", e);
            throw new EventSerializationFailedException(
                    event.getClass().getSimpleName() + " 직렬화에 실패했습니다.",
                    e
            );
        }
    }
}
