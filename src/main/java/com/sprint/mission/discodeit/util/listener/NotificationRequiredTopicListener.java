package com.sprint.mission.discodeit.util.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.auth.enums.Role;
import com.sprint.mission.discodeit.dto.notification.NotificationCreateRequest;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.event.ErrorNotificationEvent;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(
    name = "notification-type.kafka.enabled",
    havingValue = "true"
)
public class NotificationRequiredTopicListener {

  private final NotificationService notificationService;
  private final ReadStatusRepository readStatusRepository;
  private final UserRepository userRepository;
  private final ObjectMapper objectMapper;

  @KafkaListener(topics = "discodeit.MessageCreatedEvent")
  public void onMessageCreatedEvent(String kafkaEvent) {
    try {
      log.debug("Kafka: MessageCreatedEvent 알림 처리 시작");
      MessageCreatedEvent event = objectMapper.readValue(kafkaEvent,
          MessageCreatedEvent.class);

      String title =
          event.authorName() + " (#" + (event.channelName() != null ? event.channelName() :
              ChannelType.PRIVATE) + ")";
      readStatusRepository.findAllByChannelIdAndNotificationEnabledAndUserIdNot(event.channelId(),
              true, event.authorId())
          .forEach(r -> {
            notificationService.create(
                new NotificationCreateRequest(r.getUser().getId(), title, event.content()));
          });
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    log.info("Kafka: MessageCreatedEvent 알림 처리 성공");
  }

  @KafkaListener(topics = "discodeit.RoleUpdatedEvent")
  public void onRoleUpdatedEvent(String kafkaEvent) {
    try {
      log.debug("Kafka: RoleUpdatedEvent 알림 처리 시작");
      RoleUpdatedEvent event = objectMapper.readValue(kafkaEvent,
          RoleUpdatedEvent.class);

      String title = "권한이 변경되었습니다.";
      String content = event.beforeRole() + " -> " + event.afterRole();
      notificationService.create(new NotificationCreateRequest(event.userId(), title, content));

    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    log.info("Kafka: RoleUpdatedEvent 알림 처리 성공");
  }

  @KafkaListener(topics = "discodeit.ErrorNotificationEvent")
  public void onS3UploadFailedEvent(String kafkaEvent) {
    try {
      log.debug("Kafka: ErrorNotificationEvent 알림 처리 시작");
      ErrorNotificationEvent event = objectMapper.readValue(kafkaEvent,
          ErrorNotificationEvent.class);

      userRepository.findAllByRole(Role.ADMIN)
          .forEach(u -> notificationService.create(new NotificationCreateRequest(
              u.getId(),
              event.title(),
              event.content()
          )));

    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    log.info("Kafka: ErrorNotificationEvent 알림 처리 성공");
  }
}
