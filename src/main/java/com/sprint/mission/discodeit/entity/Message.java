package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
public class Message extends BaseEntity {

    private String content;
    private final UUID authorId;
    private final UUID channelId;
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
