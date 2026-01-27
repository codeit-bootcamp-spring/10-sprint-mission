package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Message extends CommonEntity{
    private static final long serialVersionUID = 1L;
    private String content;
    private final UUID senderId;
    private final UUID channelId;

    public Message(String content, UUID sender, UUID channel) {
        this.content = content;
        this.senderId = sender;
        this.channelId = channel;
    }

    public String getContent() {
        return content;
    }

    public UUID getSenderId() {
        return senderId;
    }

    public UUID getChannelId() {
        return channelId;
    }

    public void updateContent(String content) {
        this.content = content;
        this.updateAt = System.currentTimeMillis();
    }
}
