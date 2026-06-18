package com.sprint.mission.discodeit.sse;

import java.util.Collection;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class SseMessage {

  private UUID id;
  private Collection<UUID> receiverIds;
  private boolean broadcast;
  private String eventName;
  private Object data;
}
