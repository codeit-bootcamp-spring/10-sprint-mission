package com.sprint.mission.entity;

import java.util.UUID;

public class User {
    private final UUID id;
    private final Long createdAt;
    private String nickName;
    private Long updatedAt;

    public User(String nickName) {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.nickName = getValidatedTrimmedInput(nickName);
    }

    public void updateNickName(String nickName) {
        this.nickName = getValidatedTrimmedInput(nickName);
        touch();
    }

    private String getValidatedTrimmedInput(String input) {
        validateContentIsNotBlank(input);
        return input.trim();
    }

    private void validateContentIsNotBlank(String input) {
        if (input == null || input.isBlank()) {
            throw new IllegalArgumentException("닉네임이 비어있을 수 없습니다.");
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

    public String getNickName() {
        return nickName;
    }
}
