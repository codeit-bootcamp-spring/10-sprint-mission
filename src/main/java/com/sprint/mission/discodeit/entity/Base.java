package com.sprint.mission.discodeit.entity;

import java.util.UUID;

// User, Channel, Message 간 공통 필드, Getter, Setter가 존재해 추상 Class
public abstract class Base {
    // 필드
    private final UUID id;
    private final Long createdAt;
    private Long updatedAt;

    // 생성자
    public Base() {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = createdAt;
    }

    // Getter
    public UUID getId() {
        return id;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    // Setter
    public void updateUpdatedAt() {
        this.updatedAt = System.currentTimeMillis();
    }

    // other
    public void update(){
        this.updatedAt = System.currentTimeMillis();
    }
}
