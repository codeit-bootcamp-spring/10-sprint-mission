package com.sprint.mission.discodeit.event.sse;

import com.sprint.mission.discodeit.dto.data.ChannelDto;

public record ChannelChangedEvent(
    Type type,
    ChannelDto channel
) {

  public enum Type {
    CREATED, UPDATED, DELETED
  }
}
