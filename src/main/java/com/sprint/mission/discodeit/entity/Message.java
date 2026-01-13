package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Message extends Common {
    private final UUID channelId;
    private final UUID userId;
    private String message;

    public Message(UUID channelId, UUID userId, String message) {
        super();
        this.channelId = channelId;
        this.userId = userId;
        this.message = message;
    }

    public UUID getChannelId() {
        return this.channelId;
    }

    public UUID getUserId() {
        return this.userId;
    }

    public String getMessage() {
        return this.message;
    }
    public void updateMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return String.format("채널ID: %s\t유저ID: %s\t채팅메세지: %s", getChannelId(), getUserId(), getMessage());
    }
}
