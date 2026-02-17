package com.sprint.mission.discodeit.channel.dto;

import java.util.UUID;

public record ChannelUpdateRequest(
        UUID channelId,
        String newName,
        String newDescription
) {
}
