package com.sprint.mission.discodeit.user.domain.event;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sprint.mission.discodeit.common.event.DomainEvent;
import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public final class UserCreatedEvent extends DomainEvent {

  private final UUID id;
  @JsonIgnore
  private final String password;
}
