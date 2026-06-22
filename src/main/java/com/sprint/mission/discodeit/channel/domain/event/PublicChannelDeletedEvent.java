package com.sprint.mission.discodeit.channel.domain.event;

import com.sprint.mission.discodeit.common.event.DomainEvent;
import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PublicChannelDeletedEvent extends DomainEvent {

  private final UUID id;
}
