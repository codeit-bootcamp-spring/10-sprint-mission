package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

//불변이여야 하기 때문에 BaseEntity를 상속받을 수 없음
@Getter
public class BinaryContent implements Serializable {
    private static final long serialVersionUID = 1L;

    private final UUID id;
    private final Instant createdAt;
    private final String fileName;
    private final String contentType;
    private final long size;
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
