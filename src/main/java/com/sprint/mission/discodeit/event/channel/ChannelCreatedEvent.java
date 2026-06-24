package com.sprint.mission.discodeit.event.channel;

import com.sprint.mission.discodeit.dto.channel.ChannelDto;

public record ChannelCreatedEvent(
    ChannelDto channel
) {
}