package com.sprint.mission.discodeit.event.kafka;
//다른 Spring 서버에 있다고 가정된 코드

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.event.S3UploadFailedEvent;
import com.sprint.mission.discodeit.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class NotificationRequiredTopicListener {

  private final NotificationService notificationService;
  private final ObjectMapper objectMapper;

  @KafkaListener(topics = "discodeit.MessageCreatedEvent")
  public void onMessageCreatedEvent(String kafkaEvent){
    try {
      MessageCreatedEvent event = objectMapper.readValue(kafkaEvent,
          MessageCreatedEvent.class);

      notificationService.createMessageNotifications(
          event.channelId(),
          event.senderId(),
          event.content()
      );
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  @KafkaListener(topics = "discodeit.RoleUpdatedEvent")
  public void onRoleUpdatedEvent(String kafkaEvent){
    try {
      RoleUpdatedEvent event = objectMapper.readValue(kafkaEvent,
          RoleUpdatedEvent.class);

      notificationService.createRoleNotification(
          event.userId(),
          event.beforeRole(),
          event.afterRole()
      );
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  @KafkaListener(topics = "discodeit.S3UploadFailedEvent")
  public void onS3UploadFailedEvent(String kafkaEvent) {
    try {
      S3UploadFailedEvent event = objectMapper.readValue(kafkaEvent,
          S3UploadFailedEvent.class);

      notificationService.s3UploadFailedNotification(event.requestId(), event.binaryContentId(),
          event.errorMessage());
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

}
