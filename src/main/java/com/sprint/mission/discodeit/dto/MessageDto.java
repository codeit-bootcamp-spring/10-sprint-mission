package com.sprint.mission.discodeit.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public final class MessageDto {
    private MessageDto() {}

    public record createRequest(UUID channelId, UUID authorId, String message) {}
    public record updateRequest(String message) {}
    public record response(UUID uuid, Instant createdAt, Instant updatedAt,
                           UUID channelId, UUID authorId,
                           String message, List<UUID> attachmentIds) {}
}
