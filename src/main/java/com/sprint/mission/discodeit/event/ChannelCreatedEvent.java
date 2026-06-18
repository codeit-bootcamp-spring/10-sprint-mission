package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.dto.channel.ChannelDto;

public record ChannelCreatedEvent(
    ChannelDto channelDto
) {

}
