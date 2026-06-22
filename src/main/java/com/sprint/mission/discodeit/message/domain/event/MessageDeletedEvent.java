package com.sprint.mission.discodeit.message.domain.event;

import com.sprint.mission.discodeit.common.event.DomainEvent;
import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MessageDeletedEvent extends DomainEvent {

  private final UUID id;
  private final UUID channelId;
}
