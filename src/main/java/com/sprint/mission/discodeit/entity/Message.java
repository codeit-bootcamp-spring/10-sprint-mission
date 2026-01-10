package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Message extends BaseEntity{
    private String content;
    private final User author;
    private final Channel channel;

    public Message(Channel channel, User author, String content) {
        super();
        this.content = content;
        this.author = author;
        this.channel = channel;
    }
    public String getContent() {
        return content;
    }
    public UUID getUserId() {
        return author.getId();
    }
    public UUID getChannelId() { return channel.getId(); }
    public void update(String content) {
        this.content = content;
        updateTimestamps();
    }
    @Override
    public String toString() {
        return "메시지[채널: " + channel.getName() +
                ", 작성자: " + author.getName() +
                ", 내용: " + content +"]";
    }
}
