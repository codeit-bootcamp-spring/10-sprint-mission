package com.sprint.mission.discodeit.event;

import java.util.UUID;

public record MessageCreatedEvent (
        UUID messageId,
        UUID channelId,
        UUID authorId,
        String authorUsername,
        String channelName,
        String content
){
}
