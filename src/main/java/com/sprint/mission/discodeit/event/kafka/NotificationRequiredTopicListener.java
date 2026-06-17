package com.sprint.mission.discodeit.event.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.entity.UserRole;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/*
Kafka 토픽의 이벤트를 소비(consume)하여 실제로 알림을 생성하는 Consumer 컴포넌트

# 전체 흐름
도메인 로직 (메시지 작성, 권한 변경, S3 업로드 실패)
  -> ApplicationEvent 발행
  -> KafkaProduceRequiredEventListener 가 이벤트를 JSON으로 직렬화하여 Kafka 토픽에 produce
  -> Kafka 토픽을 consume하여 JSON 역직렬화 후 알림 생성

# kafka 사용 이유
- 알림 생성을 원래 트랜잭션과 비동기로 분리하여 응답 지연 줄이기
- Kafka가 메시지를 보관하므로, 컨슈머가 잠시 죽어도 재기동 시 못 받은 이벤틀를 다시 다시 처리 가능
- 알림 처리량이 늘어나면 컨슈머만 수평 확장 가능
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationRequiredTopicListener {

  private final ReadStatusRepository readStatusRepository;
  private final NotificationService notificationService;
  private final UserRepository userRepository;
  private final ObjectMapper objectMapper;

  // 메시지 생성 이벤트를 처리
  // @KafkaListener : Spring이 백그라운드에서 계속 토픽을 폴링(poll)하다가 메시지가 오면 자동으로 메서드를 호출해줌
  @KafkaListener(topics = "discodeit.MessageCreatedEvent") // 이 topic으로 들어오는 레코드를 구도
  @Transactional
  public void onMessageCreatedEvent(String kafkaEvent) throws JsonProcessingException {
    // JSON 문자열을 이벤트 객체로 역직렬화
    MessageCreatedEvent event = objectMapper.readValue(kafkaEvent, MessageCreatedEvent.class);
    // 알림 제목에 표시할 채널 이름 구성
    String channelDisplay = event.channelName() != null ? "#" + event.channelName() : "비공개 채널";
    // 알림 제목
    String title = event.authorUsername() + " (#" + channelDisplay + ")";
    // 해당 채널에서 알림이 켜져 있는 readStatus 목록을 조회한 뒤
    readStatusRepository.findAllByChannelIdAndNotificationEnabled(event.channelId()).stream()
        // 메지시를 작성한 본인은 알림 대상에서 제외
        .filter(rs -> !rs.getUser().getId().equals(event.authorId()))
        // 남은 각 사용자에게 content를 본문으로 하는 알림을 생성
        .forEach(rs -> notificationService.create(rs.getUser().getId(), title, event.content()));
  }

  // 권한 변경 이벤트 처리
  @KafkaListener(topics = "discodeit.RoleUpdatedEvent")
  @Transactional
  public void onRoleUpdatedEvent(String kafkaEvent) throws JsonProcessingException {
    RoleUpdatedEvent event = objectMapper.readValue(kafkaEvent, RoleUpdatedEvent.class);
    String content = event.oldRole().name() + " -> " + event.newRole().name();
    notificationService.create(event.userId(), "권한이 변경되었습니다.", content);
  }

  // S3 업로드 실패 이벤트 처리
  @KafkaListener(topics = "discodeit.S3UploadFailedEvent")
  @Transactional
  public void onS3UploadFailedEvent(String kafkaEvent) throws JsonProcessingException {
    S3UploadFailedKafkaPayload p = objectMapper.readValue(kafkaEvent,
        S3UploadFailedKafkaPayload.class);
    String title = "[관리자 알림] S3 파일 업로드 실패";
    String content = String.format(
        "작업: %s%nRequestId: %s%nBinaryContentId: %s%nError: %s",
        "S3 파일 업로드", p.requestId(), p.binaryContentId(), p.reason());
    userRepository.findAllByRole(UserRole.ADMIN)
        .forEach(admin -> notificationService.create(admin.getId(), title, content));
  }
}
