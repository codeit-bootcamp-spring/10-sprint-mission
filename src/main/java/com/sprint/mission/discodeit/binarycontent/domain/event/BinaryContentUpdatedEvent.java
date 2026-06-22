package com.sprint.mission.discodeit.binarycontent.domain.event;

import com.sprint.mission.discodeit.common.event.DomainEvent;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BinaryContentUpdatedEvent extends DomainEvent {

  private final List<UUID> ids;
  private final Object status;
}
