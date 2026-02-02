package com.sprint.mission.discodeit.dto;

import java.time.Instant;
import java.util.UUID;

public class BinaryContentDto {
    public record CreateRequest(
            UUID id,
            String fileName,
            byte[] data,
            Instant createAt
    ) {}

    public record Response(
            UUID id,
            String fileName,
            byte[] data,
            Instant createdAt
    ) {}
}
