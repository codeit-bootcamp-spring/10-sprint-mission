package com.sprint.mission.discodeit.entity;

import java.util.ArrayList;
import java.util.List;

public class Channel extends BaseEntity {
    private String name;
    private final List<User> users = new ArrayList<>();
    private final List<Message> messages = new ArrayList<>();

    public Channel(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }


    public List<User> getUsers() { return users; }
    public List<Message> getMessages() { return messages; }


    public void addUser(User user) {
        if (!this.users.contains(user)) { // 중복 방지
            this.users.add(user);
        }
    }

    public void addMessage(Message message) {
        this.messages.add(message);
    }

    @Override
    public String toString() {
        return "Channel{" + "id=" + getId() + ", name='" + name + '\'' + '}';
    }

    public void updateName(String name) {
        this.name = name;
        this.updatedAt = System.currentTimeMillis();
    }
}