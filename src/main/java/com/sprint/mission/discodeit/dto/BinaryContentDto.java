package com.sprint.mission.discodeit.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sprint.mission.discodeit.entity.BinaryContentType;

import java.time.Instant;
import java.util.UUID;

public final class BinaryContentDto {
    private BinaryContentDto() {}

    public record binaryContentCreateRequest(BinaryContentType contentType, String filename, byte[] bytes) {}
    public record binaryContentResponse(@JsonProperty("id") UUID uuid, Instant createdAt,
                                        String fileName, long size, String contentType, byte[] bytes) { }
}
