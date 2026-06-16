package com.sprint.mission.discodeit.event.sse;

import com.sprint.mission.discodeit.dto.user.UserDto;

public record UserChangedEvent(
    UserDto userDto,
    Action action
) {

  public enum Action {
    CREATED, UPDATED, DELETED
  }
}
