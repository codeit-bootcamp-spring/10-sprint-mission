package com.sprint.mission.discodeit.binarycontent.domain.event;

import com.sprint.mission.discodeit.common.event.DomainEvent;
import java.util.Map;
import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public final class BinaryContentFailedEvent extends DomainEvent {

  private final Map<UUID, String> failures;
}
