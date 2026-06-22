package com.sprint.mission.discodeit.channel.domain.event;

import com.sprint.mission.discodeit.common.event.DomainEvent;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public final class ReadStatusCreatedEvent extends DomainEvent {

  private final UUID channelId;
  private final List<UUID> participantIds;
  private final boolean notificationEnabled;
}
