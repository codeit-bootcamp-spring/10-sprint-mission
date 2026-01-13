package com.sprint.mission.entity;

import java.util.UUID;

public class Message extends BaseEntity {
    private final UUID userId;
    private final UUID channelId;
    private String content;

    public Message(UUID userId, UUID channelId, String content) {
        super();
        this.content = getValidatedTrimmedContent(content);
        this.userId = userId;
        this.channelId = channelId;
    }

    public void updateContent(String content) {
        this.content = getValidatedTrimmedContent(content);
        touch();
    }

    private String getValidatedTrimmedContent(String content) {
        validateContentIsNotBlank(content);
        return content.trim();
    }

    private void validateContentIsNotBlank(String content) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("메시지 내용은 비어있을 수 없습니다.");
        }
    }

    public String getContent() {
        return content;
    }

    public UUID getUserId() {
        return userId;
    }

    public UUID getChannelId() {
        return channelId;
    }
}
