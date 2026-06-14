package com.sprint.mission.discodeit.event.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.config.CacheNames;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.event.S3UploadFailedEvent;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationRequiredTopicListener {

  private static final String ROLE_UPDATED_TITLE = "권한이 변경되었습니다.";
  private static final String S3_UPLOAD_FAILED_TITLE = "S3 파일 업로드 실패";

  private final ObjectMapper objectMapper;
  private final NotificationRepository notificationRepository;
  private final MessageRepository messageRepository;
  private final ReadStatusRepository readStatusRepository;
  private final UserRepository userRepository;
  private final CacheManager cacheManager;

  @KafkaListener(topics = KafkaTopics.MESSAGE_CREATED)
  @Transactional
  public void onMessageCreatedEvent(String kafkaEvent) {
    try {
      MessageCreatedEvent event = objectMapper.readValue(kafkaEvent, MessageCreatedEvent.class);
      log.debug("MessageCreatedEvent 수신: messageId={}, channelId={}",
          event.messageId(), event.channelId());

      Message message = messageRepository.findById(event.messageId()).orElse(null);
      if (message == null) {
        log.warn("메시지가 존재하지 않습니다: messageId={}", event.messageId());
        return;
      }

      List<ReadStatus> readStatuses = readStatusRepository.findAllByChannelIdWithUser(
          event.channelId());

      List<UUID> notifiedUserIds = readStatuses.stream()
          .filter(ReadStatus::isNotificationEnabled)
          .map(readStatus -> readStatus.getUser().getId())
          .filter(userId -> !userId.equals(event.authorId()))
          .toList();

      if (notifiedUserIds.isEmpty()) {
        log.debug("알림 대상 사용자가 없습니다: channelId={}", event.channelId());
        return;
      }

      String title = buildMessageTitle(message);
      String content = message.getContent();

      List<Notification> notifications = notifiedUserIds.stream()
          .map(userId -> new Notification(userId, title, content))
          .toList();
      notificationRepository.saveAll(notifications);

      evictNotificationsCache(notifiedUserIds);

      log.info("메시지 알림 저장 완료: messageId={}, 알림 대상 수={}",
          event.messageId(), notifiedUserIds.size());
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  @KafkaListener(topics = KafkaTopics.ROLE_UPDATED)
  @Transactional
  public void onRoleUpdatedEvent(String kafkaEvent) {
    try {
      RoleUpdatedEvent event = objectMapper.readValue(kafkaEvent, RoleUpdatedEvent.class);
      log.debug("RoleUpdatedEvent 수신: userId={}, oldRole={}, newRole={}",
          event.userId(), event.oldRole(), event.newRole());

      String content = event.oldRole().name() + " -> " + event.newRole().name();
      Notification notification = new Notification(event.userId(), ROLE_UPDATED_TITLE, content);
      notificationRepository.save(notification);

      evictNotificationsCache(List.of(event.userId()));

      log.info("권한 변경 알림 저장 완료: userId={}, content={}", event.userId(), content);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  @KafkaListener(topics = KafkaTopics.S3_UPLOAD_FAILED)
  @Transactional
  public void onS3UploadFailedEvent(String kafkaEvent) {
    try {
      S3UploadFailedEvent event = objectMapper.readValue(kafkaEvent, S3UploadFailedEvent.class);
      log.debug("S3UploadFailedEvent 수신: binaryContentId={}", event.binaryContentId());

      String content = String.format(
          "RequestId: %s%nOperation: %s%nBinaryContentId: %s%nError: %s",
          event.requestId(),
          event.operation(),
          event.binaryContentId(),
          event.errorMessage()
      );

      List<User> admins = userRepository.findAllByRole(Role.ADMIN);
      if (admins.isEmpty()) {
        log.warn("ADMIN 사용자가 없어 실패 알림을 보낼 수 없습니다.");
        return;
      }

      List<Notification> notifications = admins.stream()
          .map(admin -> new Notification(admin.getId(), S3_UPLOAD_FAILED_TITLE, content))
          .toList();
      notificationRepository.saveAll(notifications);

      List<UUID> adminIds = admins.stream().map(User::getId).toList();
      evictNotificationsCache(adminIds);

      log.info("S3 업로드 실패 알림 발송 완료: adminCount={}, binaryContentId={}",
          admins.size(), event.binaryContentId());
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  private String buildMessageTitle(Message message) {
    String authorName = message.getAuthor() != null
        ? message.getAuthor().getUsername()
        : "Unknown";
    if (message.getChannel().getType() == ChannelType.PUBLIC) {
      return authorName + " (#" + message.getChannel().getName() + ")";
    }
    return authorName;
  }

  private void evictNotificationsCache(List<UUID> userIds) {
    Cache cache = cacheManager.getCache(CacheNames.NOTIFICATIONS);
    if (cache == null) {
      return;
    }
    userIds.forEach(cache::evict);
  }
}
