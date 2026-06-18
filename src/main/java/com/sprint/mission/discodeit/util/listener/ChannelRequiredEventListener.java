package com.sprint.mission.discodeit.util.listener;

import com.sprint.mission.discodeit.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.event.ChannelCreatedEvent;
import com.sprint.mission.discodeit.event.ChannelDeletedEvent;
import com.sprint.mission.discodeit.event.ChannelUpdatedEvent;
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
public class ChannelRequiredEventListener {

  private final SseService sseService;

  @Async
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void on(ChannelCreatedEvent event) {
    ChannelDto dto = event.channelDto();
    if (dto.type() == ChannelType.PUBLIC) {
      sseService.broadcast("channels.created", dto);
    } else {
      List<UUID> memberIds = dto.participants().stream().map(UserDto::id).toList();
      sseService.send(memberIds, "channels.created", dto);
    }
  }

  @Async
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void on(ChannelUpdatedEvent event) {
    sseService.broadcast("channels.updated", event.channelDto());
  }

  @Async
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void on(ChannelDeletedEvent event) {
    sseService.broadcast("channels.deleted", event);
  }

}
