package com.sprint.mission.discodeit.event.sse;

import com.sprint.mission.discodeit.dto.channel.ChannelDto;

public record ChannelChangedEvent(
    ChannelDto channelDto,
    ChannelAction action
) {

  public enum ChannelAction {
    CREATED, UPDATED, DELETED
  }
}
