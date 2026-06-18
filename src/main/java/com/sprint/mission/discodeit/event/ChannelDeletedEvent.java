package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.dto.ChannelDto;

public record ChannelDeletedEvent(
    ChannelDto channel
) {

}
