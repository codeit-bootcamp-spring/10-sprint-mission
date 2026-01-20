package com.sprint.mission.entity;

import java.util.Objects;
import java.util.UUID;

public class Message extends Entity<Message> {
    private final User user;
    private final Channel channel;
    private String content;

    private Message(Message obj) {
        super(obj);
        this.user = obj.user;
        this.channel = obj.channel;
        this.content = obj.content;
    }

    public Message(User user, Channel channel, String content) {
        super();
        this.user = Objects.requireNonNull(user);
        this.channel = Objects.requireNonNull(channel);
        this.content = Objects.requireNonNull(content);
    }

    @Override
    public Message copy() {
        return new Message(this);
    }

    @Override
    public void update(String content) {
        this.content = content;
        updateTime();
    }

    public String getContent() {
        return content;
    }

    public boolean isAuthoredBy(UUID userId) {
        return this.user.getId().equals(userId);
    }

    public boolean isPostedIn(UUID channelId) {
        return this.channel.getId().equals(channelId);
    }

    @Override
    public String toString() {
        return String.format("content: %s\n%s", content, super.toString());
    }
}
