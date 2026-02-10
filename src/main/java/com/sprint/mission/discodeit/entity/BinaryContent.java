package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class BinaryContent implements Serializable {
    private static final long serialVersionUID = 1L;
    private final UUID id;
    private final UUID userId;
    private final UUID messageId;
    private final Instant createdAt;
    private final byte[] bytes;
    private final String fileName;
    private final String contentType;

    public BinaryContent(UUID userId, UUID messageId, byte[] bytes, String fileName, String contentType){
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.messageId = messageId;
        this.createdAt = Instant.now();
        this.bytes = bytes;
        this.fileName = fileName;
        this.contentType = contentType;
    }
}
