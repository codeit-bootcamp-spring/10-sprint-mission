package com.sprint.mission.discodeit.event.sse;

import com.sprint.mission.discodeit.dto.data.UserDto;

public record UserChangedEvent(
    Type type,
    UserDto user
) {

  public enum Type {
    CREATED, UPDATED, DELETED, STATUS_CHANGED
  }
}
