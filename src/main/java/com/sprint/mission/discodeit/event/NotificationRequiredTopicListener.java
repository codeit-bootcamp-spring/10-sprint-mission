package com.sprint.mission.discodeit.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationRequiredTopicListener {

  private final ObjectMapper objectMapper;
  private final MessageRepository messageRepository;
  private final ReadStatusRepository readStatusRepository;
  private final UserRepository userRepository;
  private final NotificationService notificationService;

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  @KafkaListener(topics = "discodeit.MessageCreatedEvent")
  public void onMessageCreatedEvent(String kafkaEvent) {
    try {
      MessageCreatedEvent event = objectMapper.readValue(kafkaEvent, MessageCreatedEvent.class);

      Message message =
          messageRepository
              .findById(event.messageId())
              .orElseThrow(() -> MessageNotFoundException.withId(event.messageId()));

      UUID channelId = message.getChannel().getId();
      UUID authorId = message.getAuthor().getId();

      List<ReadStatus> readStatuses =
          readStatusRepository.findAllNotificationEnabledByChannelId(channelId);

      String channelName = message.getChannel().getName();
      String authorName = message.getAuthor().getUsername();

      String title = authorName + " (#CHANNEL: " + channelName + ")";
      String content = message.getContent();

      for (ReadStatus readStatus : readStatuses) {
        UUID receiverId = readStatus.getUser().getId();

        // 메시지를 보낸 사람에게는 자기 메시지 알림 X
        if (receiverId.equals(authorId)) {
          continue;
        }

        notificationService.create(receiverId, title, content);
      }

      log.debug("Kafka 메시지 생성 이벤트 처리 완료: messageId={}", event.messageId());
    } catch (JsonProcessingException e) {
      throw new RuntimeException("MessageCreatedEvent 역직렬화 실패", e);
    }
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  @KafkaListener(topics = "discodeit.RoleUpdatedEvent")
  public void onRoleUpdatedEvent(String kafkaEvent) {
    try {
      RoleUpdatedEvent event = objectMapper.readValue(kafkaEvent, RoleUpdatedEvent.class);

      String title = "권한이 변경되었습니다.";
      String content = event.previousRole() + " -> " + event.newRole();

      notificationService.create(event.receiverId(), title, content);

      log.debug("Kafka 권한 변경 이벤트 처리 완료: receiverId={}", event.receiverId());
    } catch (JsonProcessingException e) {
      throw new RuntimeException("RoleUpdatedEvent 역직렬화 실패", e);
    }
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  @KafkaListener(topics = "discodeit.S3UploadFailedEvent")
  public void onS3UploadFailedEvent(String kafkaEvent) {
    try {
      S3UploadFailedEvent event = objectMapper.readValue(kafkaEvent, S3UploadFailedEvent.class);

      String title = "S3 파일 업로드 실패";
      String content =
          """
                TaskName: %s
                RequestId: %s
                BinaryContentId: %s
                Error: %s
                """
              .formatted(
                  event.taskName(),
                  event.requestId(),
                  event.binaryContentId(),
                  event.errorMessage());

      List<User> admins = userRepository.findAllByRole(Role.ADMIN);

      for (User admin : admins) {
        notificationService.create(admin.getId(), title, content);
      }

      log.debug("Kafka S3 업로드 실패 이벤트 처리 완료: binaryContentId={}", event.binaryContentId());
    } catch (JsonProcessingException e) {
      throw new RuntimeException("S3UploadFailedEvent 역직렬화 실패", e);
    }
  }
}
