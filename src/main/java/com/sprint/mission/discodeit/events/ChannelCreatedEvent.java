package com.sprint.mission.discodeit.events;

import com.sprint.mission.discodeit.dto.channeldto.ChannelDto;

public record ChannelCreatedEvent(
    ChannelDto channel
) {

}
