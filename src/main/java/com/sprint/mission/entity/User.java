package com.sprint.mission.entity;

import java.util.*;

public class User extends Entity {
    private final Set<UUID> channelIDs;
    private final Set<UUID> messageIDs;

    public User(String userName) {
        super(userName);
        this.channelIDs = new HashSet<>();
        this.messageIDs = new HashSet<>();
    }

    public void addChannelID(UUID uuid) {
        this.channelIDs.add(uuid);
    }

    public void addMessageID(UUID uuid) {
        this.messageIDs.add(uuid);
    }

    public String getUserName() {
        return getValue();
    }

    public Set<UUID> getChannelIDs() {
        return Set.copyOf(channelIDs);
    }

    public Set<UUID> getMessageIDs() {
        return Set.copyOf(messageIDs);
    }

    public boolean hasChannelId(UUID channelId) {
        return this.channelIDs.contains(channelId);
    }

    public boolean hasMessageId(UUID messageId) {
        return this.messageIDs.contains(messageId);
    }
}
