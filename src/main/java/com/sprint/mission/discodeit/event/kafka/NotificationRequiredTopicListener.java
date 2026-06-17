package com.sprint.mission.discodeit.event.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.event.S3UploadFailedEvent;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Component
public class NotificationRequiredTopicListener {

  private static final int NOTIFICATION_CONTENT_MAX_LENGTH = 500;

  private final ReadStatusRepository readStatusRepository;
  private final UserRepository userRepository;
  private final NotificationService notificationService;
  private final ObjectMapper objectMapper;

  @KafkaListener(topics = KafkaTopics.MESSAGE_CREATED)
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void onMessageCreatedEvent(String kafkaEvent) {
    try {
      MessageCreatedEvent event = objectMapper.readValue(kafkaEvent, MessageCreatedEvent.class);
      readStatusRepository.findAllByChannelIdAndNotificationEnabledTrueWithUser(event.channelId())
          .stream()
          .map(ReadStatus::getUser)
          .filter(user -> !user.getId().equals(event.authorId()))
          .forEach(user -> notificationService.create(
              user.getId(),
              messageNotificationTitle(event),
              event.content()
          ));
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Failed to deserialize MessageCreatedEvent", e);
    }
  }

  @KafkaListener(topics = KafkaTopics.ROLE_UPDATED)
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void onRoleUpdatedEvent(String kafkaEvent) {
    try {
      RoleUpdatedEvent event = objectMapper.readValue(kafkaEvent, RoleUpdatedEvent.class);
      notificationService.create(
          event.userId(),
          "권한이 변경되었습니다.",
          event.previousRole().name() + " -> " + event.newRole().name()
      );
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Failed to deserialize RoleUpdatedEvent", e);
    }
  }

  @KafkaListener(topics = KafkaTopics.S3_UPLOAD_FAILED)
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void onS3UploadFailedEvent(String kafkaEvent) {
    try {
      S3UploadFailedEvent event = objectMapper.readValue(kafkaEvent, S3UploadFailedEvent.class);
      String content = s3UploadFailedNotificationContent(event);
      userRepository.findAllByRole(Role.ADMIN)
          .forEach(admin -> notificationService.create(
              admin.getId(),
              "S3 파일 업로드 실패",
              content
          ));
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Failed to deserialize S3UploadFailedEvent", e);
    }
  }

  private String messageNotificationTitle(MessageCreatedEvent event) {
    return event.authorUsername() + " (#" + channelLabel(event.channelName(), event.channelId())
        + ")";
  }

  private String channelLabel(String channelName, UUID channelId) {
    if (channelName != null && !channelName.isBlank()) {
      return channelName;
    }
    return channelId.toString();
  }

  private String s3UploadFailedNotificationContent(S3UploadFailedEvent event) {
    String content = String.join(System.lineSeparator(),
        "TaskName: S3 파일 업로드",
        "RequestId: " + event.requestId(),
        "BinaryContentId: " + event.binaryContentId(),
        "Error: " + event.errorMessage()
    );
    if (content.length() <= NOTIFICATION_CONTENT_MAX_LENGTH) {
      return content;
    }
    return content.substring(0, NOTIFICATION_CONTENT_MAX_LENGTH - 3) + "...";
  }
}
