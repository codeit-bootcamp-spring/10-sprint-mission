package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

//불변이여야 하기 때문에 BaseEntity를 상속받을 수 없음
public class BinaryContent implements Serializable {
    private static final long serialVersionUID = 1L;

    @Getter
    private final UUID id;
    @Getter
    private final Instant createdAt;
    @Getter
    private final String fileName;
    @Getter
    private final String contentType;
    @Getter
    private final long size;
    @Getter
    private final byte[] bytes;

    public BinaryContent(String fileName, String contentType, long size, byte[] bytes) {
        this.fileName = fileName;
        this.contentType = contentType;
        this.size = size;
        this.bytes = bytes;
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
    }
}
