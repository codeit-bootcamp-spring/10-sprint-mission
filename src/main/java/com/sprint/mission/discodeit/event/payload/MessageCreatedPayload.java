package com.sprint.mission.discodeit.event.payload;

import java.util.UUID;

public record MessageCreatedPayload(
    UUID channelId,
    String channelName,
    UUID messageId,
    UUID authorId,
    String authorName,
    String content
) {

}
