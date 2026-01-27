package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class BaseEntity {
<<<<<<< HEAD
    protected UUID id;
    protected long createdAt;
    protected long updatedAt;

    protected BaseEntity() {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = this.createdAt;
=======
    private UUID id;
    private Long createdAt;
    private Long updatedAt;

    public BaseEntity() {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getCreatedAt() {
        return createdAt;
>>>>>>> 3a7b55e457e0d55f5042c220079e6b60cb0acc7f
    }

    public UUID getId() {
        return id;
    }

<<<<<<< HEAD
    public long getCreatedAt() {
        return createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void touch() { // 수정 시점 갱신용 헬퍼
        this.updatedAt = System.currentTimeMillis();
    }

=======
>>>>>>> 3a7b55e457e0d55f5042c220079e6b60cb0acc7f
}
