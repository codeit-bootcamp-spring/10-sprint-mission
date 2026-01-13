package com.sprint.mission.discodeit.entity;

public class Message extends CommonEntity{
    private String content;
    private final User sender;
    private final Channel channel;

    public Message(String content, User sender, Channel channel) {
        this.content = content;
        this.sender = sender;
        this.channel = channel;
    }

    public String getContent() {
        return content;
    }

    public User getSender() {
        return sender;
    }

    public Channel getChannel() {
        return channel;
    }

    public void updateContent(String content) {
        this.content = content;
        this.updateAt = System.currentTimeMillis();
    }
}
