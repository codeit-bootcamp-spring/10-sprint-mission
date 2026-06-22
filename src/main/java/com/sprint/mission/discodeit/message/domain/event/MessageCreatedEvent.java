package com.sprint.mission.discodeit.message.domain.event;

import com.sprint.mission.discodeit.common.event.DomainEvent;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public final class MessageCreatedEvent extends DomainEvent {

  private final UUID id;
  private final Instant createdAt;
  private final UUID authorId;
  private final UUID channelId;
  private final String content;
  private final List<UUID> attachmentIds;
}
