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

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaProduceRequiredEventListener {

  private final KafkaTemplate<String, String> kafkaTemplate;
  private final ObjectMapper objectMapper;

  @Async("eventTaskExecutor")
  @TransactionalEventListener
  public void on(MessageCreatedEvent event) {
    send(KafkaTopics.MESSAGE_CREATED, event.messageId().toString(), event);
  }

  @Async("eventTaskExecutor")
  @TransactionalEventListener
  public void on(RoleUpdatedEvent event) {
    send(KafkaTopics.ROLE_UPDATED, event.userId().toString(), event);
  }

  @Async("eventTaskExecutor")
  @EventListener
  public void on(S3UploadFailedEvent event) {
    send(KafkaTopics.S3_UPLOAD_FAILED, event.binaryContentId().toString(), event);
  }

  private void send(String topic, String key, Object event) {
    try {
      String payload = objectMapper.writeValueAsString(event);
      kafkaTemplate.send(topic, key, payload);
      log.info("Kafka event published: topic={}, key={}", topic, key);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Failed to serialize Kafka event: topic=" + topic, e);
    }
  }
}
