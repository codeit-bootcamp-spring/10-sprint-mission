package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.DTO.BinaryContentRecord;

import java.time.Instant;
import java.util.UUID;

public class BinaryContent {
    private final UUID id;
    private final Instant createdAt;
    private final String contentType;
    private final byte[] files;

    public BinaryContent(String contentType, byte[] files){
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.contentType = contentType;
        this.files = files;
    }

    public UUID getId() {
        return id;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public String getContentType() {
        return contentType;
    }

    public byte[] getFiles() {
        return files;
    }
}
