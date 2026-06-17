package com.sprint.mission.discodeit.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@RequiredArgsConstructor
@ConditionalOnBooleanProperty(value = "discodeit.kafka.enabled", havingValue = true)
@Component
public class KafkaProduceRequiredEventListener {

  private final KafkaTemplate<String, String> kafkaTemplate;
  private final ObjectMapper objectMapper;

  @Async("eventTaskExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void on(MessageCreatedEvent event) throws JsonProcessingException {
    String payload = objectMapper.writeValueAsString(event);
    kafkaTemplate.send("discodeit.MessageCreatedEvent", payload);
  }

  @Async("eventTaskExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void on(RoleUpdatedEvent event) throws JsonProcessingException {
    String payload = objectMapper.writeValueAsString(event);
    kafkaTemplate.send("discodeit.RoleUpdatedEvent", payload);
  }

  @Async("eventTaskExecutor")
  @EventListener
  public void on(BinaryContentUploadFailedEvent event) throws JsonProcessingException {
    String payload = objectMapper.writeValueAsString(event);
    kafkaTemplate.send("discodeit.BinaryContentUploadFailedEvent", payload);
  }
}