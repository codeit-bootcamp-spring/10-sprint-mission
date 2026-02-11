package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.util.*;

@Getter
public class Message extends MutableEntity {
    private final UUID channelId;
    private final UUID authorId;
    private final List<UUID> attachmentIds = new ArrayList<>();
    private String message;

    public Message(UUID channelId, UUID userId, String message) {
        super();
        this.channelId = channelId;
        this.authorId = userId;
        this.message = message;
    }

    public List<UUID> getAttachmentIds() {
        return Collections.unmodifiableList(this.attachmentIds);
    }
    public void addAttachmentId(UUID attachmentId) {
        this.attachmentIds.add(attachmentId);
    }
    public void removeAttachmentId(UUID attachmentId) {
        this.attachmentIds.remove(attachmentId);
    }

    public void updateMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return String.format("'채널ID: %s / 유저ID: %s / 채팅메세지: %s'", getChannelId(), getAuthorId(), getMessage());
    }
}
