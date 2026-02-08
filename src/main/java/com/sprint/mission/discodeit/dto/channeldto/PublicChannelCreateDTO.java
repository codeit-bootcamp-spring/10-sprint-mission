package com.sprint.mission.discodeit.dto.channeldto;

import com.sprint.mission.discodeit.entity.ChannelType;

public record PublicChannelCreateDTO(
        String name,
        String description
) {
}
