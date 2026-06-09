package com.sprint.mission.discodeit.event.kafka.dto;

import java.util.UUID;

public record MessageCreatedKafkaEvent(
        UUID messageId,
        UUID channelId,
        String channelName,
        UUID senderId,
        String senderUsername,
        String content
) {
}