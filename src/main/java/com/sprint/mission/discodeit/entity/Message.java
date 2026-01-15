package com.sprint.mission.discodeit.entity;

public class Message extends BaseEntity{
    private String text;
    private final User user;
    private final Channel channel;

    public Message(String text, User user, Channel channel) {
        this.text = text;
        this.user = user;
        this.channel = channel;
    }

    public User getUser() {
        return user;
    }

    public Channel getChannel() {
        return channel;
    }

    public String getText() {
        return text;
    }

    public void update(String text) {
        this.text = text;
        setUpdateAt();
    }

    @Override
    public String toString() {
        return channel + "-" + user + ": " + text;
    }
}
