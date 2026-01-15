package com.sprint.mission.entity;

import java.util.*;

public class Channel extends Entity {
    private final Set<UUID> messageIDs;
    private final Set<UUID> userIDs;

    public Channel(String channelName) {
        super(channelName);
        this.messageIDs = new HashSet<>();
        this.userIDs = new HashSet<>();
    }

    public void addUserID(UUID uuid) {
        this.userIDs.add(uuid);
    }

    public void addMessageID(UUID uuid) {
        this.messageIDs.add(uuid);
    }

    public String getChannelName() {
        return getValue();
    }

    public Set<UUID> getMessageIDs() {
        return Set.copyOf(messageIDs);
    }

    public Set<UUID> getUserIDs() {
        return Set.copyOf(userIDs);
    }

    public boolean hasMessageId(UUID messageId) {
        return this.messageIDs.contains(messageId);
    }

    public boolean hasUserId(UUID userId) {
        return this.userIDs.contains(userId);
    }
}
