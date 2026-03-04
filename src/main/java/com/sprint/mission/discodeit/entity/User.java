package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.util.*;

@Getter
public class User extends MutableEntity {
    private final Set<UUID> joinedChannels;
    private final List<UUID> messageHistory;
    private String username;
    private String password;
    private String email;
    private UUID profileId;

    public User(String username, String password, String email) {
        super();
        this.joinedChannels = new HashSet<>();
        this.messageHistory = new ArrayList<>();
        this.username = username;
        this.password = password;
        this.email = email;
        this.profileId = null;
    }

    // joinedChannels
    public Set<UUID> getJoinedChannels() {
        return Collections.unmodifiableSet(this.joinedChannels);
    }

    public void addJoinedChannels(UUID channelId) {
        this.joinedChannels.add(channelId);
    }

    public void removeJoinedChannels(UUID channelId) {
        this.joinedChannels.remove(channelId);
    }

    // messageHistory
    public List<UUID> getMessageHistory() {
        return Collections.unmodifiableList(this.messageHistory);
    }

    public void addMessageHistory(UUID messageId) {
        this.messageHistory.add(messageId);
    }

    public void removeMessageHistory(UUID messageId) {
        this.messageHistory.remove(messageId);
    }


    public void updateUserName(String username) {
        this.username = username;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public void updateEmail(String email) {
        this.email = email;
    }

    public void updateProfileId(UUID profileId) {
        this.profileId = profileId;
    }

    @Override
    public String toString() {
        return String.format("'유저이름: %s / 메일: %s'",
                getUsername(), getEmail());
    }
}