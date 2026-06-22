package com.sprint.mission.discodeit.notification.domain.event;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sprint.mission.discodeit.common.event.DomainEvent;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class NotificationCreatedEvent extends DomainEvent {

  private final UUID id;
  @JsonIgnore
  private final UUID receiverId;
  private final String title;
  private final String content;
}
