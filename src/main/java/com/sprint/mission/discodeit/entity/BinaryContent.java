package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public final class BinaryContent implements Serializable {
    private static final long serialVersionUID = 1L;
    private final UUID id;
    private final Instant createdAt;

    private final String fileName;
    private final String contentType;
    private final byte[] bytes;

    public BinaryContent(String fileName, String contentType, byte[] bytes) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.fileName = fileName;
        this.contentType = contentType;
        this.bytes = bytes;
    }

    public byte[] getBytes() {
        return bytes.clone();
    }
}
