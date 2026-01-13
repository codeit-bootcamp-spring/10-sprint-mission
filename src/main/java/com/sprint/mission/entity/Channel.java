package com.sprint.mission.entity;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Channel extends BaseEntity {
    private final UUID ownerId;
    private final Set<UUID> users = new HashSet<>();
    private String name;
//    private List<Message> messages;

    public Channel(UUID ownerId, String name) {
        super();
        this.name = getValidatedTrimmedName(name);
        this.ownerId = ownerId;
        users.add(ownerId);
    }

    public void updateName(String name) {
        this.name = getValidatedTrimmedName(name);
        touch();
    }

    public void joinUser(UUID userId) {
        users.add(userId);
    }

    public void leaveUser(UUID userId) {
        users.remove(userId);
    }

    public Set<UUID> getUsers() {
        return users;
    }

    public String getName() {
        return name;
    }

    public UUID getOwnerId() {
        return ownerId;
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
}
