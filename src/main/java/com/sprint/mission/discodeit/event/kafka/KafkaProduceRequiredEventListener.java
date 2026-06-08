package com.sprint.mission.discodeit.event.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaProduceRequiredEventListener {

    private static final String MESSAGE_CREATED_TOPIC = "discodeit.MessageCreatedEvent";
    private static final String ROLE_UPDATED_TOPIC = "discodeit.RoleUpdatedEvent";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Async("eventTaskExecutor")
    @TransactionalEventListener
    public void on(MessageCreatedEvent event) {
        publish(MESSAGE_CREATED_TOPIC, event);
    }

    @Async("eventTaskExecutor")
    @TransactionalEventListener
    public void on(RoleUpdatedEvent event) {
        publish(ROLE_UPDATED_TOPIC, event);
    }

    private void publish(String topic, Object event) {
        try {
            String payload = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(topic, payload);

            log.info("[KAFKA_EVENT_PUBLISH_SUCCESS] topic={}, event={}",
                    topic,
                    event.getClass().getSimpleName()
            );
        } catch (JsonProcessingException e) {
            log.error("[KAFKA_EVENT_PUBLISH_FAIL] topic={}, event={}",
                    topic,
                    event.getClass().getSimpleName(),
                    e
            );
            throw new RuntimeException(e);
        }
    }
}
