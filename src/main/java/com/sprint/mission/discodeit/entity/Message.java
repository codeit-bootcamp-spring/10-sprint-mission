package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.util.UUID;

public class Message extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @Getter
    private String content;
    @Getter
    private final UUID authorId;
    @Getter
    private final UUID channelId;

    public Message(String content, UUID channelId, UUID authorId) {
        this.content = content;
        this.authorId = authorId;
        this.channelId = channelId;
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
