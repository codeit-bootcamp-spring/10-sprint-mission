package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class Message extends CommonEntity{
    private static final long serialVersionUID = 1L;
    private String content;
    private final UUID senderId;
    private final UUID channelId;
    private final List<UUID> attachmentIds;

    public Message(String content, UUID sender, UUID channel, List<UUID> attachmentIds) {
        this.content = content;
        this.senderId = sender;
        this.channelId = channel;
        this.attachmentIds = attachmentIds;
    }

    public void updateContent(String content) {
        this.content = content;
        this.updateAt = Instant.now();
    }

    public List<UUID> getAttachmentIds() {
        return List.copyOf(attachmentIds);
    }

    public void addAttachmentId(UUID attachmentId) {
        attachmentIds.add(attachmentId);
    }

    public void removeAttachmentId(UUID attachmentId) {
        attachmentIds.remove(attachmentId);
    }
}
