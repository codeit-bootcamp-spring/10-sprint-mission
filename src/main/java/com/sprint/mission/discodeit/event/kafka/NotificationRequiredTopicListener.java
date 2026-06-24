package com.sprint.mission.discodeit.event.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.event.S3UploadFailedEvent;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.NotificationMapper;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.SseService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class NotificationRequiredTopicListener {

    private final ObjectMapper objectMapper;
    private final NotificationRepository notificationRepository;
    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final NotificationMapper notificationMapper;
    private final CacheManager cacheManager;
    private final SseService sseService;

    @KafkaListener(topics = "discodeit.MessageCreatedEvent")
    @Transactional
    public void onMessageCreatedEvent(String kafkaEvent) {
        MessageCreatedEvent event = read(kafkaEvent, MessageCreatedEvent.class);

        String channelName = event.channelName() == null ? "private" : event.channelName();
        String title = event.authorUsername() + " (#" + channelName + ")";

        List<User> receivers = readStatusRepository
                .findAllByChannelIdAndNotificationEnabledIsTrueWithUser(event.channelId())
                .stream()
                .map(ReadStatus::getUser)
                .filter(user -> !user.getId().equals(event.authorId()))
                .toList();

        List<Notification> notifications = receivers.stream()
                .map(user -> new Notification(user, title, event.content()))
                .toList();

        List<Notification> savedNotifications = notificationRepository.saveAll(notifications);
        receivers.forEach(user -> evictNotificationCache(user.getId()));
        savedNotifications.forEach(this::sendNotificationCreatedEvent);
    }

    @KafkaListener(topics = "discodeit.RoleUpdatedEvent")
    @Transactional
    public void onRoleUpdatedEvent(String kafkaEvent) {
        RoleUpdatedEvent event = read(kafkaEvent, RoleUpdatedEvent.class);

        User receiver = userRepository.findById(event.userId())
                .orElseThrow(() -> UserNotFoundException.withId(event.userId()));

        Notification notification = new Notification(
                receiver,
                "권한이 변경되었습니다.",
                event.oldRole() + " -> " + event.newRole()
        );

        Notification savedNotification = notificationRepository.save(notification);
        evictNotificationCache(receiver.getId());
        sendNotificationCreatedEvent(savedNotification);
    }

    @KafkaListener(topics = "discodeit.S3UploadFailedEvent")
    @Transactional
    public void onS3UploadFailedEvent(String kafkaEvent) {
        S3UploadFailedEvent event = read(kafkaEvent, S3UploadFailedEvent.class);

        String content = """
        Task: S3 파일 업로드
        RequestId: %s
        BinaryContentId: %s
        Error: %s
        """.formatted(event.requestId(), event.binaryContentId(), event.errorMessage());

        List<User> admins = userRepository.findAllByRole(Role.ADMIN);
        List<Notification> notifications = admins.stream()
                .map(admin -> new Notification(admin, "S3 파일 업로드 실패", content))
                .toList();

        List<Notification> savedNotifications = notificationRepository.saveAll(notifications);
        admins.forEach(admin -> evictNotificationCache(admin.getId()));
        savedNotifications.forEach(this::sendNotificationCreatedEvent);
    }

    private <T> T read(String kafkaEvent, Class<T> type) {
        try {
            return objectMapper.readValue(kafkaEvent, type);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Kafka 이벤트 역직렬화 실패", e);
        }
    }

    private void evictNotificationCache(UUID receiverId) {
        Cache cache = cacheManager.getCache("notifications");
        if (cache != null) {
            cache.evict(receiverId);
        }
    }

    private void sendNotificationCreatedEvent(Notification notification) {
        NotificationDto dto = notificationMapper.toDto(notification);
        sseService.send(
                List.of(notification.getReceiver().getId()),
                "notifications.created",
                dto
        );
    }
}
