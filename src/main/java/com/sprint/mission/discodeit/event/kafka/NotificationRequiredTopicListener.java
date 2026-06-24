package com.sprint.mission.discodeit.event.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.event.message.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.user.RoleUpdatedEvent;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationRequiredTopicListener {

  private static final String MESSAGE_CREATED_TOPIC = "discodeit.MessageCreatedEvent";
  private static final String ROLE_UPDATED_TOPIC = "discodeit.RoleUpdatedEvent";

  private final ReadStatusRepository readStatusRepository;
  private final NotificationService notificationService;
  private final ObjectMapper objectMapper;

  @KafkaListener(
      topics = MESSAGE_CREATED_TOPIC
//      groupId = "discodeit-group"
  )
  public void onMessageCreated(String payload) {
    try {
      MessageCreatedEvent event = objectMapper.readValue(payload, MessageCreatedEvent.class);

      readStatusRepository.findNotificationTargets(event.channelId(), event.senderId())
          .forEach(readStatus -> {
            String title = "%s (#%s)".formatted(event.senderName(), event.channelName());
            String content = event.content();

            notificationService.create(readStatus.getUserId(), title, content);
          });

      log.info("Message notification created from Kafka. messageId={}", event.messageId());
    } catch (JsonProcessingException e) {
      log.error("Failed to deserialize MessageCreatedEvent. payload={}", payload, e);
    }
  }

  @KafkaListener(
      topics = ROLE_UPDATED_TOPIC
//      groupId = "discodeit-group"
  )
  public void onRoleUpdated(String payload) {
    try {
      RoleUpdatedEvent event = objectMapper.readValue(payload, RoleUpdatedEvent.class);

      String title = "권한이 변경되었습니다.";
      String content = "%s -> %s".formatted(event.oldRole(), event.newRole());

      notificationService.create(event.userId(), title, content);

      log.info("Role update notification created from Kafka. userId={}", event.userId());
    } catch (JsonProcessingException e) {
      log.error("Failed to deserialize RoleUpdatedEvent. payload={}", payload, e);
    }
  }
}