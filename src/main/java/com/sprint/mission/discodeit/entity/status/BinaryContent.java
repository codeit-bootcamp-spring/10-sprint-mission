package com.sprint.mission.discodeit.entity.status;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;


@Getter
public class BinaryContent {
    // Getters
    private final UUID id;
    private final String contentType;
    private final int size;
    private final byte[] data;
    private String fileName;
    private final Instant createdAt;
    private Instant updatedAt;

    public BinaryContent(UUID id, String contentType, int size, byte[] data, String fileName, Instant createdAt, Instant now) {
        this.id = id;
        this.contentType = contentType;
        this.size = size;
        this.data = data;
        this.fileName = this.fileName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public BinaryContent create(String contentType, byte[] data) {
        return new BinaryContent(
                UUID.randomUUID(),
                contentType,
                data.length,
                data,
                fileName,
                Instant.now(),
                Instant.now()
        );
    }

}
