package com.sprint.mission.discodeit.entity.status;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

/**
 * 수정 불가능한 도메인 모델 (updatedAt 없음)
 */
@Getter
public class BinaryContent {
    // Getters
    private final UUID id;
    private final String contentType;
    private final int size;
    private final byte[] data;
    private final Instant createdAt;

    public BinaryContent(UUID id, String contentType, int size, byte[] data, Instant createdAt) {
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

}
