package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public abstract class BaseEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    // Getter
    // 식별자/생성 시각/수정 시각 공통 필드
    protected final UUID id;
    protected final Instant createdAt;
    protected Instant updatedAt;

    // constructor
    protected BaseEntity() {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    // update
    protected void updateTimestamp() {
        this.updatedAt = Instant.now();
    }
}