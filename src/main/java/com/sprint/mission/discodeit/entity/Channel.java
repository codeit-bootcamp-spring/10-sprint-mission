package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
public class Channel extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private IsPrivate isPrivate;
    private User owner; // 채널 주인
    private List<User> users;   // 채널 멤버
    private List<Message> messages; // 채널에서 주고받은 메시지들

    public Channel(String name, IsPrivate isPrivate, User owner) {
        super(UUID.randomUUID(), System.currentTimeMillis());
        this.name = name;
        this.isPrivate = isPrivate;
        this.users = new ArrayList<>();
        this.messages = new ArrayList<>();
        addOwner(owner);
    }

    public void addMessage(Message message) {
        if (message == null) {
            return;
        }
        if(!messages.contains(message)) {
            messages.add(message);
        }
        if (message.getChannelId() != this.id) {
            message.setChannel(this);
        }
    }

    public void addUser(User user) {
        boolean isAlreadyJoind = users.stream()
                .anyMatch(u -> u.getId().equals(user.getId()));
        if (!isAlreadyJoind) {
            this.users.add(user);
        }
        if (!user.getChannels().contains(this)) {
            user.addChannel(this);
        }
    }

    public void addOwner(User owner){
        this.owner = owner;
        if (!owner.getChannels().contains(this)) {
            owner.addChannel(this);
        }
    }

    public void updateOwner(User owner) {
        this.owner = owner;
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void updatePrivate(IsPrivate isPrivate) {
        this.isPrivate = isPrivate;
    }

    @Override
    public String toString() {
        String memberNames = users.stream()
                .map(User::getName)
                .collect(Collectors.joining(", "));
        String messages = this.messages.stream()
                .map(Message::toString)
                .collect(Collectors.joining("\n"));
        return "채널명 : " + name + ", 공개여부 : " + isPrivate + ", 채널장 : " + owner.getName();
    }

}
