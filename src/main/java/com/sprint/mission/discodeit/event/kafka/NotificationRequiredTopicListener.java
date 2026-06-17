package com.sprint.mission.discodeit.event.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.event.BinaryContentUploadFailedEvent;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.basic.SseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

/// 여기는 따로 알림서비스 프로젝트를 만들어서 분리되는 부분이다.
/// discodeit 서비스에서 Kafka로 알림서비스로 event객체 넘겨주고
/// 알림 서비스에서는 event객체를 받아서 event객체 안에있는 receiverIds, title, content
/// 이런 내용들을 이용해서 진짜 알림만 만드는 작업을 수행한다.
/// DB내의 Notification테이블도 알림서비스쪽으로 분리된다.
///
/// userNotifications캐시도 알림서비스의 책임으로 넘어간다.
/// 현재는 discodeit에서 redis를 이용해서 userNotifications 캐시를 만들고 있는데
/// discodeit에서 만들도록하지않고 알림서비스에서 만들도록 위임한다.
/// Redis는 공유하되, prefix를 달리하여 캐시key가 충돌하지 않게 분리해야한다.
///
///
/// 현재는 Kafka를 찍먹목적으로 discodeit 서비스 내에서 보내고 받고를 처리하도록했다.
@Slf4j
@RequiredArgsConstructor
@Component
public class NotificationRequiredTopicListener {

    private final ObjectMapper objectMapper;
    private final CacheManager cacheManager;

    private final ReadStatusRepository readStatusRepository;
    private final MessageRepository messageRepository;
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    private final SseService sseService;

    @KafkaListener(topics = "discodeit.MessageCreatedEvent")
    public void onMessageCreatedEvent(String kafkaEvent) {
        try {
            MessageCreatedEvent event = objectMapper.readValue(kafkaEvent, MessageCreatedEvent.class);

            /// 사용자 알림목록 캐시
            /// 알림 서비스 책임이 된다.
            Cache cache = cacheManager.getCache("userNotifications");

            /// 해당 채널의 알림 여부를 활성화한 ReadStatus를 조회합니다.
            List<ReadStatus> readStatuses = readStatusRepository.findAllByChannelIdAndNotificationEnabledTrue(event.getChannelId());

            Message message = messageRepository.findByIdWithAuthorAndChannel(event.getMessageId())
                    .orElseThrow(() -> MessageNotFoundException.withId(event.getMessageId()));

            /// 해당 ReadStatus의 사용자들에게 알림을 생성합니다.
            List<Notification> notifications = readStatuses.stream()
                    /// 자기자신이 보낸 message는 알림이 안울려야하므로 필터링.
                    .filter(readStatus -> !readStatus.getUser().getId().equals(message.getAuthor().getId()))
                    .map(readStatus -> new Notification(
                            readStatus.getUser(), // 받는 사람
                            "%s (#%s)".formatted( // 제목
                                    message.getAuthor().getUsername(),
                                    message.getChannel().getName()
                            ),
                            message.getContent()  // 내용
                    ))
                    .toList();

            List<Notification> savedNotifications = notificationRepository.saveAll(notifications);

            savedNotifications.forEach(notification -> {
                NotificationDto notificationDto = new NotificationDto(
                        notification.getId(),
                        notification.getCreatedAt(),
                        notification.getReceiver().getId(),
                        notification.getTitle(),
                        notification.getContent()
                );

                /// SSE에 연결돼있는
                sseService.send(
                        List.of(notification.getReceiver().getId()),
                                "notifications.created",
                                notificationDto
                );
            });

            /// 알림이 발생한 사용자의 알림을 캐시에서 삭제
            if (cache != null) {
                notifications.stream()
                        .map(notification -> notification.getReceiver().getId())
                        .distinct()
                        .forEach(cache::evict);
            }
            log.debug("메시지 생성 알림 저장 완료: messageId={}, channelId={}, count={}",
                    event.getMessageId(), event.getChannelId(), notifications.size());

        }catch (JsonProcessingException e) {
            log.error("Kafka 이벤트 역직렬화 실패. payload={}", kafkaEvent, e);
            /// Spring Kafka의 Consumer ErrorHandler로 넘어간다.

            /// Controller 예외 → GlobalExceptionHandler → ErrorResponse
            /// KafkaListener 예외 → Kafka ErrorHandler → retry/DLT/log
            throw new RuntimeException(e);
        }
    }

    @KafkaListener(topics = "discodeit.RoleUpdatedEvent")
    public void onRoleUpdatedEvent(String kafkaEvent) {
        try {
            RoleUpdatedEvent event = objectMapper.readValue(kafkaEvent, RoleUpdatedEvent.class);

            Cache cache = cacheManager.getCache("userNotifications");

            User receiver = userRepository.findById(event.getUserId())
                    .orElseThrow(() -> UserNotFoundException.withId(event.getUserId()));

            Notification notification = new Notification(
                    receiver,
                    "권한이 변경되었습니다.",
                    "%s -> %s".formatted(event.getPreviousRole(), event.getNewRole())
            );

            Notification savedNotification = notificationRepository.save(notification);
            NotificationDto notificationDto = new NotificationDto(
                    savedNotification.getId(),
                    savedNotification.getCreatedAt(),
                    savedNotification.getReceiver().getId(),
                    savedNotification.getTitle(),
                    savedNotification.getContent()
            );

            /// SSE에 연결돼있는
            sseService.send(
                    List.of(savedNotification.getReceiver().getId()),
                    "notifications.created",
                    notificationDto
            );

            if (cache != null) {
                cache.evict(event.getUserId());
            }
            log.debug("권한 변경 알림 저장 완료: userId={}, previousRole={}, newRole={}",
                    event.getUserId(), event.getPreviousRole(), event.getNewRole());
        }catch (JsonProcessingException e) {
            log.error("Kafka 이벤트 역직렬화 실패. payload={}", kafkaEvent, e);
            throw new RuntimeException(e);

        }

    }

    @KafkaListener(topics = "discodeit.BinaryContentUploadFailedEvent")
    public void onS3UploadFailedEvent(String kafkaEvent) {
        try {
            BinaryContentUploadFailedEvent event = objectMapper.readValue(kafkaEvent, BinaryContentUploadFailedEvent.class);
            Cache cache = cacheManager.getCache("userNotifications");
            List<User> admins = userRepository.findAllByRole(Role.ADMIN);

            String title = "S3 바이너리 데이터 업로드 실패";
            String content = """
      Task: %s
      RequestId: %s
      BinaryContentId: %s
      Error: %s
      """.formatted(
                    event.getTaskName(),
                    event.getRequestId(),
                    event.getBinaryContentId(),
                    event.getErrorMessage()
            );

            List<Notification> notifications = admins.stream()
                    .map(admin -> new Notification(admin, title, content))
                    .toList();

            List<Notification> savedNotifications = notificationRepository.saveAll(notifications);

            savedNotifications.forEach(notification -> {
                NotificationDto notificationDto = new NotificationDto(
                        notification.getId(),
                        notification.getCreatedAt(),
                        notification.getReceiver().getId(),
                        notification.getTitle(),
                        notification.getContent()
                );

                /// SSE에 연결돼있는
                sseService.send(
                        List.of(notification.getReceiver().getId()),
                        "notifications.created",
                        notificationDto
                );
            });


            if (cache != null) {
                admins.stream()
                        .map(User::getId)
                        .distinct()
                        .forEach(cache::evict);
            }
            log.error("S3업로드 실패로 관리자에게 알림. binaryContentId={}, adminCount={}",
                    event.getBinaryContentId(), admins.size());
        }catch (JsonProcessingException e) {
            log.error("Kafka 이벤트 역직렬화 실패. payload={}", kafkaEvent, e);
            throw new RuntimeException(e);

        }

    }
}
