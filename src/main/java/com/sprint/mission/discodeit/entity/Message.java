package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
public class Message extends DefaultEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    //
    private String content;
    //
    private UUID channelId;
    private UUID authorId;
    private List<UUID> attachments;

    public Message(String content, UUID channelId, UUID authorId, List<UUID> attachments) {
        super();
        //
        this.content = content;
        this.channelId = channelId;
        this.authorId = authorId;
        this.attachments = attachments;
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
}
