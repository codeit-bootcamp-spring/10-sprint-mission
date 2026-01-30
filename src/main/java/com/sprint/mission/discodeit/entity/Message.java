package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class Message extends BaseEntity implements  Serializable {
    private static final long serialVersionUID = 1L;

    private UUID senderId;
    private UUID channelId;
    private String content;
    private List<UUID> attachmentIds;   // 첨부파일

    public Message(UUID senderId, UUID channelId, String content, List<UUID> attachmentIds) {
        super(UUID.randomUUID(), Instant.now());
        this.channelId = channelId;
        this.content = content;
        this.senderId = senderId;
        if (attachmentIds == null) {
            this.attachmentIds = new ArrayList<>();
        }
        else{
            this.attachmentIds = attachmentIds;
        }
    }

    public void updateContent(String newContent) {
        this.content = newContent;
    }

    public void updateAttachmentIds(List<UUID> newAttachmentIds) {
        this.attachmentIds = newAttachmentIds;
    }
}
