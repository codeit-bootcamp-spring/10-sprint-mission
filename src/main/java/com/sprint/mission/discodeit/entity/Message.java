package com.sprint.mission.discodeit.entity;

import java.util.Objects;

public class Message extends Common {
    private final Channel channel;
    private final User user;
    private String message;

    public Message(Channel channel, User user, String message) {
        super();
        this.channel = channel;
        this.user = user;
        this.message = message;
    }

    public Channel getChannel() {
        return this.channel;
    }

    public User getUser() {
        return this.user;
    }

    public String getMessage() {
        return this.message;
    }
    public void updateMessage(String message) {
        this.message = message;
    }

    public void update(String message) {
        if (!Objects.equals(getMessage(), message)) {
            updateMessage(message);
            updateUpdatedAt();
        }
    }

    @Override
    public String toString() {
        return String.format("채널: %s\t\t유저: %s\t채팅메세지: %s", getChannel().getTitle(), getUser().getName(), getMessage());
    }
}
