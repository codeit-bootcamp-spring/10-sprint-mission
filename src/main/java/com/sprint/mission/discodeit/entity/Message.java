package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class Message implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID id;
    private Instant createdAt;
    private Instant updatedAt;
    private String content;
    private UUID channelId;
    private UUID authorId;
    private List<UUID> attachmentIds = new ArrayList<>();

    public Message(UUID authorId, UUID channelId, String content) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        //
        this.content = content;
        this.channelId = channelId;
        this.authorId = authorId;
    }

    public void changeContent(String content) {
        this.content = content;
        this.updatedAt = Instant.now();
    }

    public void update(String newContent) {
        boolean anyValueUpdated = false;
        if (newContent != null && !newContent.equals(this.content)) {
            this.content = newContent;
            anyValueUpdated = true;
        }

        if (anyValueUpdated) {
            this.updatedAt = Instant.now();
        }
    }

    public UUID getSenderId() { return this.authorId; }

    public void updateContent(String content) {
        this.content = content;
        this.updatedAt = Instant.now();
    }

    public void assignAttachments(List<UUID> attachmentIds) {
        this.attachmentIds = attachmentIds;
    }
}
