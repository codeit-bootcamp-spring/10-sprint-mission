package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Getter
public class Message extends BaseEntity {
    private final UUID senderId;
    private final UUID channelId;
    private String content;
    private List<UUID> attachmentIds;

    public Message(UUID senderId, UUID channelId, String content) {
        this.senderId = senderId;
        this.channelId = channelId;
        this.content = content;
        this.attachmentIds = new ArrayList<>();
    }

    public void updateAttachments(List<UUID> attachmentIds) {
        this.attachmentIds = new ArrayList<>(attachmentIds);
        markUpdated();
    }

    public void updateContent(String content) {
        Optional.ofNullable(content)
                .ifPresent(value -> this.content = value);
        markUpdated();
    }

    public void validateSender(UUID userId) {
        if (!senderId.equals(userId)) {
            throw new IllegalArgumentException("메세지의 sender가 아닙니다. userId: " + userId);
        }
    }
}
