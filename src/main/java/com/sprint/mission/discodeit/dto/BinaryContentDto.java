package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.BinaryContentType;

import java.time.Instant;
import java.util.UUID;

public final class BinaryContentDto {
    private BinaryContentDto() {}

    public record createRequest(BinaryContentType contentType, String filename, String url) {}
    public record response(UUID uuid, Instant createdAt,
                           BinaryContentType contentType, String filename, String url) { }
}
