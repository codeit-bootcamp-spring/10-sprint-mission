package com.sprint.mission.discodeit.dto.channel;

import java.util.UUID;

public record ChannelUpdateRequestDto
        (
                UUID channelId,
                String newName,
                String newDescription
        ) {
}
