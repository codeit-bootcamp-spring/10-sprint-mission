package com.sprint.mission.discodeit.entity;

import java.util.HashSet;
import java.util.Set;

public class Channel extends BaseEntity{
    private String name;
    private String type;
    private final Set<User> users = new HashSet<>();

    public Channel(String name, String type) {
        super();
        this.name = name;
        this.type = type;
    }
    public String getName() {
        return name;
    }
    public String getType() {
        return type;
    }
    public void updateName(String name) {
        this.name = name;
        updateTimestamps();
    }
    public void updateType(String type) {
        this.type = type;
        updateTimestamps();
    }
    public void addMember(User user) {
        users.add(user);
    }
    public void removeMember(User user) {
        users.remove(user);
    }
    public Set<User> getUsers() {
        return users;
    }
    @Override
    public String toString() {
        return "채널[이름: " + name +
                ", 타입: " + type + "]";
    }
}
