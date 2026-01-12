package com.sprint.mission.discodeit.entity;

import java.util.ArrayList;
import java.util.UUID;

public class Message extends defaultEntity{
    private String message; // 메시지 내용
    private final User user; // 보낸 사람
    private Channel channel; //속한 채널

    public Message(User user, String message, Channel channel) {
        this.message = message;
        this.user = user;
        this.channel = channel;
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
