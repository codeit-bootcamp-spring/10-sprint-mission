package com.sprint.mission.discodeit.event.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.event.BinaryUploadFailedEvent;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
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

  @Async
  @TransactionalEventListener
  public void on(MessageCreatedEvent event) {
    try {
      String payload = objectMapper.writeValueAsString(event);

      kafkaTemplate.send("discodeit.MessageCreatedEvent", payload);

      log.info("[KAFKA] MessageCreatedEvent sent: {}", payload);

    } catch (Exception e) {
      log.error("[KAFKA FAIL] MessageCreatedEvent", e);
    }
  }

  @Async
  @TransactionalEventListener
  public void on(RoleUpdatedEvent event) {
    try {
      String payload = objectMapper.writeValueAsString(event);

      kafkaTemplate.send("discodeit.RoleUpdatedEvent", payload);

      log.info("[KAFKA] RoleUpdatedEvent sent: {}", payload);

    } catch (Exception e) {
      log.error("[KAFKA FAIL] RoleUpdatedEvent", e);
    }
  }

  @Async
  @EventListener
  public void on(BinaryUploadFailedEvent event) {
    try {
      String payload = objectMapper.writeValueAsString(event);

      kafkaTemplate.send("discodeit.BinaryUploadFailedEvent", payload);

      log.info("[KAFKA] BinaryUploadFailedEvent sent: {}", payload);

    } catch (Exception e) {
      log.error("[KAFKA FAIL] BinaryUploadFailedEvent", e);
    }
  }
}
