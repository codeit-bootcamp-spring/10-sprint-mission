package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public abstract class BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    private final UUID id;
    private final Long createdAt;
    private Long updatedAt;

    protected BaseEntity() { // 필드 초기화
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseEntity)) return false;
        BaseEntity that = (BaseEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public UUID getId() {
        return id;
    } // ID Getter

    public Long getCreatedAt() {
        return createdAt;
    } // CreatedAt Getter

    public Long getUpdatedAt() {
        return updatedAt;
    } // UpdatedAt Getter

    protected void update() {  // 필드 수정 메서드
        this.updatedAt = System.currentTimeMillis();
    }
}
