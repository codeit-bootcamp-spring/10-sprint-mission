package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Arrays;
import java.util.UUID;

@Getter
@Setter
public class BinaryContent {
    private final UUID id;
    private final Instant createdAt;
    private UUID userId;
    private UUID messageId;
    private byte[] data;
    private String contentType;   // image/png, image/jpeg
    private String filename;      // 원본 파일명
    private long size;            // 바이트 크기

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
                ", data=" + Arrays.toString(data) +
                ", contentType='" + contentType + '\'' +
                ", filename='" + filename + '\'' +
                ", size=" + size +
                '}';
    }
}
