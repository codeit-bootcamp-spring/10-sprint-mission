package com.sprint.mission.discodeit.entity;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

public abstract class BaseEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    // 식별자/생성 시각/수정 시각 공통 필드
    protected final UUID id;
    protected final Long createdAt;
    protected Long updatedAt;

    // constructor
    protected BaseEntity() {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = this.createdAt;
    }

    // Getter
    public UUID getId() {
        return id;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    // update
    protected void updateTimestamp() {
        this.updatedAt = System.currentTimeMillis();
    }
}