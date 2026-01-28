package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.UUID;

public class User extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;


    private String email;
    private String username;

    public User(String username, String email) {
//        this.id = UUID.randomUUID();
//        this.createdAt = System.currentTimeMillis();
//        this.updatedAt = this.createdAt;

        this.username = username;
        this.email = email;
    }

//    public void update(UUID id,String username, String email) {
//        this.username = username;
//        this.id = UUID.randomUUID();
//        this.email = email;
//        this.updatedAt = System.currentTimeMillis();
//    }

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

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
