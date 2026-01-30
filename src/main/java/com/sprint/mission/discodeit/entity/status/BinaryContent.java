package com.sprint.mission.discodeit.entity.status;

import java.time.Instant;
import java.util.UUID;

/**
 * 수정 불가능한 도메인 모델 (updatedAt 없음)
 */
public class BinaryContent {
    private final UUID id;
    private final String contentType;
    private final long size;
    private final byte[] data;
    private final Instant createdAt;

    public BinaryContent(UUID id, String contentType, long size, byte[] data, Instant createdAt) {
        this.id = id;
        this.contentType = contentType;
        this.size = size;
        this.data = data;
        this.createdAt = createdAt;
    }

    public static BinaryContent create(String contentType, byte[] data) {
        return new BinaryContent(
                UUID.randomUUID(),
                contentType,
                data.length,
                data,
                Instant.now()
        );
    }

    // Getters
    public UUID getId() { return id; }
    public String getContentType() { return contentType; }
    public long getSize() { return size; }
    public byte[] getData() { return data; }
    public Instant getCreatedAt() { return createdAt; }
}
