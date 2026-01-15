package com.sprint.mission.entity;

import java.util.UUID;

public abstract class Entity {
    private final UUID id;
    private String value;
    private final long createdAt;
    private long updatedAt;

    public Entity(String value) {
        this.value = value;
        this.id = UUID.randomUUID();
        this.createdAt = getUnixTimestamp();
        this.updatedAt = createdAt;
    }

    private long getUnixTimestamp() {
        return System.currentTimeMillis() / 1000;
    }

    protected String getValue() {
        return value;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void update(String value) {
        this.value = value;
        this.updatedAt = getUnixTimestamp();
    }

    public UUID getId() {
        return id;
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Entity entity) {
            return this.id.equals(entity.getId());
        }
        throw new IllegalArgumentException("not Entity class");
    }
}
