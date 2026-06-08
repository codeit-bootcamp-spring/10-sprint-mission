package com.sprint.mission.discodeit.event.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.event.BinaryUploadFailedEvent;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class NotificationRequiredTopicListener {

  private final ObjectMapper objectMapper;
  private final MessageRepository messageRepository;
  private final ReadStatusRepository readStatusRepository;
  private final NotificationRepository notificationRepository;
  private final UserRepository userRepository;
  private final CacheManager cacheManager;

  @KafkaListener(topics = "discodeit.MessageCreatedEvent")
  @Transactional
  public void onMessageCreatedEvent(String kafkaEvent) {
    try {
      MessageCreatedEvent event =
          objectMapper.readValue(kafkaEvent, MessageCreatedEvent.class);

      Message message = messageRepository.findWithAuthorAndChannelById(event.messageId())
          .orElseThrow();

      Channel channel = message.getChannel();

      List<ReadStatus> targets =
          readStatusRepository.findAllByChannelIdAndNotificationEnabledTrueAndUserIdNot(
              channel.getId(),
              message.getAuthor().getId()
          );

      List<Notification> notifications = targets.stream()
          .map(rs -> new Notification(
              rs.getUser(),
              "보낸 사람 (" + channel.getName() + ")",
              message.getContent()
          ))
          .toList();

      notificationRepository.saveAll(notifications);

      Cache cache = cacheManager.getCache("userNotifications");
      if (cache != null) {
        targets.forEach(rs -> cache.evict(rs.getUser().getId()));
      }

      log.info("[KAFKA SUCCESS] Message Notification Created: messageId={}", event.messageId());

    } catch (JsonProcessingException e) {
      log.error("[KAFKA FAIL] MessageCreatedEvent parsing error", e);
    }
  }

  @KafkaListener(topics = "discodeit.RoleUpdatedEvent")
  @Transactional
  public void onRoleUpdatedEvent(String kafkaEvent) {
    try {
      RoleUpdatedEvent event =
          objectMapper.readValue(kafkaEvent, RoleUpdatedEvent.class);

      User user = userRepository.findById(event.userId())
          .orElseThrow();

      Notification notification = new Notification(
          user,
          "권한이 변경되었습니다.",
          event.previousRole() + " -> " + event.newRole()
      );

      notificationRepository.save(notification);

      Cache cache = cacheManager.getCache("userNotifications");
      if (cache != null) {
        cache.evict(event.userId());
      }

      log.info("[KAFKA SUCCESS] Role Notification Created: userId={}", event.userId());

    } catch (JsonProcessingException e) {
      log.error("[KAFKA FAIL] RoleUpdatedEvent parsing error", e);
    }
  }

  @KafkaListener(topics = "discodeit.BinaryUploadFailedEvent")
  @Transactional
  public void onS3UploadFailedEvent(String kafkaEvent) {
    try {
      BinaryUploadFailedEvent event =
          objectMapper.readValue(kafkaEvent, BinaryUploadFailedEvent.class);

      User admin = userRepository.findByRole(Role.ADMIN)
          .orElseThrow(() -> new DiscodeitException(ErrorCode.ADMIN_USER_NOT_FOUND));

      Notification notification = new Notification(
          admin,
          "S3 파일 업로드 실패",
          "RequestId: " + event.requestId()
              + "\nBinaryContentId: " + event.binaryContentId()
              + "\nError: " + event.errorMessage()
      );

      notificationRepository.save(notification);

      Cache cache = cacheManager.getCache("userNotifications");
      if (cache != null) {
        cache.evict(admin.getId());
      }

      log.info("[KAFKA SUCCESS] S3 Upload Failure Notification Created");

    } catch (JsonProcessingException e) {
      log.error("[KAFKA FAIL] S3UploadFailedEvent parsing error", e);
    }
  }
}
