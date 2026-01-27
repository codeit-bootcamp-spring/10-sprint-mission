package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
@Getter
public class User extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private UserStatus status;
    private List<Message> messages;
    private List<Channel> channels;

    public User(String name, UserStatus status) {
        super(UUID.randomUUID(), System.currentTimeMillis());
        this.name = name;
        this.status = status;
        this.messages = new ArrayList<Message>();
        this.channels = new ArrayList<Channel>();
    }

    public void addMessages(Message message) {
        this.messages.add(message);
        if (message.getSender() != this) {
            message.addSender(this);
        }
    }

    public void addChannel(Channel channel) {
        if (!this.channels.contains(channel)) {
            this.channels.add(channel);
        }

        // 채널장이 비어있으면 방장 등록
        if (channel.getOwner() == null) {
            channel.addOwner(this);
        }

        // 채널에 멤버로 추가
        if (!channel.getUsers().contains(this)) {
            channel.addUser(this);
        }
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void updateStatus(UserStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        String channleNames = channels.stream()
                .map(Channel::getName)
                .collect(Collectors.joining(","));
        return "유저명 : " + name + ", 상태 : " + status;
    }


}
