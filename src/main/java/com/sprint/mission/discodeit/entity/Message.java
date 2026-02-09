package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.util.List;
import java.util.UUID;

public class Message extends BaseEntity {
    @Getter
    private String content;
    @Getter
    private final UUID authorId;
    @Getter
    private final UUID channelId;
    @Getter
    private final List<UUID> attachmentIds;

    public Message(String content, UUID authorId, UUID channelId, List<UUID> attachmentIds) {
        this.content = content;
        this.authorId = authorId;
        this.channelId = channelId;
        this.attachmentIds = attachmentIds;
    }

    public void update(String content) {
        this.content = content;
        setUpdatedAt();
    }
}
