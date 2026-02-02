package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Arrays;
import java.util.UUID;

@Getter
public class BinaryContent {
    private final UUID id;
    private final Instant createdAt;
    private final UUID userId;
    private final UUID messageId;
    private final byte[] data;
    private final String contentType;   // image/png, image/jpeg
    private final String filename;      // 원본 파일명
    private final long size;            // 바이트 크기

    // 사용자 프로필 사진용
    public BinaryContent(UUID userId, byte[] data, String contentType, String filename) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.userId = userId;
        this.messageId = null;

        this.data = data;
        this.contentType = contentType;
        this.filename = filename;
        this.size = data.length;
    }

    public BinaryContent(UUID userId, UUID messageId, byte[] data, String contentType, String filename) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.userId = userId;
        this.messageId = messageId;

        this.data = data;
        this.contentType = contentType;
        this.filename = filename;
        this.size = data.length;
    }

    @Override
    public String toString() {
        return "BinaryContent{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", userId=" + userId +
                ", messageId=" + messageId +
                ", data=" + data.length +
                ", contentType='" + contentType + '\'' +
                ", filename='" + filename + '\'' +
                ", size=" + size +
                '}';
    }
}
