package com.sprint.mission.discodeit.util.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.auth.enums.Role;
import com.sprint.mission.discodeit.dto.message.MessageDto;
import com.sprint.mission.discodeit.dto.notification.NotificationCreateRequest;
import com.sprint.mission.discodeit.dto.notification.NotificationDto;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.event.ErrorNotificationEvent;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import com.sprint.mission.discodeit.service.SseService;
import java.util.List;
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
  private final SseService sseService;

  @KafkaListener(topics = "discodeit.MessageCreatedEvent")
  public void onMessageCreatedEvent(String kafkaEvent) {
    try {
      log.debug("Kafka: MessageCreatedEvent 알림 처리 시작");
      MessageCreatedEvent event = objectMapper.readValue(kafkaEvent,
          MessageCreatedEvent.class);

      MessageDto data = event.data();
      String title =
          data.author().username() + " (#" + (event.channelName() != null ? event.channelName() :
              ChannelType.PRIVATE) + ")";
      readStatusRepository.findAllByChannelIdAndNotificationEnabledAndUserIdNot(data.channelId(),
              true, data.author().id())
          .forEach(r -> {
            NotificationDto response = notificationService.create(
                new NotificationCreateRequest(r.getUser().getId(), title, data.content()));
            sseService.send(List.of(r.getUser().getId()), "notifications.created", response);
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
      NotificationDto response = notificationService.create(
          new NotificationCreateRequest(event.userId(), title, content));
      sseService.send(List.of(event.userId()), "notifications.created", response);

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
          .forEach(u -> {
            NotificationDto response = notificationService.create(new NotificationCreateRequest(
                u.getId(),
                event.title(),
                event.content()
            ));
            sseService.send(List.of(u.getId()), "notifications.created", response);
          });
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    log.info("Kafka: ErrorNotificationEvent 알림 처리 성공");
  }
}
