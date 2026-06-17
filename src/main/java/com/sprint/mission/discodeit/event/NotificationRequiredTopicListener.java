package com.sprint.mission.discodeit.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@ConditionalOnBooleanProperty(value = "discodeit.kafka.enabled", havingValue = true)
@Component
public class NotificationRequiredTopicListener {

  private final ObjectMapper objectMapper;
  private final NotificationService notificationService;

  @KafkaListener(topics = "discodeit.MessageCreatedEvent")
  public void onMessageCreatedEvent(String kafkaEvent) throws JsonProcessingException {
    log.debug("Event [MessageCreatedEvent]: start");
    MessageCreatedEvent event = objectMapper.readValue(kafkaEvent,
        MessageCreatedEvent.class);
    notificationService.registerMessageCreatedNotification(event);
    log.debug("Event [MessageCreatedEvent]: end");
  }

  @KafkaListener(topics = "discodeit.RoleUpdatedEvent")
  public void onRoleUpdatedEvent(String kafkaEvent) throws JsonProcessingException {
    log.debug("Event [RoleUpdatedEvent]: start");
    RoleUpdatedEvent event = objectMapper.readValue(kafkaEvent, RoleUpdatedEvent.class);
    notificationService.registerRoleUpdatedNotification(event);
    log.debug("Event [RoleUpdatedEvent]: end");
  }

  @KafkaListener(topics = "discodeit.BinaryContentUploadFailedEvent")
  public void onBinaryContentUploadFailedEvent(String kafkaEvent) throws JsonProcessingException {
    log.debug("Event [BinaryContentUploadFailedEvent]: start");
    BinaryContentUploadFailedEvent event = objectMapper.readValue(kafkaEvent,
        BinaryContentUploadFailedEvent.class);
    notificationService.registerBinaryContentUploadFailNotification(event);
    log.debug("Event [BinaryContentUploadFailedEvent]: end");
  }
}
