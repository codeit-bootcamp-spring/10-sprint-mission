package com.sprint.mission.discodeit.user.entity;

import com.sprint.mission.discodeit.binarycontent.dto.BinaryContentResponse;
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
    private @Setter UserStatus userStatus;
    private BinaryContentResponse profileImage;
    private UUID profileId;


    public User(String username, String email, String password, BinaryContentResponse profileImage) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        //
        this.username = username;
        this.email = email;
        this.password = password;
        this.profileImage = profileImage;
    }

    public void update(String newUsername, String newEmail, String newPassword, BinaryContentResponse profileImage) {
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

        if(profileImage != null && !profileImage.equals(this.profileImage)){
            this.profileImage = profileImage;
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
}
