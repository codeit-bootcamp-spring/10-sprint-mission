package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
public class BinaryContent implements Serializable {
    private static final long serialVersionUID = 1L;

    private final UUID id;
    private final Instant createdAt;

    private final byte[] data;
    private final UUID userId;
    private final UUID messageId;

    public BinaryContent(UUID id, byte[] data ,UUID userId, UUID messageId){
        this.id = id;
        this.createdAt = Instant.now();
        this.data = data;
        this.userId = userId;
        this.messageId = messageId;
    }
}
