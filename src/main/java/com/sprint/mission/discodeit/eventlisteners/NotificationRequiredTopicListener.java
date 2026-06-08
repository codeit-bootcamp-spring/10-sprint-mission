package com.sprint.mission.discodeit.eventlisteners;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.enums.Role;
import com.sprint.mission.discodeit.events.MessageCreatedEvent;
import com.sprint.mission.discodeit.events.RoleUpdatedEvent;
import com.sprint.mission.discodeit.events.S3UploadFailedEvent;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class NotificationRequiredTopicListener {

  private final ObjectMapper objectMapper;
  private final UserRepository userRepository;
  private final NotificationRepository notificationRepository;
  private final MessageRepository messageRepository;
  private final ReadStatusRepository readStatusRepository;

  @KafkaListener(topics = "discodeit.MessageCreatedEvent", groupId = "discodeit-group")
  public void onMessageCreatedEvent(String kafkaEvent) {
    MessageCreatedEvent event;

    // String의 kafkaEvent를 MessageCreatedEvent로 역직렬화
    try {
      event = objectMapper.readValue(kafkaEvent,
          MessageCreatedEvent.class);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }

    Optional<Message> optMessage = messageRepository.findById(event.messageId());
    Message message = optMessage.orElse(null); // 메시지
    Channel channel = Objects.requireNonNull(message).getChannel(); // 해당 채널
    List<ReadStatus> readStatusList = readStatusRepository
        .findAllByChannelIdAndNotificationEnabledTrue(channel.getId()); // 채널의 사용자 읽기 정보들
    User author = message.getAuthor(); // 메시지 작성자

    // 읽기 상태 리스트 순회
    for (ReadStatus rs : readStatusList) {
      User receiver = rs.getUser(); // 알림 수신 대상자들
      if (receiver.getId().equals(author.getId())) {
        continue; // 메시지 작성자와 알림 수신 대상자가 같으면 무시
      }

      notificationRepository.save(new Notification(
          rs.getUser(),
          author.getUsername() + " (#" + channel.getName() + ")",
          message.getContent()
      ));
    }
  }

  @KafkaListener(topics = "discodeit.RoleUpdatedEvent", groupId = "discodeit-group")
  public void onRoleUpdatedEvent(String kafkaEvent) throws JsonProcessingException {
    RoleUpdatedEvent event;
    try {
      // String으로 전달받은 event를 객체로 역직렬화
      event = objectMapper.readValue(kafkaEvent, RoleUpdatedEvent.class);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }

    // 알림 수신 대상자 (권한이 변경된 사용자)
    User receiver = userRepository.findById(event.userId())
        .orElseThrow(() -> new UserNotFoundException(event.userId()));

    // 알림 객체 생성
    notificationRepository.save(new Notification(
        receiver,
        "권한이 변경되었습니다.",
        event.previousRole().name() + " -> " + event.newRole().name()
    ));

  }

  @KafkaListener(topics = "discodeit.S3UploadFailedEvent", groupId = "discodeit-group")
  public void onS3UploadFailedEvent(String kafkaEvent) {
    S3UploadFailedEvent event;

    // 역직렬화
    try {
      event = objectMapper.readValue(kafkaEvent, S3UploadFailedEvent.class);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }

    String content = """
        Task: %s
        RequestId: %s
        BinaryContentId: %s
        """.formatted(
        "S3 파일 업로드 실패",
        MDC.get("requestId"),
        event.binaryContentId()
    );

    // 권한이 ADMIN 유저를 리스트 형식으로 뽑고
    List<User> admins = userRepository.findAllByRole(Role.ADMIN);
    // 유저 리스트를 순회하면서 알림 객체 생성 및 저장
    for (User admin : admins) {
      notificationRepository.save(new Notification(
          admin,
          "작업 실패: " + "S3 파일 업로드 실패",
          content
      ));
    }


  }
}

