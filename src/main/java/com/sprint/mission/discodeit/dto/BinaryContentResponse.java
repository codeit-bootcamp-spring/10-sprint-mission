package com.sprint.mission.discodeit.dto;

import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class BinaryContentResponse {
    private final UUID id;
    private final String fileName;
    private final long size;
    private final String contentType;
    private final Instant createdAt;

    public BinaryContentResponse(UUID id, String fileName, long size, String contentType, Instant createdAt) {
        this.id = id;
        this.fileName = fileName;
        this.size = size;
        this.contentType = contentType;
        this.createdAt = createdAt;
    }
}
