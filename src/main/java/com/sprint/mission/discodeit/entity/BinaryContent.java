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
    private final byte[] bytes;
    private final UUID userId;
    private final UUID messageId;
    private final BinaryContentType type;


    public BinaryContent(byte[] bytes, UUID userId, UUID messageId ){
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.bytes = bytes;
        this.userId = userId;
        this.messageId = messageId;
        this.type = (messageId == null) ? BinaryContentType.PROFILE : BinaryContentType.MESSAGE;
    }
}
