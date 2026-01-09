package com.sprint.mission.entity;

import java.util.UUID;

public class Message {
    private UUID id;
    private Long createdAt;
    private Long updatedAt;
    private String content;

    public Message(String content) {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.content = getValidatedTrimmedContent(content);
    }

    public void updateContent(String content) {
        this.content = getValidatedTrimmedContent(content);
        touch();
    }

    private String getValidatedTrimmedContent(String content) {
        validateContentIsNotNull(content);
        return content.trim();
    }

    private void validateContentIsNotNull(String content) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("메시지 내용은 비어있을 수 없습니다.");
        }
    }

    private void touch() {
        this.updatedAt = System.currentTimeMillis();
    }

    public UUID getId() {
        return id;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public String getContent() {
        return content;
    }
}
