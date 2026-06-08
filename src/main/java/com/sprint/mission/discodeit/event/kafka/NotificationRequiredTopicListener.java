package com.sprint.mission.discodeit.event.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    private final ObjectMapper objectMapper;
    private final ReadStatusRepository readStatusRepository;
    private final NotificationRepository notificationRepository;

    @Transactional
    @CacheEvict(value = "notifications", allEntries = true)
    @KafkaListener(topics = MESSAGE_CREATED_TOPIC)
    public void onMessageCreatedEvent(String kafkaEvent) {
        try {
            MessageCreatedEvent event = objectMapper.readValue(
                    kafkaEvent,
                    MessageCreatedEvent.class
            );

            Message message = event.message();
            Channel channel = message.getChannel();
            User sender = message.getAuthor();

            List<ReadStatus> readStatuses =
                    readStatusRepository.findByChannelAndNotificationEnabledTrue(channel);

            List<Notification> notifications = readStatuses.stream()
                    .filter(readStatus -> !readStatus.getUser().getId().equals(sender.getId()))
                    .map(readStatus -> new Notification(
                            readStatus.getUser(),
                            sender.getUsername() + " (#" + channel.getName() +")",
                            message.getContent()
                    ))
                    .toList();
            notificationRepository.saveAll(notifications);

            log.info("[KAFKA_NOTIFICATION_CREATE_SUCCESS] topic={}, channelId={}, senderId={}, targetCount={}",
                    MESSAGE_CREATED_TOPIC,
                    channel.getId(),
                    sender.getId(),
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
            RoleUpdatedEvent event = objectMapper.readValue(
                    kafkaEvent,
                    RoleUpdatedEvent.class
            );

            User user = event.user();
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
                    user.getId(),
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
}
