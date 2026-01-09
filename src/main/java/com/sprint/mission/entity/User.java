package com.sprint.mission.entity;

import java.util.UUID;

public class User {
    UUID id;
    String nickName;
    Long createdAt;
    Long updatedAt;

    public User(String nickName) {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.nickName = nickName;
    }

    public void updateNickName(String nickName) {
        if (!nickName.isEmpty()) {
            this.nickName = nickName;
            touch();
        }
    }

    private void touch() {
        this.updatedAt = System.currentTimeMillis();
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
