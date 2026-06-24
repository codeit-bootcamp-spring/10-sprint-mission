package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.dto.data.ChannelDto;

public record ChannelEvent(Action action, ChannelDto channel) {

  public enum Action {
    CREATED, UPDATED, DELETED
  }

  public String eventName() {
    return "channels." + action.name().toLowerCase();
  }
}
