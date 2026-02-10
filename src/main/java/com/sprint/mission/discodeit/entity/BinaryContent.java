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
    private final byte[] data;
    private final String fileName;
    private final String fileType;

    public BinaryContent(UUID userId, UUID messageId, byte[] data, String fileName, String fileType){
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.messageId = messageId;
        this.createdAt = Instant.now();
        this.data = data;
        this.fileName = fileName;
        this.fileType = fileType;
    }
}
