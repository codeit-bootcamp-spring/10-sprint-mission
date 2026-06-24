package com.sprint.mission.discodeit.event.channel;

import java.util.UUID;

public record ChannelDeletedEvent(
    UUID channelId
) {
}