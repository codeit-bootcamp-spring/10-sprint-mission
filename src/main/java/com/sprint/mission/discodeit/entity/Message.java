package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
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
    private List<UUID> attachmentIds; //BinaryContent id의 List(한 메시지당 여러 파일 첨부 가능)

    public Message(String content, UUID channelId, UUID authorId,List<UUID> attachmentIds) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        //
        this.content = content;
        this.channelId = channelId;
        this.authorId = authorId;
        this.attachmentIds = attachmentIds != null
                ? new ArrayList<>(attachmentIds)
                : new ArrayList<>();


    }

    public void update(String newContent,List<UUID> newAttachmentIds) {
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
