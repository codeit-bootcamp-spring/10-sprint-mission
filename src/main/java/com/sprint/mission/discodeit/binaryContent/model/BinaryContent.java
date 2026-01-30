package com.sprint.mission.discodeit.binaryContent.model;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 수정 불가능한 도메인 모델 (updatedAt 없음)
 */
public class BinaryContent {
    private final UUID id;
    private final String contentType;
    private final long size;
    private final byte[] data; // 실제로는 파일 경로나 스토리지 키를 쓸 수도 있음
    private final LocalDateTime createdAt;

    public BinaryContent(UUID id, String contentType, long size, byte[] data, LocalDateTime createdAt) {
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
                LocalDateTime.now()
        );
    }

    // Getters
    public UUID getId() { return id; }
    public String getContentType() { return contentType; }
    public long getSize() { return size; }
    public byte[] getData() { return data; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
