package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public abstract class BaseEntity {
    private UUID id;
    private Long createdAt;
    private Long updatedAt;

    protected BaseEntity() {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
    }
    public UUID getId() {
        return id;
    }
    protected void setUpdatedAt() {
        this.updatedAt =  System.currentTimeMillis();
    }
    public Long getCreatedAt() {
        return createdAt;
    }
    public Long getUpdatedAt() {
        return updatedAt;
    }
}
