package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Message extends BaseEntity{

    private User user;
    private Channel channel;
    private String content;

    public Message(User user, Channel channel, String content) {
//        this.id = UUID.randomUUID();
//        this.createdAt = System.currentTimeMillis();
//        this.updatedAt = this.createdAt;
        super();
        this.user = user;
        this.channel = channel;
        this.content = content;
    }

    public void update(String content, User user, Channel channel) {
        this.content = content;
        this.user = user;
        this.channel = channel;
        touch(); // BaseEntity의 updatedAt 갱신
    }

    public UUID getId() {
        return id;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public String getContent() {
        return content;
    }
    public User getUser() {
        return user;
    }

    public Channel getChannel() {
        return channel;
    }


    @Override
    public String toString() {
        return "Message{" +
                "user=" + user +
                ", channel=" + channel +
                ", content='" + content + '\'' +
                '}';
    }
}
