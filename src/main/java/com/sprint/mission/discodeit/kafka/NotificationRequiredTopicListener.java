package com.sprint.mission.discodeit.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.message.entity.ReadStatus;
import com.sprint.mission.discodeit.message.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.message.repository.JPAReadStatusRepository;
import com.sprint.mission.discodeit.notification.entity.Notification;
import com.sprint.mission.discodeit.notification.repository.JPANotificationRepository;
import com.sprint.mission.discodeit.user.Role;
import com.sprint.mission.discodeit.user.event.RoleUpdatedEvent;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class NotificationRequiredTopicListener {

  private final ObjectMapper objectMapper;
  private final JPAReadStatusRepository jpaReadStatusRepository;
  private final JPANotificationRepository jpaNotificationRepository;

  @KafkaListener(topics = "discodeit.MessageCreatedEvent")
  public void onMessageCreatedEvent(String kafkaEvent) {
    try {
      MessageCreatedEvent event = objectMapper.readValue(kafkaEvent, MessageCreatedEvent.class);

      List<ReadStatus> users = jpaReadStatusRepository.findAllByChannelIdAndNotificationEnabledTrue(
          event.message().getChannel().getId()
      );
      for (ReadStatus readStatus : users) {
        if (readStatus.getUser().getId().equals(event.message().getAuthor().getId())) {
          continue;
        }
        Notification notification = new Notification(
            readStatus.getUser().getId(),
            event.message().getAuthor().getUsername() + " (#" + event.message().getChannel()
                .getName() + ")",
            event.message().getContent()
        );
        jpaNotificationRepository.save(notification);
      }
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  @KafkaListener(topics = "discodeit.RoleUpdatedEvent")
  public void onRoleUpdatedEvent(String kafkaEvent) {
    try {
      RoleUpdatedEvent event = objectMapper.readValue(kafkaEvent, RoleUpdatedEvent.class);
      Role oldRole = event.oldRole();
      Role newRole = event.newRole();
      Notification notification = new Notification(
          event.userId(),
          "권한이 변경되었습니다.",
          oldRole.toString() + " -> " + newRole.toString()
      );
      jpaNotificationRepository.save(notification);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  @KafkaListener(topics = "discodeit.S3UploadFailedEvent")
  public void onS3UploadFailedEvent(String kafkaEvent) {
  }

}
