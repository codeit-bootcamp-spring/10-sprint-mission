package com.sprint.mission.discodeit.dto;

import java.time.Instant;
import java.util.UUID;

public class ReadStatusDto {
    public record CreateRequest(
            UUID userId,
            UUID channelId
    ){}

    public record Response(
            UUID id,
            UUID userId,
            UUID channelId,
            UUID lastMessageReadId,
            Instant updatedAt

    ){}

    public record UpdateRequest(
            UUID id,
            UUID lastReadMessageId
    ){}
}
