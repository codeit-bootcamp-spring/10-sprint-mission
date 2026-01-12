package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public abstract class BaseEntity {
    protected final UUID id;
    protected final Long createdAt;
    protected Long updatedAt;

    public BaseEntity() {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = this.createdAt;
    }

    // Getters
    public UUID getId() {
        return id;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    // Update
    protected void updateTimestamp() {
        this.updatedAt = System.currentTimeMillis();
    }


}
