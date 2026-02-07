package com.sprint.mission.discodeit.entity;

import jakarta.validation.constraints.NotBlank;
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
    //
    private String content;
    //
    private UUID channelId;
    private UUID authorId;
    private List<UUID> attachmentIds; // BinaryContent의 id를 참조하기 위한 리스트
    // 생성자에서 생성해야하는지, 파라미터로 받아야하는지???

    public Message(String content, UUID channelId, UUID authorId, List<UUID> attachmentIds) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        //
        this.content = content;
        this.channelId = channelId;
        this.authorId = authorId;
        this.attachmentIds = attachmentIds;
    }

//    @Getter로 대체
//    public UUID getId() {
//        return id;
//    }
//
//    public Long getCreatedAt() {
//        return createdAt;
//    }
//
//    public Long getUpdatedAt() {
//        return updatedAt;
//    }
//
//    public String getContent() {
//        return content;
//    }
//
//    public UUID getChannelId() {
//        return channelId;
//    }
//
//    public UUID getAuthorId() {
//        return authorId;
//    }

    public void update(String newContent, List<UUID> newAttachmentIds) {
        boolean anyValueUpdated = false;
        if (newContent != null && !newContent.equals(this.content)) {
            this.content = newContent;
            anyValueUpdated = true;
        }
        if (newAttachmentIds != null && !newAttachmentIds.equals(this.attachmentIds)) {
            this.attachmentIds = newAttachmentIds;
            anyValueUpdated = true;
        }

        if (anyValueUpdated) {
            this.updatedAt = Instant.now();
        }
    }
}
