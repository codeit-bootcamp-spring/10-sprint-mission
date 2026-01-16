package com.sprint.mission.entity;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class Channel extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

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

    @Override
    public boolean equals(Object obj) { // leaveUser에서 remove는 내부적으로 equal을 씀 때문에 파일에서 load할 때마다 새로운 객체이므로 equals, hashcode를 id 기반으로 오버라이딩 해줘야 함
        if (this == obj) return true;
        if (!(obj instanceof Channel)) return false;
        Channel channel = (Channel) obj;
        return Objects.equals(this.id, channel.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    private String getValidatedTrimmedName(String name) {
        validatedNameIsNotBlank(getTrimmedName(name));
        return getTrimmedName(name);
    }

    private String getTrimmedName(String name) {
        return name.trim();
    }

    public void validateMember(User user) {
        if (!users.contains(user)) {
            throw new IllegalArgumentException("해당 유저가 채널에 참여하고 있지 않습니다.");
        }
    }

    private void validatedNameIsNotBlank(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("채널명이 비어있을 수 없습니다.");
        }
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
}
