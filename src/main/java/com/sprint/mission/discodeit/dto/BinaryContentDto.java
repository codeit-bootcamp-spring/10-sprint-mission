package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.BinaryContentType;

import java.time.Instant;
import java.util.UUID;

public final class BinaryContentDto {
    private BinaryContentDto() {}

    public record createRequest(BinaryContentType contentType, String filename, byte[] bytes) {}
    public record response(UUID uuid, Instant createdAt,
                           String contentType, String filename, byte[] bytes) { }
}
