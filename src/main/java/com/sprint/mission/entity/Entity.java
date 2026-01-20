package com.sprint.mission.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

public abstract class Entity<T extends Entity<T>> implements Serializable {
    private static final long serialVersionUID = 20260120L;
    private final UUID id;
    private final long createdAt;
    private long updatedAt;

    protected Entity(Entity<T> obj) {
        this.id = obj.getId();
        this.createdAt = obj.getCreatedAt();
        this.updatedAt = obj.getUpdatedAt();
    }

    public Entity() {
        this.id = UUID.randomUUID();
        this.createdAt = getUnixTimestamp();
        this.updatedAt = createdAt;
    }

    private long getUnixTimestamp() {
        return System.currentTimeMillis() / 1000;
    }

    private Date convertToDate(long unixTimestamp) {
        return new Date(unixTimestamp * 1000);
    }

    protected void updateTime() {
        this.updatedAt = getUnixTimestamp();
    }

    public abstract T copy();

    public long getCreatedAt() {
        return createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public abstract void update(String value);

    public UUID getId() {
        return id;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Entity<?> entity) {
            return this.id.equals(entity.getId());
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("id: %s, created: %s, last updated: %s",
                id, convertToDate(createdAt), convertToDate(updatedAt));
    }
}
