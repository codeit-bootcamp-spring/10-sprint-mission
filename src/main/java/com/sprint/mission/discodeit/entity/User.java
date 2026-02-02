package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.util.*;

@Getter
public class User extends MutableEntity {
    private final Set<UUID> joinedChannels;
    private final List<UUID> messageHistory;
    private String accountId;           // 계정ID, 상속받은id(UUID)와 다름, 헷갈림 주의
    private transient String password;
    private String name;
    private String mail;
    private UUID profileId;

    public User(String accountId, String password, String name, String mail) {
        super();
        this.joinedChannels = new HashSet<>();
        this.messageHistory = new ArrayList<>();
        this.accountId = accountId;
        this.password = password;
        this.name = name;
        this.mail = mail;
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

    public void updateJoinedChannel(UUID channelId) {
        if (this.joinedChannels.removeIf(c -> Objects.equals(c, channelId))) {
            addJoinedChannels(channelId);
        }
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

    public void updateMessageHistory(UUID messageId) {
        for (int i = 0; i < this.messageHistory.size(); i++) {
            if (this.messageHistory.get(i).equals(messageId)) {
                this.messageHistory.set(i, messageId);
                break;
            }
        }
    }

    public void updateAccountId(String accountId) {
        this.accountId = accountId;
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public void updateMail(String mail) {
        this.mail = mail;
    }

    public void updateProfileId(UUID profileId) {
        this.profileId = profileId;
    }

    @Override
    public String toString() {
        return String.format("'계정ID: %s / PW: %s / 이름: %s / 메일: %s'",
                getAccountId(), getPassword(), getName(), getMail());
    }
}