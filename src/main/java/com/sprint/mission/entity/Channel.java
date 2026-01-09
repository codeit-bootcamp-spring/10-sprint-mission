package com.sprint.mission.entity;

import java.util.UUID;

public class Channel {
    private UUID id;
    private String name;
    private Long createdAt;
    private Long updatedAt;

    public Channel(String name) {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.name = getValidatedTrimmedName(name);
    }

    public void updateName(String name) {
        this.name = getValidatedTrimmedName(name);
        touch();
    }

    private String getValidatedTrimmedName(String name) {
        validatedNameIsNotBlank(getTrimmedName(name));
        return getTrimmedName(name);
    }

    private void validatedNameIsNotBlank(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("채널명이 비어있을 수 없습니다.");
        }
    }

    private String getTrimmedName(String name) {
        return name.trim();
    }

    private void touch() {
        this.updatedAt = System.currentTimeMillis();
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public String getName() {
        return name;
    }

    public UUID getId() {
        return id;
    }
}
