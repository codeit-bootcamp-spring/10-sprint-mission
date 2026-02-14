package com.sprint.mission.discodeit.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class MessageDto {
    public record CreateRequest(
            UUID authorId,
            UUID channelId,
            String content,
            List<AttachmentRequest> attachments
    ) {}

    public record AttachmentRequest(
            String fileName,
            byte[] data
    ) {}

    public record UpdateRequest(
            UUID id,
            String content
    ) {}

    public record Response(
            UUID id,
            String content,
            UUID authorId,
            UUID channelId,
            Instant createdAt,
            Instant updatedAt,
            List<UUID> attachmentIds
    ) {}
}
