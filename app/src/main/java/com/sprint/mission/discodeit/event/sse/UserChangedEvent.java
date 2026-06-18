package com.sprint.mission.discodeit.event.sse;

import com.sprint.mission.discodeit.dto.user.UserDto;

public record UserChangedEvent(
    UserDto userDto,
    UserAction action
) {

  public enum UserAction {
    CREATED, UPDATED, DELETED
  }
}
