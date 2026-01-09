package org.example.entity;

import java.util.UUID;

public class BaseEntity {
    private final UUID id;
    private final Long createdAt;
    protected Long updatedAt;

    protected BaseEntity() {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
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
}
