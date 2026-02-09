package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.util.*;

public class User extends BaseEntity {
    @Getter
    private String username;
    @Getter
    private String email;
    @Getter
    private String password;
    @Getter
    private UUID profileId;

    public User(String username, String email, String password, UUID profileId) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.profileId = profileId;
    }

    public void updateUsername(String username) {
        this.username = username;
        setUpdatedAt();
    }

    public void updateEmail(String email) {
        this.email = email;
        setUpdatedAt();
    }

    public void updatePassword(String newPassword) {
        this.password = newPassword;
        setUpdatedAt();
    }

    public void updateProfileId(UUID profileId) {
        this.profileId = profileId;
        setUpdatedAt();
    }
}
