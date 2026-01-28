package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class BinaryContent{
    private final UUID id;
    private final Instant createdAt;
    private final byte[] content;

    public BinaryContent(byte[] content){
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.content = content;
    }
}
