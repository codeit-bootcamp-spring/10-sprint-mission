package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public abstract class Entity {
    private final UUID id;
    private final Long createdAt;
    private Long updatedAt;

    public Entity() {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public void update() {
        // 업데이트 시간 갱신
        this.updatedAt = System.currentTimeMillis();
    }
}
