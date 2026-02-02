package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Setter
@Getter
@RequiredArgsConstructor
public class Message extends BaseEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private UUID id;
    private String content;
    private UUID channelId;
    private UUID authorId;
    private Instant createdAt;
    private Instant updatedAt;


    public Message(String content, UUID channelId, UUID authorId) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        //
        this.content = content;
        this.channelId = channelId;
        this.authorId = authorId;
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
