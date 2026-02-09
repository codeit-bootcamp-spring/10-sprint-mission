package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
public class BinaryContent extends BaseEntity{

    private String contentType;
    private byte[] content;

    public BinaryContent(String contentType, byte[] content) {
        super(UUID.randomUUID(), Instant.now());
        this.contentType = contentType;
        this.content = content;
    }

}
