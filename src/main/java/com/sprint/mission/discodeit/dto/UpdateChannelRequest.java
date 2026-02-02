package com.sprint.mission.discodeit.dto;

import java.util.UUID;

public record UpdateChannelRequest(
        UUID channelId,
        String name,
        String description
) {
}
