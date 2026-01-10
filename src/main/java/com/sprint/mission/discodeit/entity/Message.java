package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Message extends Entity {
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
        return channelId;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getMessage() {
        return message;
    }

    public Message update(String message) {
        super.update();
        this.message = message;
        return this;
    }

    @Override
    public String toString() {
        return String.format(
                "Message [id=%s, channelId=%s, userId=%s, message=%s]",
                getId().toString().substring(0, 5),
                channelId.toString().substring(0, 5),
                userId.toString().substring(0, 5),
                message
        );
    }

}
