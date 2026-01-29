package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class BinaryContent {
    private final UUID id;
    private final Instant createdAt;
    private final String contentType;
    private final byte[] file;

    public BinaryContent(String contentType, byte[] file){
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.contentType = contentType;
        this.file = file;
    }

}
