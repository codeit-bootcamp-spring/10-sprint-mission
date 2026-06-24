package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.dto.data.UserDto;

public record UserEvent(Action action, UserDto user) {

  public enum Action {
    CREATED, UPDATED, DELETED
  }

  public String eventName() {
    return "users." + action.name().toLowerCase();
  }
}
