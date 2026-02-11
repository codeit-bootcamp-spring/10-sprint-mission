package com.sprint.mission.discodeit.dto;

import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString
public class BinaryContentResponse {
    private UUID id;
    private String fileName;
    private long size;
    private String contentType;
    private Instant createdAt;

    public BinaryContentResponse(UUID id, String fileName, long size, String contentType, Instant createdAt) {
        this.id = id;
        this.fileName = fileName;
        this.size = size;
        this.contentType = contentType;
        this.createdAt = createdAt;
    }
}
