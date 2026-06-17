package com.sprint.mission.discodeit.event.message;

import java.util.UUID;

public record MessageCreatedEvent(
    UUID messageId,
    UUID channelId,
    String channelName,
    UUID senderId,
    String senderName,
    String content
) {

}
