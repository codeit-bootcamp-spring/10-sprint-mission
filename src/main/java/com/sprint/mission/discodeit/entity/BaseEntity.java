package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public abstract class BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
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

    // [추가] equals 재정의: ID가 같으면 같은 객체로 판단
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseEntity that = (BaseEntity) o;
        return Objects.equals(id, that.id);
    }

    // [추가] hashCode 재정의: Set이나 Map에서 ID 기반으로 관리되도록 함
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }


}
