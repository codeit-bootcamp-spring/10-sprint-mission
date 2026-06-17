package com.sprint.mission.discodeit.event.sse;

import com.sprint.mission.discodeit.dto.channel.ChannelDto;

public record ChannelChangedEvent(
    ChannelDto channelDto,
    Action action
) {

  public enum Action {
    CREATED, UPDATED, DELETED
  }
}
