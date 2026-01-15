package com.sprint.mission.entity;

import java.util.UUID;

public class Message extends Entity {
    private final UUID userId;
    private final UUID channelId;

    public Message(String message, UUID userId, UUID channelId) {
        super(message);
        this.userId = userId;
        this.channelId = channelId;
    }

    public String getMessage() {
        return getValue();
    }

    public boolean isEqualToUserId(UUID userId) {
        return this.userId.equals(userId);
    }

    public boolean isEqualToChannelId(UUID channelId) {
        return this.channelId.equals(channelId);
    }
}
