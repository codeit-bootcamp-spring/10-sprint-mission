package com.sprint.mission.discodeit.entity;

<<<<<<< HEAD
import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID id;
    private Long createdAt;
    private Long updatedAt;
    //
    private String username;
    private String email;
    private String password;

    public User(String username, String email, String password) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now().getEpochSecond();
        //
        this.username = username;
=======
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
>>>>>>> upstream/김혜성
        this.email = email;
        this.password = password;
    }

<<<<<<< HEAD
//    public UUID getId() {
//        return id;
//    }
//
//    public Long getCreatedAt() {
//        return createdAt;
//    }
//
//    public Long getUpdatedAt() {
//        return updatedAt;
//    }
//
//    public String getUsername() {
//        return username;
//    }
//
//    public String getEmail() {
//        return email;
//    }
//
//    public String getPassword() {
//        return password;
//    }

    public void update(String newUsername, String newEmail, String newPassword) {
        boolean anyValueUpdated = false;
        if (newUsername != null && !newUsername.equals(this.username)) {
            this.username = newUsername;
            anyValueUpdated = true;
        }
        if (newEmail != null && !newEmail.equals(this.email)) {
            this.email = newEmail;
            anyValueUpdated = true;
        }
        if (newPassword != null && !newPassword.equals(this.password)) {
            this.password = newPassword;
            anyValueUpdated = true;
        }

        if (anyValueUpdated) {
            this.updatedAt = Instant.now().getEpochSecond();
        }
=======
    public void setUsername(String username) {
        this.username = username;
>>>>>>> upstream/김혜성
    }
}
