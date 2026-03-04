package com.sprint.mission.discodeit.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public final class MessageDto {
    private MessageDto() {}

    public record messageCreateRequest(UUID channelId, UUID authorId, @JsonProperty("content") String message) {}
    public record messageUpdateRequest(@JsonProperty("newContent") String message) {}
    public record messageResponse(@JsonProperty("id") UUID uuid, Instant createdAt, Instant updatedAt,
                                  UUID channelId, UUID authorId,
                                  @JsonProperty("content") String message, List<UUID> attachmentIds) {}
}
