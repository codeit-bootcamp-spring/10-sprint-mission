package com.sprint.mission.discodeit.event.listener;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
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
import com.sprint.mission.discodeit.service.SseService;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class SseRequiredEventListener {

  private static final String NOTIFICATION_CREATED = "notifications.created";
  private static final String BINARY_CONTENT_UPDATED = "binaryContents.updated";
  private static final String CHANNEL_CREATED = "channels.created";
  private static final String CHANNEL_UPDATED = "channels.updated";
  private static final String CHANNEL_DELETED = "channels.deleted";
  private static final String USER_CREATED = "users.created";
  private static final String USER_UPDATED = "users.updated";
  private static final String USER_DELETED = "users.deleted";

  private final SseService sseService;

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
  public void on(NotificationCreatedEvent event) {
    sseService.send(
        Set.of(event.getData().receiverId()),
        NOTIFICATION_CREATED,
        event.getData()
    );
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
  public void on(BinaryContentUpdatedEvent event) {
    sseService.broadcast(BINARY_CONTENT_UPDATED, event.getTo());
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
  public void on(ChannelCreatedEvent event) {
    sendChannelEvent(CHANNEL_CREATED, event.getData());
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
  public void on(ChannelUpdatedEvent event) {
    sendChannelEvent(CHANNEL_UPDATED, event.getTo());
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
  public void on(ChannelDeletedEvent event) {
    sendChannelEvent(CHANNEL_DELETED, event.getData());
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
  public void on(UserCreatedEvent event) {
    sseService.broadcast(USER_CREATED, event.getData());
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
  public void on(UserUpdatedEvent event) {
    sseService.broadcast(USER_UPDATED, event.getTo());
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
  public void on(UserDeletedEvent event) {
    sseService.broadcast(USER_DELETED, event.getData());
  }

  private void sendChannelEvent(String eventName, ChannelDto channel) {
    if (channel.type() == ChannelType.PRIVATE) {
      Set<UUID> receiverIds = channel.participants().stream()
          .map(UserDto::id)
          .collect(Collectors.toSet());
      sseService.send(receiverIds, eventName, channel);
      return;
    }

    sseService.broadcast(eventName, channel);
  }
}
