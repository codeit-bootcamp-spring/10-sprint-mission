package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Message extends BaseEntity {
    private final UUID channelId;
    private final UUID userId;
    private String content;
    private final long sentAt;

    public Message (UUID userId, UUID channelId, String content) {
        super();
        this.userId = userId;
        this.channelId = channelId;
        this.content = content;
        this.sentAt = System.currentTimeMillis();
    }

    public void setContent(String setMessage) {
        content = setMessage;
        setUpdatedAt(System.currentTimeMillis());
    }

    public String getContent() {
        return content;
    }

    public long getSentAt() {
        return sentAt;
    }


    public UUID getChannelId() { return channelId; }
    public UUID getUserId() { return userId; }

}
