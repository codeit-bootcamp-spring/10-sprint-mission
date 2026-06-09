package com.sprint.mission.discodeit.event.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.event.S3UploadFailedEvent;
import com.sprint.mission.discodeit.event.kafka.dto.MessageCreatedKafkaEvent;
import com.sprint.mission.discodeit.event.kafka.dto.RoleUpdatedKafkaEvent;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationRequiredTopicListener {

    private static final String MESSAGE_CREATED_TOPIC = "discodeit.MessageCreatedEvent";
    private static final String ROLE_UPDATED_TOPIC = "discodeit.RoleUpdatedEvent";
    private static final String S3_UPLOAD_FAILED_TOPIC =
            "discodeit.S3UploadFailedEvent";

    private final ObjectMapper objectMapper;
    private final ReadStatusRepository readStatusRepository;
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Value("${discodeit.admin.username}")
    private String adminUsername;

    @Transactional
    @CacheEvict(value = "notifications", allEntries = true)
    @KafkaListener(topics = MESSAGE_CREATED_TOPIC)
    public void onMessageCreatedEvent(String kafkaEvent) {
        try {
            MessageCreatedKafkaEvent event = objectMapper.readValue(
                    kafkaEvent,
                    MessageCreatedKafkaEvent.class
            );

            List<ReadStatus> readStatuses =
                    readStatusRepository.findByChannelIdAndNotificationEnabledTrue(event.channelId());

            List<Notification> notifications = readStatuses.stream()
                    .filter(readStatus -> !readStatus.getUser().getId().equals(event.senderId()))
                    .map(readStatus -> new Notification(
                            readStatus.getUser(),
                            event.senderUsername() + " (#" + event.channelName() + ")",
                            event.content()
                    ))
                    .toList();

            notificationRepository.saveAll(notifications);

            log.info("[KAFKA_NOTIFICATION_CREATE_SUCCESS] topic={}, channelId={}, senderId={}, targetCount={}",
                    MESSAGE_CREATED_TOPIC,
                    event.channelId(),
                    event.senderId(),
                    notifications.size()
            );

        } catch (JsonProcessingException e) {
            log.error("[KAFKA_EVENT_CONVERT_FAIL] topic={}, payload={}",
                    MESSAGE_CREATED_TOPIC,
                    kafkaEvent,
                    e
            );
            throw new RuntimeException(e);
        }
    }

    @Transactional
    @CacheEvict(value = "notifications", allEntries = true)
    @KafkaListener(topics = ROLE_UPDATED_TOPIC)
    public void onRoleUpdatedEvent(String kafkaEvent) {
        try {
            RoleUpdatedKafkaEvent event = objectMapper.readValue(
                    kafkaEvent,
                    RoleUpdatedKafkaEvent.class
            );

            User user = userRepository.findById(event.userId())
                    .orElseThrow(() -> new UserNotFoundException(event.userId()));

            String title = "권한이 변경되었습니다.";
            String content = event.oldRole() + " -> " + event.newRole();

            Notification notification = new Notification(
                    user,
                    title,
                    content
            );

            notificationRepository.save(notification);

            log.info("[KAFKA_NOTIFICATION_CREATE_SUCCESS] topic={}, userId={}, oldRole={}, newRole={}",
                    ROLE_UPDATED_TOPIC,
                    event.userId(),
                    event.oldRole(),
                    event.newRole()
            );

        } catch (JsonProcessingException e) {
            log.error("[KAFKA_EVENT_CONVERT_FAIL] topic={}, payload={}",
                    ROLE_UPDATED_TOPIC,
                    kafkaEvent,
                    e
            );
            throw new RuntimeException(e);
        }
    }

    @Transactional
    @CacheEvict(value = "notifications", allEntries = true)
    @KafkaListener(topics = S3_UPLOAD_FAILED_TOPIC)
    public void onS3UploadFailedEvent(String kafkaEvent) {
        try {
            S3UploadFailedEvent event = objectMapper.readValue(
                    kafkaEvent,
                    S3UploadFailedEvent.class
            );

            User admin = userRepository.findByUsername(adminUsername)
                    .orElseThrow(() -> new UserNotFoundException(adminUsername));

            String title = "S3 바이너리 저장 실패";
            String content = """
                작업: S3_BINARYCONTENT_SAVE
                RequestId: %s
                BinaryContentId: %s
                Error: %s
                """.formatted(
                    event.requestId(),
                    event.binaryContentId(),
                    event.errorMessage()
            );

            Notification notification = new Notification(
                    admin,
                    title,
                    content
            );

            notificationRepository.save(notification);

            log.info("[KAFKA_NOTIFICATION_CREATE_SUCCESS] topic={}, adminId={}, binaryContentId={}",
                    S3_UPLOAD_FAILED_TOPIC,
                    admin.getId(),
                    event.binaryContentId()
            );

        } catch (JsonProcessingException e) {
            log.error("[KAFKA_EVENT_CONVERT_FAIL] topic={}, payload={}",
                    S3_UPLOAD_FAILED_TOPIC,
                    kafkaEvent,
                    e
            );
            throw new RuntimeException(e);
        }
    }
}