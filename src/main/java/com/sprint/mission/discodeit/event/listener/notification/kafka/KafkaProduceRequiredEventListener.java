package com.sprint.mission.discodeit.event.listener.notification.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.event.BinaryContentEvents;
import com.sprint.mission.discodeit.event.MessageEvents;
import com.sprint.mission.discodeit.event.UserEvents;
import com.sprint.mission.discodeit.exception.etc.InternalServerException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@RequiredArgsConstructor
@Component
@Profile("prod")
public class KafkaProduceRequiredEventListener {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Async("ioTaskExecutor")
    @TransactionalEventListener
    public void on(MessageEvents.Created event) {
        kafkaTemplate.send("discodeit.MessageCreatedEvent", toJson(event));
    }

    @Async("ioTaskExecutor")
    @TransactionalEventListener
    public void on(UserEvents.RoleUpdated event) {
        kafkaTemplate.send("discodeit.RoleUpdatedEvent", toJson(event));
    }

    @Async("ioTaskExecutor")
    @EventListener
    public void on(BinaryContentEvents.S3UploadFailed event) {
        kafkaTemplate.send("discodeit.S3UploadFailedEvent", toJson(event));
    }

    private String toJson(Object event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            log.error("Kafka 메시지 직렬화 실패: {}", e.getMessage(), e);
            throw InternalServerException.dataIntegrity("Kafka 메시지 직렬화 중 오류가 발생했습니다.");
        }
    }
}

