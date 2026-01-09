package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class User {
    private final UUID id;
    private final Long createAt;
    private Long updatedAt;
    private String userName;

    public User(String userName) {
        this.id = UUID.randomUUID();
        this.createAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
        this.userName = userName;
    }

    public UUID getId() {
        return id;
    }

    public Long getCreateAt() {
        return createAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public String getUserName() {
        return userName;
    }

    public void updateUserName(String userName) {
        this.userName = userName;
        this.updatedAt = System.currentTimeMillis();
    }

    public String toString() {
        return userName;
    }
}
