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
        setUpdateAt();
    }

    public void updateEmail(String email) {
        this.email = email;
        setUpdateAt();
    }

    public void updatePassword(String newPassword) {
        this.password = newPassword;
        setUpdateAt();
    }

    public void updateProfileId(UUID profileId) {
        this.profileId = profileId;
        setUpdateAt();
    }

    @Override
    public String toString() {
        return username + "/" + email;
    }
}
