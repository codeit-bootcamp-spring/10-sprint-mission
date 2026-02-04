package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
@ToString
public class BinaryContent implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final UUID id;
    private final Instant createdAt;
    private final String fileName;
    private final byte[] data;

    public BinaryContent(String fileName, byte[] data) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.fileName = fileName;
        this.data = data;
    }
}
