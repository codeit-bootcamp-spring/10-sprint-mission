package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.BinaryContentType;

import java.time.Instant;
import java.util.UUID;

public final class BinaryContentDto {
    public record createRequest(BinaryContentType contentType, Byte[] contentBytes) {}
    public record response(UUID uuid, Instant createdAt, Instant updatedAt,
                           BinaryContentType contentType, Byte[] content) { }
}
