package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public abstract class Common {
    private final UUID id;
    private final long createdAt;
    private long updatedAt;

    Common() {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = this.createdAt;
    }

    public UUID getId() {
        return this.id;
    }

    public long getCreatedAt() {
        return this.createdAt;
    }

    public long getUpdatedAt() {
        return this.updatedAt;
    }
    public void updateUpdatedAt() {
        this.updatedAt = System.currentTimeMillis();
    }
}
