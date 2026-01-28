package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Instant;

@Getter
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

    public void updateText(String text) {
        this.text = text;
        updateUpdatedAt(Instant.now());
    }

    @Override
    public String toString() {
        return "{" +
            channel.getName() + ">" +
            sender.getNickName() + ": " +
            text +
            "(" + getUpdatedAt() + ")}";
    }
}
