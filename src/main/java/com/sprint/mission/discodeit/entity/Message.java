package com.sprint.mission.discodeit.entity;

import java.sql.Timestamp;
import java.util.UUID;

public class Message extends Base {
    private User sender;
    private String text;
    // private File attachmentedFile;
    private Channel channel;

    public Message(User sender, String text, Channel channel) {
        this.sender = sender;
        this.text = text;
        this.channel = channel;
    }

    public User getSender() {
        return sender;
    }

    public String getText() {
        return text;
    }

    public Channel getChannel() {
        return channel;
    }

    public void updateText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "{" +
            channel.getName() + ">" +
            sender.getNickName() + ": " +
            text +
            "(" + new Timestamp(updatedAt == null ? createdAt : updatedAt) + ")}";
    }
}
