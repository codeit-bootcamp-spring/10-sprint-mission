package com.sprint.mission.discodeit.eventlisteners;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.notificationdto.NotificationDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.enums.Role;
import com.sprint.mission.discodeit.events.MessageCreatedEvent;
import com.sprint.mission.discodeit.events.NotificationCreatedEvent;
import com.sprint.mission.discodeit.events.RoleUpdatedEvent;
import com.sprint.mission.discodeit.events.S3UploadFailedEvent;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.basic.SseService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class NotificationRequiredTopicListener {

  private static final String NOTIFICATION_CREATED_TOPIC = "discodeit.NotificationCreatedEvent";
  private static final String NOTIFICATION_CREATED_EVENT_NAME = "notifications.created";

  private final ObjectMapper objectMapper;
  private final KafkaTemplate<String, String> kafkaTemplate;
  private final UserRepository userRepository;
  private final NotificationRepository notificationRepository;
  private final MessageRepository messageRepository;
  private final ReadStatusRepository readStatusRepository;
  private final SseService sseService;

  @KafkaListener(topics = "discodeit.MessageCreatedEvent", groupId = "discodeit-group")
  @Caching(evict = {
      @CacheEvict(cacheNames = "channelsByUser", allEntries = true),
      @CacheEvict(cacheNames = "notificationsByUser", allEntries = true)
  })
  public void onMessageCreatedEvent(String kafkaEvent) throws JsonProcessingException {
    MessageCreatedEvent event = objectMapper.readValue(kafkaEvent, MessageCreatedEvent.class);
    Message message = messageRepository.findById(event.messageId()).orElse(null);
    if (message == null) {
      return;
    }

    Channel channel = message.getChannel();
    User author = message.getAuthor();
    List<ReadStatus> readStatuses =
        readStatusRepository.findAllByChannelIdAndNotificationEnabledTrue(channel.getId());

    for (ReadStatus readStatus : readStatuses) {
      User receiver = readStatus.getUser();
      if (receiver.getId().equals(author.getId())) {
        continue;
      }

      Notification notification = notificationRepository.save(new Notification(
          receiver,
          author.getUsername() + " (#" + channel.getName() + ")",
          message.getContent()
      ));

      publishNotificationCreated(List.of(receiver.getId()), toDto(notification));
    }
  }

  @KafkaListener(topics = "discodeit.RoleUpdatedEvent", groupId = "discodeit-group")
  @Caching(evict = {
      @CacheEvict(cacheNames = "notificationsByUser", allEntries = true),
      @CacheEvict(cacheNames = "users", allEntries = true)
  })
  public void onRoleUpdatedEvent(String kafkaEvent) throws JsonProcessingException {
    RoleUpdatedEvent event = objectMapper.readValue(kafkaEvent, RoleUpdatedEvent.class);
    User receiver = userRepository.findById(event.userId())
        .orElseThrow(() -> new UserNotFoundException(event.userId()));

    Notification notification = notificationRepository.save(new Notification(
        receiver,
        "Role changed",
        event.previousRole().name() + " -> " + event.newRole().name()
    ));

    publishNotificationCreated(List.of(receiver.getId()), toDto(notification));
  }

  @KafkaListener(topics = "discodeit.S3UploadFailedEvent", groupId = "discodeit-group")
  public void onS3UploadFailedEvent(String kafkaEvent) throws JsonProcessingException {
    S3UploadFailedEvent event = objectMapper.readValue(kafkaEvent, S3UploadFailedEvent.class);

    String content = """
        Task: S3 file upload failed
        RequestId: %s
        BinaryContentId: %s
        ErrorMessage: %s
        """.formatted(
        event.requestId(),
        event.binaryContentId(),
        event.errorMessage()
    );

    List<User> admins = userRepository.findAllByRole(Role.ADMIN);
    for (User admin : admins) {
      Notification notification = notificationRepository.save(new Notification(
          admin,
          "Task failed: S3 file upload",
          content
      ));

      publishNotificationCreated(List.of(admin.getId()), toDto(notification));
    }
  }

  @KafkaListener(
      topics = NOTIFICATION_CREATED_TOPIC,
      groupId = "realtime-sse-${discodeit.kafka.realtime-group-id}"
  )
  public void onNotificationCreatedEvent(String kafkaEvent) throws JsonProcessingException {
    NotificationCreatedEvent event = objectMapper.readValue(
        kafkaEvent,
        NotificationCreatedEvent.class
    );
    sseService.send(
        event.receiverIds(),
        NOTIFICATION_CREATED_EVENT_NAME,
        event.notification()
    );
  }

  private void publishNotificationCreated(List<java.util.UUID> receiverIds, NotificationDto dto)
      throws JsonProcessingException {
    NotificationCreatedEvent event = new NotificationCreatedEvent(receiverIds, dto);
    kafkaTemplate.send(NOTIFICATION_CREATED_TOPIC, objectMapper.writeValueAsString(event));
  }

  private NotificationDto toDto(Notification notification) {
    return new NotificationDto(
        notification.getId(),
        notification.getCreatedAt(),
        notification.getReceiver().getId(),
        notification.getTitle(),
        notification.getContent()
    );
  }
}
