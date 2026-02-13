package com.sprint.mission.discodeit.dto.channel.request;

import java.util.UUID;

public record ChannelUpdateRequest(
        String name,
        String descriptions
) {
}
