package com.sprint.mission.discodeit.event.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.event.S3UploadFailedEvent;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationRequiredTopicListener {

    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = KafkaTopics.MESSAGE_CREATED, groupId = "discodeit-group")
    public void onMessageCreatedEvent(String payload) throws Exception {
        MessageCreatedEvent event = objectMapper.readValue(payload, MessageCreatedEvent.class);
        readStatusRepository.findAllByChannelId(event.channelId()).stream()
                .filter(ReadStatus::isNotificationEnabled)
                .map(readStatus -> readStatus.getUser().getId())
                // 메시지를 보낸 사람은 알림 대상에서 제외
                .filter(receiverId -> !receiverId.equals(event.senderId()))
                .forEach(receiverId -> notificationService.create(
                        receiverId,
                        event.senderName() + " (#" + event.channelName() + ")",
                        event.content()));
    }

    @KafkaListener(topics = KafkaTopics.ROLE_UPDATED, groupId = "discodeit-group")
    public void onRoleUpdatedEvent(String payload) throws Exception {
        RoleUpdatedEvent event = objectMapper.readValue(payload, RoleUpdatedEvent.class);
        notificationService.create(
                event.userId(),
                "권한이 변경되었습니다.",
                event.previousRole() + " -> " + event.newRole());
    }

    @KafkaListener(topics = KafkaTopics.S3_UPLOAD_FAILED, groupId = "discodeit-group")
    public void onS3UploadFailedEvent(String payload) throws Exception {
        S3UploadFailedEvent event = objectMapper.readValue(payload, S3UploadFailedEvent.class);
        List<User> admins = userRepository.findAllByRole(Role.ADMIN);
        if (admins.isEmpty()) {
            log.warn("바이너리 저장 실패 알림을 받을 ADMIN 계정이 없습니다. contentId={}", event.binaryContentId());
            return;
        }
        String content = """
                Task: %s
                RequestId: %s
                BinaryContentId: %s
                Error: %s
                """.formatted(event.task(), event.requestId(), event.binaryContentId(), event.errorMessage());
        admins.forEach(admin -> notificationService.create(admin.getId(), "바이너리 저장 실패", content));
    }
}
