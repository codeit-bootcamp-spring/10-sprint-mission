package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

// 수정 불가능한 도메인(updatedAt 필드 정의 X)
@Getter
public class BinaryContent implements Serializable {
    private UUID id;
    private Instant createdAt;
    private String fileName;
    private long size;
    private byte[] bytes;
    private String contentType;

    public BinaryContent(String fileName, long size, byte[] bytes, String contentType) {
        this.id = UUID.randomUUID();
        this.fileName = fileName;
        this.size = size;
        this.bytes = bytes;
        this.contentType = contentType;
        this.createdAt = Instant.now();
    }
}
