package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.util.*;

@Getter
public class User extends MutableEntity {
    private final Set<UUID> joinedChannels;
    private final List<UUID> messageHistory;
    private String accountId;           // 계정ID, 상속받은id(UUID)와 다름, 헷갈림 주의
    private String password;
    private String username;
    private String email;
    private UUID profileId;

    public User(String accountId, String password, String username, String email) {
        super();
        this.joinedChannels = new HashSet<>();
        this.messageHistory = new ArrayList<>();
        this.accountId = accountId;
        this.password = password;
        this.username = username;
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


    public void updateAccountId(String accountId) {
        this.accountId = accountId;
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
        return String.format("'계정ID: %s / 이름: %s / 메일: %s'",
                getAccountId(), getUsername(), getEmail());
    }
}