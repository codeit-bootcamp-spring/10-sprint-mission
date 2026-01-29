package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class User extends CommonEntity{
    private static final long serialVersionUID = 1L;
    private String userName;
    private transient String password;
    private String email;
    @Setter
    private UUID profileId;
    private final List<UUID> channelIds = new ArrayList<>();
    private final List<UUID> messageIds = new ArrayList<>();

    public User(String userName, String password, String email) {
        this.userName = userName;
        this.password = password;
        this.email = email;
    }

    public List<UUID> getChannelIds() {
        return List.copyOf(channelIds);
    }

    public List<UUID> getMessageIds() {
        return List.copyOf(messageIds);
    }

    public void updateUserName(String userName) {
        this.userName = userName;
        this.updateAt = Instant.now();
    }

    public void updatePassword(String password) {
        this.password = password;
        this.updateAt = Instant.now();
    }

    public void updateEmail(String email) {
        this.email = email;
        this.updateAt = Instant.now();
    }

    public void addChannelId(UUID channelId) {
        channelIds.add(channelId);
        this.updateAt = Instant.now();
    }

    public void removeChannelId(UUID channelId) {
        channelIds.remove(channelId);
        this.updateAt = Instant.now();
    }

    public void addMessageId(UUID messageId) {
        messageIds.add(messageId);
        this.updateAt = Instant.now();
    }

    public void removeMessageId(UUID messageId) {
        messageIds.remove(messageId);
        this.updateAt = Instant.now();
    }

    public boolean isProfileImageUploaded() {
        return profileId != null;
    }
}
