package com.sprint.mission.discodeit.entity;

import java.util.Objects;

public class Message extends Common {
    private static final long serialVersionUID = 1L;
    private Channel channel;
    private User user;
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
    public void updateChannelIfSameId(Channel channel) {
        if (Objects.equals(getChannel().getId(), channel.getId())) {
            this.channel = channel;
        }
    }

    public User getUser() {
        return this.user;
    }
    public void updateUserIfSameId(User user) {
        if (Objects.equals(getUser().getId(), user.getId())) {
            this.user = user;
        }
    }

    public String getMessage() {
        return this.message;
    }
    public void updateMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return String.format("'채널명: %s / 유저: %s / 채팅메세지: %s'", getChannel().getTitle(), getUser().getName(), getMessage());
    }
}
