package com.sprint.mission.discodeit.dto.channel.request;

import java.util.UUID;

public record ChannelUpdateRequest(
        UUID channelID,
        String name,
        String descriptions
) {
}
