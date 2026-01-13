package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class User extends BaseEntity {

    private String email;
    private String username;

    public User(String username, String email) {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = this.createdAt;

        this.username = username;
        this.email = email;
    }

    public void update(String username, String email) {
        this.username = username;
        this.id = UUID.randomUUID();
        this.email = email;
        this.updatedAt = System.currentTimeMillis();
    }

    public UUID getId() {
        return id;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }


    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }
}
