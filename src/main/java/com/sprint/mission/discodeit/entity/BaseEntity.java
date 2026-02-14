package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.UUID;

public abstract class BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    protected final UUID id;
    protected final Long createdAt;
    protected Long updatedAt;

    public BaseEntity() {
        this.id = UUID.randomUUID();
        long now = System.currentTimeMillis();
        this.createdAt = now;
        this.updatedAt = now;
    }

    public UUID getId() { return id; }
    public Long getCreatedAt() { return createdAt; }
    public Long getUpdatedAt() { return updatedAt; }

    public void updateTimestamp() { this.updatedAt = System.currentTimeMillis(); }
}