package com.sprint.mission.discodeit.dto.channel;

import java.util.UUID;

public record ChannelUpdateDTO(
        UUID channelId,
        String name,
        String description
) {
}
