package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.Message;
import lombok.Getter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class MessageDto {
    private UUID id;
    private String content;
    private UUID channelId;
    private UUID authorId;
    private List<UUID> attachmentIds;
    private Instant createdAt;
    private Instant updatedAt;

    public MessageDto(Message message) {
        this.id = message.getId();
        this.content = message.getContent();
        this.channelId = message.getChannelId();
        this.authorId = message.getAuthorId();
        this.attachmentIds = (message.getAttachmentIds() != null) ? message.getAttachmentIds() : new ArrayList<>();
        this.createdAt = message.getCreatedAt();
        this.updatedAt = message.getUpdatedAt();
    }
}
