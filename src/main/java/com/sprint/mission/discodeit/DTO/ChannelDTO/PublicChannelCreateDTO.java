package com.sprint.mission.discodeit.DTO.ChannelDTO;

import com.sprint.mission.discodeit.entity.ChannelType;

public record PublicChannelCreateDTO(
        ChannelType channelType,
        String name,
        String description
) {
}
