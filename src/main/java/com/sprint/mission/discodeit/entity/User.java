package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID id;
    private Instant createdAt;
    private Instant updatedAt;
    //
    private String userName;
    private String email;
    private String password;
    private UUID profileId; //BinaryContent의 id와 일치

    public User(String username, String email, String password) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        //
        this.userName = username;
        this.email = email;
        this.password = password;
    }

    public void update(String newUsername, String newEmail, String newPassword, UUID newProfileId) {
        boolean anyValueUpdated = false;
        if (newUsername != null && !newUsername.equals(this.userName)) {//이름이 null이 아니고 기존꺼와 같지 않을때 변경.
            this.userName = newUsername;
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
            this.updatedAt = Instant.now();
        }
        if(newProfileId != null && !newProfileId.equals(this.profileId)) {
            this.profileId = newProfileId;
        }

    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + userName + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
