package com.sprint.mission.discodeit.event.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.event.message.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.user.RoleUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaProduceRequiredEventListener {
  private static final String MESSAGE_CREATED_TOPIC = "discodeit.MessageCreatedEvent";
  private static final String ROLE_UPDATED_TOPIC = "discodeit.RoleUpdatedEvent";

  private final KafkaTemplate<String, String> kafkaTemplate;
  private final ObjectMapper objectMapper;

  @Async("taskExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void on(MessageCreatedEvent event) {
    send(MESSAGE_CREATED_TOPIC, event.messageId().toString(), event);
  }

  @Async("taskExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void on(RoleUpdatedEvent event){
    send(ROLE_UPDATED_TOPIC, event.userId().toString(), event);
  }

  private void send(String topic, String key, Object event){
    try{
      String payload = objectMapper.writeValueAsString(event);
      kafkaTemplate.send(topic, key, payload);

      log.info("Sent message to topic '{}', key '{}'", topic, key);
    } catch(JsonProcessingException e){
      log.error("Failed to serialize Kafka event topic '{}' event '{}'", topic, event, e);
    }
  }
}
