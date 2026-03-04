package com.sprint.mission.discodeit.dto.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record MessageResponse(

    @JsonProperty("id")
    UUID messageId,

    UUID channelId,

    @JsonProperty("authorId")
    UUID userId,

    String content,
    List<UUID> attachmentIds,
    Instant createdAt,
    Instant updatedAt
) {

}
