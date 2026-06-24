package com.sprint.mission.discodeit.event.sse;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.service.SseService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class SseEventListener {

  private final SseService sseService;

  @Async("eventTaskExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void on(NotificationCreatedEvent event) {
    NotificationDto dto = event.notification();
    sseService.send(List.of(dto.receiverId()), "notifications.created", dto);
  }

  @Async("eventTaskExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void on(BinaryContentStatusChangedEvent event) {
    sseService.broadcast("binaryContents.updated", event.binaryContent());
  }

  @Async("eventTaskExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void on(ChannelChangedEvent event) {
    ChannelDto dto = event.channel();
    String eventName = switch (event.type()) {
      case CREATED -> "channels.created";
      case UPDATED -> "channels.updated";
      case DELETED -> "channels.deleted";
    };
    if (dto.type() == ChannelType.PRIVATE) {
      List<UUID> receiverIds = dto.participants().stream().map(UserDto::id).toList();
      sseService.send(receiverIds, eventName, dto);
    } else {
      sseService.broadcast(eventName, dto);
    }
  }

  @Async("eventTaskExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void on(UserChangedEvent event) {
    if (event.type() == UserChangedEvent.Type.STATUS_CHANGED) {
      return;
    }
    broadcastUserEvent(event);
  }

  @Async("eventTaskExecutor")
  @EventListener
  public void onUserStatusChanged(UserChangedEvent event) {
    if (event.type() != UserChangedEvent.Type.STATUS_CHANGED) {
      return;
    }
    broadcastUserEvent(event);
  }

  private void broadcastUserEvent(UserChangedEvent event) {
    String eventName = switch (event.type()) {
      case CREATED -> "users.created";
      case UPDATED, STATUS_CHANGED -> "users.updated";
      case DELETED -> "users.deleted";
    };
    sseService.broadcast(eventName, event.user());
  }
}
