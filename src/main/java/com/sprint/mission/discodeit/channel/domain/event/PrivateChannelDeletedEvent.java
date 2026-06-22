package com.sprint.mission.discodeit.channel.domain.event;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sprint.mission.discodeit.common.event.DomainEvent;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PrivateChannelDeletedEvent extends DomainEvent {

  private final UUID id;
  @JsonIgnore
  private final List<UUID> participantIds;
}
