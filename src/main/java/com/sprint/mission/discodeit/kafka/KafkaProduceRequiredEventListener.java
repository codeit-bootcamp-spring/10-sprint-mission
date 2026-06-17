package com.sprint.mission.discodeit.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.aws.event.S3UploadFailedEvent;
import com.sprint.mission.discodeit.message.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.user.event.RoleUpdatedEvent;
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

  @Async("taskExecutor")
  @TransactionalEventListener
  public void on(MessageCreatedEvent event) {
    try {
      String payload = objectMapper.writeValueAsString(event);
      kafkaTemplate.send("discodeit.MessageCreatedEvent", payload);
    } catch (JsonProcessingException e) {
      log.error("Kafka 발행 실패: {}", e.getMessage());
      throw new RuntimeException(e);
    }
  }

  @Async("taskExecutor")
  @TransactionalEventListener
  public void on(RoleUpdatedEvent event) {
    try {
      String payload = objectMapper.writeValueAsString(event);
      kafkaTemplate.send("discodeit.RoleUpdatedEvent", payload);
    } catch (JsonProcessingException e) {
      log.error("Kafka 발행 실패: {}", e.getMessage());
      throw new RuntimeException(e);
    }
  }

  @Async("taskExecutor")
  @EventListener
  public void on(S3UploadFailedEvent event) {
    try {
      String payload = objectMapper.writeValueAsString(event);
      kafkaTemplate.send("discodeit.S3UploadFailedEvent", payload);
    } catch (JsonProcessingException e) {
      log.error("Kafka 발행 실패: {}", e.getMessage());
      throw new RuntimeException(e);
    }

  }

}
