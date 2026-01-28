package com.sprint.mission.discodeit.dto.channel.request;

import java.util.UUID;

public record ChannelCreateRequestPublic(
        UUID channelID,
        String name,
        String descriptions
) {
}
