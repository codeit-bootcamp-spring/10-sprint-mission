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
import com.sprint.mission.discodeit.service.sse.SseService;
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

  private static final String NOTIFICATIONS_CREATED = "notifications.created";
  private static final String BINARY_CONTENTS_UPDATED = "binaryContents.updated";
  private static final String CHANNELS_CREATED = "channels.created";
  private static final String CHANNELS_UPDATED = "channels.updated";
  private static final String CHANNELS_DELETED = "channels.deleted";
  private static final String USERS_CREATED = "users.created";
  private static final String USERS_UPDATED = "users.updated";
  private static final String USERS_DELETED = "users.deleted";

  private final SseService sseService;

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
  public void on(NotificationCreatedEvent event) {
    sseService.send(Set.of(event.getData().receiverId()), NOTIFICATIONS_CREATED, event.getData());
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
  public void on(BinaryContentUpdatedEvent event) {
    sseService.broadcast(BINARY_CONTENTS_UPDATED, event.getTo());
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
  public void on(ChannelCreatedEvent event) {
    sendChannelEvent(CHANNELS_CREATED, event.getData());
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
  public void on(ChannelUpdatedEvent event) {
    sendChannelEvent(CHANNELS_UPDATED, event.getTo());
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
  public void on(ChannelDeletedEvent event) {
    sendChannelEvent(CHANNELS_DELETED, event.getData());
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
  public void on(UserCreatedEvent event) {
    sseService.broadcast(USERS_CREATED, event.getData());
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
  public void on(UserUpdatedEvent event) {
    sseService.broadcast(USERS_UPDATED, event.getTo());
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
  public void on(UserDeletedEvent event) {
    sseService.broadcast(USERS_DELETED, event.getData());
  }

  private void sendChannelEvent(String eventName, ChannelDto channel) {
    if (channel.type().equals(ChannelType.PRIVATE)) {
      sseService.send(resolveParticipantIds(channel), eventName, channel);
      return;
    }
    sseService.broadcast(eventName, channel);
  }

  private Set<UUID> resolveParticipantIds(ChannelDto channel) {
    return channel.participants().stream()
        .map(UserDto::id)
        .collect(Collectors.toSet());
  }
}
