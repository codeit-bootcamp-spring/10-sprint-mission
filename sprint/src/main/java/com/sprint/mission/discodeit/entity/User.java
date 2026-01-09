package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class User {
    private UUID id;
    private long createdAt;
    private long updatedAt;
    private String username;
    private String email;

    public UUID getId() {
        return id;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public void updateUsername(String username) {
        this.username = username;
        this.updatedAt = System.currentTimeMillis();
    }

    public void updateEmail(String email) {
        this.email = email;
        this.updatedAt = System.currentTimeMillis();
    }

    public User(String username, String email) {
        id = UUID.randomUUID();
        createdAt = System.currentTimeMillis();
        updatedAt = createdAt;

        this.username = username;
        this.email = email;
    }
}
