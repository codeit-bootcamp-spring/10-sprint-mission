package com.sprint.mission.discodeit.util.listener;

import com.sprint.mission.discodeit.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.event.UserCreatedEvent;
import com.sprint.mission.discodeit.event.UserDeletedEvent;
import com.sprint.mission.discodeit.event.UserUpdatedEvent;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.SseService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserRequiredEventListener {

  private final SseService sseService;
  private final ChannelService channelService;

  @Async
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void on(UserCreatedEvent event) {
    sseService.broadcast("users.created", event.userDto());
  }

  @Async
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT,
      fallbackExecution = true)
  public void on(UserUpdatedEvent event) {
    sseService.broadcast("users.updated", event.userDto());
    List<ChannelDto> myAllChannels = channelService.findAllByUserId(event.userDto().id());
    myAllChannels.stream()
        .filter(dto -> dto.type() == ChannelType.PRIVATE)
        .forEach(dto -> {
          List<UUID> memberIds = dto.participants().stream()
              .map(UserDto::id)
              .toList();
          sseService.send(memberIds, "channels.updated", dto);
        });
  }

  @Async
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void on(UserDeletedEvent event) {
    sseService.broadcast("users.deleted", event);
  }

}
