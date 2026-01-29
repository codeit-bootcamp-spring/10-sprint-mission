package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID id;
    private Instant createdAt;
    private Instant updatedAt;
    private String username;
    private String email;
    private String password;
    private UUID profileId;
    private @Setter UserStatus userStatus;


    public User(String username, String email, String password, UUID profileId) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        //
        this.username = username;
        this.email = email;
        this.password = password;
        this.profileId = profileId;
    }

    public void update(String newUsername, String newEmail, String newPassword, UUID profiledId) {
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

        if(profiledId != null && !profiledId.equals(this.profileId)){
            this.profileId = profiledId;
            anyValueUpdated = true;
        }

        if (anyValueUpdated) {
            this.updatedAt = Instant.now();
        }
    }

    public boolean isOnline() {
        if (this.userStatus == null) return false;
        return this.userStatus.isConnected();
    }

//    public UUID getId() {
//         return id;             롬복 게터 적용
//    }

//    public Long getCreatedAt() {
//        return createdAt;
//    }

//    public Long getUpdatedAt() {
//        return updatedAt;
//    }

//    public String getUsername() {
//        return username;
//    }

//    public String getEmail() {
//        return email;
//    }

//    public String getPassword() {
//        return password;
//    }
}
