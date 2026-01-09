package com.sprint.mission.discodeit.entity;

import java.util.ArrayList;
import java.util.UUID;

public class Message {
    private final UUID id;
    private final Long createAt;
    private Long updatedAt;
    private String message; // 메시지 내용
    private final User user; // 보낸 사람
    private Channel channel; //속한 채널

    public Message(User user, String message, Channel channel) {
        this.id = UUID.randomUUID();
        this.createAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();

        this.message = message;
        this.user = user;
        this.channel = channel;
    }

    public UUID getId() {
        return id;
    }

    public Long getCreateAt() {
        return createAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public String getMessage() {
        return message;
    }

    public void updateMessage(String message) {
        this.message = message;
        updatedAt = System.currentTimeMillis();
    }

    public User getUser() {
        return user;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
        if(!channel.getMessages().contains(this)){
            channel.addMessage(this);
        }
    }

    public String toString() {
        return user.toString() + " : " + message;
    }
}
