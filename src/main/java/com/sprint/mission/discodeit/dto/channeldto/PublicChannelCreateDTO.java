package com.sprint.mission.discodeit.dto.channeldto;

import com.sprint.mission.discodeit.entity.ChannelType;

public record PublicChannelCreateDTO(
        ChannelType channelType,
        String name,
        String description
) {
}
