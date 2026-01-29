package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.util.Objects;

@Getter
public class Message extends MutableEntity {
    private Channel channel;
    private User user;
    private String message;

    public Message(Channel channel, User user, String message) {
        super();
        this.channel = channel;
        this.user = user;
        this.message = message;
    }

    public void updateChannelIfSameId(Channel channel) {
        if (Objects.equals(getChannel().getId(), channel.getId())) {
            this.channel = channel;
        }
    }

    public void updateUserIfSameId(User user) {
        if (Objects.equals(getUser().getId(), user.getId())) {
            this.user = user;
        }
    }

    public void updateMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return String.format("'채널명: %s / 유저: %s / 채팅메세지: %s'", getChannel().getTitle(), getUser().getName(), getMessage());
    }
}
