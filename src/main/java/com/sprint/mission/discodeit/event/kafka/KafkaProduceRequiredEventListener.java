package com.sprint.mission.discodeit.event.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.event.BinaryContentUploadFailedEvent;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaProduceRequiredEventListener {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    /// Kafka에 이벤트만 발행.
    /// 다른 Consumer가 알림 처리.
    @Async("ioTaskExecutor")
    @TransactionalEventListener
    public void on(MessageCreatedEvent event) throws JsonProcessingException {
        String payload = objectMapper.writeValueAsString(event);
        kafkaTemplate.send("discodeit.MessageCreatedEvent", payload);
    }

    /// Kafka에 RoleUpdateEvent 발행
    @Async("ioTaskExecutor")
    @TransactionalEventListener
    public void on(RoleUpdatedEvent event) throws JsonProcessingException {
        String payload = objectMapper.writeValueAsString(event);
        kafkaTemplate.send("discodeit.RoleUpdatedEvent", payload);
    }

    /// Kafka에 BinaryContentUploadFailedEvent 발행
    @Async("ioTaskExecutor")
    @TransactionalEventListener
    public void on(BinaryContentUploadFailedEvent event) throws JsonProcessingException {
        String payload = objectMapper.writeValueAsString(event);
        kafkaTemplate.send("discodeit.BinaryContentUploadFailedEvent", payload);
    }
}
