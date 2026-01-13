package com.sprint.mission.entity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Channel extends BaseEntity {
    private final User owner;
    private final Set<User> users;
    private String name;
//    private List<Message> messages;

    public Channel(User owner, String name) {
        super();
        this.users = new HashSet<>();
        this.name = getValidatedTrimmedName(name);
        this.owner = owner;
        joinUser(owner);
    }

    public void updateName(String name) {
        this.name = getValidatedTrimmedName(name);
        touch();
    }

    public void joinUser(User user) {
        users.add(user);
    }

    public void leaveUser(User user) {
        users.remove(user);
    }

    public List<User> getUsers() {
        return List.copyOf(users);
    }

    public String getName() {
        return name;
    }

    public User getOwner() {
        return owner;
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
