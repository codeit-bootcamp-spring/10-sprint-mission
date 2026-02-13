package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class User extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private String username;
    private String email;
    private String password;
    private UUID profileID; // BinaryContent??id瑜?媛由ы궡
    private List<UUID> channelIds;

    public User(String username, String email, String password, UUID profileID) {
        super();
        this.username = username;
        this.email = email;
        this.password = password;
        this.profileID = profileID;
        this.channelIds = new ArrayList<>();
    }

    public void joinChannel(UUID channelId) {
        if (channelId == null) {
            throw new IllegalArgumentException("Invalid channel id");
        }
        if (channelIds == null) {
            channelIds = new ArrayList<>();
        }
        if (channelIds.stream().anyMatch(channelId::equals)) {
            throw new IllegalStateException("User already joined channel");
        }
        channelIds.add(channelId);
        this.setUpdatedAt(Instant.now());
    }

    public void leaveChannel(UUID channelId) {
        if (channelId == null) {
            throw new IllegalArgumentException("Invalid channel id");
        }
        if (channelIds == null) {
            channelIds = new ArrayList<>();
        }
        if (channelIds.stream().noneMatch(channelId::equals)) {
            throw new IllegalStateException("User is not in channel");
        }
        channelIds.remove(channelId);
        this.setUpdatedAt(Instant.now());
    }

    public void update(String newUsername, String newEmail, String newPassword, UUID profileId) {
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

        if (profileId != null && !profileId.equals(this.profileID)) {
            this.profileID = profileId;
            anyValueUpdated = true;
        }

        if (anyValueUpdated) {
            this.setUpdatedAt(Instant.now());
        }
    }
}
