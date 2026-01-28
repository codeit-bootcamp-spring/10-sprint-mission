package com.sprint.mission.discodeit.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class Channel extends Base {
    private final List<User> userList;
    private final List<Message> messageList;
    private String name;

    public Channel(String name) {
        this.name = name;
        this.userList = new ArrayList<User>();
        this.messageList = new ArrayList<Message>();
    }

    public String getName() {
        return name;
    }

    public List<User> getUserList() {
        return userList;
    }

    public List<Message> getMessageList() {
        return messageList;
    }

    public void updateName(String name) {
        this.name = name;
        updateUpdatedAt(System.currentTimeMillis());
    }

    // 연관관계 편의 메서드
    public void addUser(User user) {
        this.userList.add(user);
        user.getChannelList().add(this);
    }

    public void addMessage(Message message) {
        this.messageList.add(message);
    }

    @Override
    public String toString() {
        return "{" + name +
            userList.stream()
                .map(User::getNickName)
                .toList() +
            "}";

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Channel channel)) return false;
        return Objects.equals(this.getId(), channel.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.getId());
    }
}
