package com.sprint.mission.discodeit.entity;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public abstract class BaseEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    protected final UUID id;
    protected final Long createdAt;
    protected Long updatedAt;

    protected BaseEntity(){
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = this.createdAt;
    }

    protected void updated(){
        this.updatedAt = System.currentTimeMillis();
    }

    public UUID getId(){ return id; }
    public Long getCreatedAt(){ return createdAt; }
    public Long getUpdatedAt(){ return updatedAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseEntity that = (BaseEntity) o;
        return Objects.equals(id, that.id); // 주소가 달라도 ID가 같으면 같은 객체로 취급
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
