package com.sprint.mission.discodeit.event.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.event.S3UploadFailedEvent;
import com.sprint.mission.discodeit.event.kafka.dto.MessageCreatedKafkaEvent;
import com.sprint.mission.discodeit.event.kafka.dto.RoleUpdatedKafkaEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
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
    private static final String S3_UPLOAD_FAILED_TOPIC = "discodeit.S3UploadFailedEvent";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Async("eventTaskExecutor")
    @TransactionalEventListener
    public void on(MessageCreatedEvent event) {
        Message message = event.message();

        MessageCreatedKafkaEvent kafkaEvent = new MessageCreatedKafkaEvent(
                message.getId(),
                message.getChannel().getId(),
                message.getChannel().getName(),
                message.getAuthor().getId(),
                message.getAuthor().getUsername(),
                message.getContent()
        );

        publish(MESSAGE_CREATED_TOPIC, kafkaEvent);
    }

    @Async("eventTaskExecutor")
    @TransactionalEventListener
    public void on(RoleUpdatedEvent event) {
        User user = event.user();

        RoleUpdatedKafkaEvent kafkaEvent = new RoleUpdatedKafkaEvent(
                user.getId(),
                user.getUsername(),
                event.oldRole(),
                event.newRole()
        );

        publish(ROLE_UPDATED_TOPIC, kafkaEvent);
    }

    @Async("eventTaskExecutor")
    @EventListener
    public void on(S3UploadFailedEvent event) {
        publish(S3_UPLOAD_FAILED_TOPIC, event);
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