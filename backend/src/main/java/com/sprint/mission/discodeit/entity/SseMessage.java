package com.sprint.mission.discodeit.entity;

import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SseMessage {

  private final UUID id;
  private final String eventName;
  private final Object data;

  public static SseMessage of(String eventName, Object data) {
    return new SseMessage(UUID.randomUUID(), eventName, data);
  }
}
