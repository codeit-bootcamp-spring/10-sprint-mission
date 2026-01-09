package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public abstract class BaseEntity {
    // common field
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
    public UUID getId() { return id; }
    public Long getCreatedAt() { return createdAt; }
    public Long getUpdatedAt() { return updatedAt; }

    // update
    protected void updateTimestamp() {
        this.updatedAt = System.currentTimeMillis();
    }
}