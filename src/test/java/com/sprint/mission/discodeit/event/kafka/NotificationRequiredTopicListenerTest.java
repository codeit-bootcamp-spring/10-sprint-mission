package com.sprint.mission.discodeit.event.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.event.S3UploadFailedEvent;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotificationRequiredTopicListenerTest {

    @Mock private ReadStatusRepository readStatusRepository;
    @Mock private UserRepository userRepository;
    @Mock private NotificationService notificationService;

    // payload JSON -> 역직렬화 경로를 실제로 검증하기 위해 실제 ObjectMapper 사용
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
    private NotificationRequiredTopicListener listener;

    @BeforeEach
    void setUp() {
        listener = new NotificationRequiredTopicListener(
                readStatusRepository, userRepository, notificationService, objectMapper);
    }

    private User userWithId(String name) {
        User user = new User(name, name + "@test.com", "pw");
        ReflectionTestUtils.setField(user, "id", UUID.randomUUID());
        return user;
    }

    private ReadStatus readStatus(User user, Channel channel, boolean enabled) {
        ReadStatus readStatus = new ReadStatus(user, channel);
        readStatus.updateNotificationEnabled(enabled);
        return readStatus;
    }

    @Test
    @DisplayName("MessageCreatedEvent: 알림 ON 사용자에게만, 발신자는 제외하고 알림 생성")
    void onMessageCreatedEvent_onlyEnabledExcludingSender() throws Exception {
        Channel channel = new Channel(ChannelType.PUBLIC, "general", "desc");
        UUID channelId = UUID.randomUUID();

        User sender = userWithId("Alice");
        User receiverOn = userWithId("Bob");
        User receiverOff = userWithId("Carol");

        given(readStatusRepository.findAllByChannelId(channelId)).willReturn(List.of(
                readStatus(sender, channel, true),       // 발신자(알림 ON) -> 제외돼야 함
                readStatus(receiverOn, channel, true),   // 알림 ON -> 수신
                readStatus(receiverOff, channel, false)  // 알림 OFF -> 미수신
        ));

        MessageCreatedEvent event =
                new MessageCreatedEvent(java.util.UUID.randomUUID(), channelId, "general",
                        sender.getId(), "Alice", "hello", java.time.Instant.now());
        String payload = objectMapper.writeValueAsString(event);

        listener.onMessageCreatedEvent(payload);

        verify(notificationService).create(receiverOn.getId(), "Alice (#general)", "hello");
        verify(notificationService, never()).create(eq(receiverOff.getId()), any(), any());
        verify(notificationService, never()).create(eq(sender.getId()), any(), any());
        verify(notificationService, times(1)).create(any(), any(), any());
    }

    @Test
    @DisplayName("RoleUpdatedEvent: 권한 변경 당사자에게 알림 생성")
    void onRoleUpdatedEvent_notifiesTarget() throws Exception {
        UUID userId = UUID.randomUUID();
        RoleUpdatedEvent event = new RoleUpdatedEvent(userId, Role.USER, Role.CHANNEL_MANAGER);
        String payload = objectMapper.writeValueAsString(event);

        listener.onRoleUpdatedEvent(payload);

        verify(notificationService).create(userId, "권한이 변경되었습니다.", "USER -> CHANNEL_MANAGER");
    }

    @Test
    @DisplayName("S3UploadFailedEvent: 모든 ADMIN에게 실패 알림 생성")
    void onS3UploadFailedEvent_notifiesAllAdmins() throws Exception {
        User admin1 = userWithId("admin1");
        User admin2 = userWithId("admin2");
        given(userRepository.findAllByRole(Role.ADMIN)).willReturn(List.of(admin1, admin2));

        UUID contentId = UUID.randomUUID();
        S3UploadFailedEvent event =
                new S3UploadFailedEvent(contentId, "binaryContentStore", "req-1", "boom");
        String payload = objectMapper.writeValueAsString(event);

        listener.onS3UploadFailedEvent(payload);

        verify(notificationService).create(eq(admin1.getId()), eq("바이너리 저장 실패"), any());
        verify(notificationService).create(eq(admin2.getId()), eq("바이너리 저장 실패"), any());
        verify(notificationService, times(2)).create(any(), any(), any());
    }

    @Test
    @DisplayName("S3UploadFailedEvent: ADMIN이 없으면 알림을 생성하지 않는다")
    void onS3UploadFailedEvent_noAdmins() throws Exception {
        given(userRepository.findAllByRole(Role.ADMIN)).willReturn(List.of());

        S3UploadFailedEvent event =
                new S3UploadFailedEvent(UUID.randomUUID(), "binaryContentStore", "req", "err");
        String payload = objectMapper.writeValueAsString(event);

        listener.onS3UploadFailedEvent(payload);

        verify(notificationService, never()).create(any(), any(), any());
    }
}
