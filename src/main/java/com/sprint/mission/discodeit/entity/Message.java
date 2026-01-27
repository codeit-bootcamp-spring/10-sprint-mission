package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Message extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private String content;
    private final UUID authorId;
    private final UUID channelId;

    public Message(String content, UUID channelId, UUID authorId) {
        this.content = content;
        this.authorId = authorId;
        this.channelId = channelId;
    }

    public String getContent() {
        return content;
    }

    public UUID getAuthorId() {
        return authorId;
    }

    public UUID getChannelId() {
        return channelId;
    }

    public void update(String content) {
        this.content = content;
        setUpdateAt();
    }

    @Override
    public String toString() {
        return content;
    }
}
