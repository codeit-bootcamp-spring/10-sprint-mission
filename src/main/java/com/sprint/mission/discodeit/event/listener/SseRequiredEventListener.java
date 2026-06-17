package com.sprint.mission.discodeit.event.listener;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.event.message.BinaryContentUpdatedEvent;
import com.sprint.mission.discodeit.event.message.ChannelCreatedEvent;
import com.sprint.mission.discodeit.event.message.ChannelDeletedEvent;
import com.sprint.mission.discodeit.event.message.ChannelUpdatedEvent;
import com.sprint.mission.discodeit.event.message.NotificationCreatedEvent;
import com.sprint.mission.discodeit.event.message.UserCreatedEvent;
import com.sprint.mission.discodeit.event.message.UserDeletedEvent;
import com.sprint.mission.discodeit.event.message.UserUpdatedEvent;
import com.sprint.mission.discodeit.sse.SseService;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class SseRequiredEventListener {

  private final SseService sseService;

  // Notification
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void on(NotificationCreatedEvent event) {
    NotificationDto notification = event.getData();

    sseService.send(Set.of(notification.receiverId()), "notifications.created", notification);

    log.debug(
        "SSE 알림 생성 이벤트 전송 완료: notificationId={}, receiverId={}",
        notification.id(),
        notification.receiverId());
  }

  // Binarycontent
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void on(BinaryContentUpdatedEvent event) {
    BinaryContentDto binaryContent = event.getTo();

    sseService.broadcast("binaryContents.updated", binaryContent);

    log.debug(
        "SSE 파일 상태 변경 이벤트 전송 완료: binaryContentId={}, status={}",
        binaryContent.id(),
        binaryContent.status());
  }

  // Channels
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void on(ChannelCreatedEvent event) {
    ChannelDto channel = event.getData();

    sendChannelEvent("channels.created", channel);

    log.debug("SSE 채널 생성 이벤트 전송 완료: channelId={}", channel.id());
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void on(ChannelUpdatedEvent event) {
    ChannelDto channel = event.getTo();

    sendChannelEvent("channels.updated", channel);

    log.debug("SSE 채널 수정 이벤트 전송 완료: channelId={}", channel.id());
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void on(ChannelDeletedEvent event) {
    ChannelDto channel = event.getData();

    sendChannelEvent("channels.deleted", channel);

    log.debug("SSE 채널 삭제 이벤트 전송 완료: channelId={}", channel.id());
  }

  private void sendChannelEvent(String eventName, ChannelDto channel) {
    if (channel.type() == ChannelType.PUBLIC) {
      sseService.broadcast(eventName, channel);
      return;
    }

    Set<UUID> receiverIds =
        channel.participants().stream()
            .map(UserDto::id)
            .collect(java.util.stream.Collectors.toSet());

    sseService.send(receiverIds, eventName, channel);
  }

  // User
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void on(UserCreatedEvent event) {
    UserDto user = event.getData();

    sseService.broadcast("users.created", user);

    log.debug("SSE 사용자 생성 이벤트 전송 완료: userId={}", user.id());
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void on(UserUpdatedEvent event) {
    UserDto user = event.getTo();

    sseService.broadcast("users.updated", user);

    log.debug("SSE 사용자 수정 이벤트 전송 완료: userId={}", user.id());
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void on(UserDeletedEvent event) {
    UserDto user = event.getData();

    sseService.broadcast("users.deleted", user);

    log.debug("SSE 사용자 삭제 이벤트 전송 완료: userId={}", user.id());
  }
}
