package com.sprint.mission.discodeit.notification.application.handler;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.sprint.mission.discodeit.binarycontent.domain.event.UploadFailedNotificationEvent;
import com.sprint.mission.discodeit.message.domain.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.notification.application.service.NotificationService;
import com.sprint.mission.discodeit.notification.domain.provider.NotificationChannelResolver;
import com.sprint.mission.discodeit.notification.domain.provider.NotificationReadStatusResolver;
import com.sprint.mission.discodeit.notification.domain.provider.NotificationUserResolver;
import com.sprint.mission.discodeit.user.domain.entity.constant.UserRole;
import com.sprint.mission.discodeit.user.domain.event.UserRoleUpdateEvent;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NotificationRequiredEventListenerTest {

  @Mock
  private NotificationService service;

  @Mock
  private NotificationUserResolver userResolver;

  @Mock
  private NotificationChannelResolver channelResolver;

  @Mock
  private NotificationReadStatusResolver readStatusResolver;

  @InjectMocks
  private NotificationRequiredEventListener listener;

  @Test
  @DisplayName("on(UserRoleUpdateEvent) - 권한 변경 이벤트 시 알림을 생성한다.")
  void onUserRoleUpdateEvent() {
    UUID userId = UUID.randomUUID();
    UserRoleUpdateEvent event = new UserRoleUpdateEvent(userId, UserRole.USER, UserRole.ADMIN);

    listener.on(event);

    verify(service).create(List.of(userId), "role updated", "USER -> ADMIN");
  }

  @Test
  @DisplayName("on(MessageCreatedEvent) - 메시지 생성 이벤트 시 알림을 활성화한 유저들에게 알림을 생성한다.")
  void onMessageCreatedEvent() {
    UUID messageId = UUID.randomUUID();
    UUID senderId = UUID.randomUUID();
    UUID channelId = UUID.randomUUID();
    MessageCreatedEvent event = new MessageCreatedEvent(senderId, channelId, "Hello World");

    given(userResolver.getUsername(senderId)).willReturn("testuser");
    given(channelResolver.getChannelName(channelId)).willReturn("general");
    UUID receiverId = UUID.randomUUID();
    given(readStatusResolver.findUserIdsByChannelIdAndNotificationEnabledIsTrue(channelId))
        .willReturn(List.of(receiverId));

    listener.on(event);

    verify(service).create(List.of(receiverId), "testuser (#general)", "Hello World");
  }

  @Test
  @DisplayName("on(UploadFailedNotificationEvent) - 업로드 실패 이벤트 시 어드민에게 메시지를 전송한다.")
  void onUploadFailedNotificationEvent() {
    UploadFailedNotificationEvent event = new UploadFailedNotificationEvent("Upload Failed", List.of("err1", "err2"));

    listener.on(event);

    verify(service).sendToAdmin("Upload Failed", List.of("err1", "err2"));
  }
}
