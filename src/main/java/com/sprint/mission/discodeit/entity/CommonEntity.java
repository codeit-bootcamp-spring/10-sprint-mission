package com.sprint.mission.discodeit.entity;

import java.util.UUID;

abstract class CommonEntity {
    protected final UUID id;
    protected Long createdAt;
    protected Long updateAt;

    public CommonEntity() {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updateAt = this.createdAt;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        return id.equals(((CommonEntity) obj).id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public UUID getId() {
        return id;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdateAt() {
        return updateAt;
    }
}