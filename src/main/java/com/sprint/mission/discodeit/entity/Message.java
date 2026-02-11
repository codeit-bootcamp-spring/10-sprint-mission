package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
public class Message extends BaseEntity {
    private String content;

    private UUID channelId;
    private UUID authorId;

    // 첨부파일 id 목록 (Message 1..* BinaryContent)
    private List<UUID> attachmentIds;

    public Message(String content, UUID channelId, UUID authorId) {
        super();
        this.content = content;
        this.channelId = channelId;
        this.authorId = authorId;
    }

    // 첨부파일이 있는 경우 (생성자 오버로딩)
    public Message(String content, UUID channelId, UUID authorId, List<UUID> attachmentIds) {
        super();
        this.content = content;
        this.channelId = channelId;
        this.authorId = authorId;
        this.attachmentIds = attachmentIds;
    }

    public void update(String newContent) {
        boolean isAnyValueUpdated = false;
        if (newContent != null && !newContent.equals(this.content)) {
            this.content = newContent;
            isAnyValueUpdated = true;
        }

        if (isAnyValueUpdated) {
            updateInstant();
        }
    }
}
