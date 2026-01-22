package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class Message extends BaseEntity implements Serializable {

    private final User sender;
    private final Channel channel;
    private String content;

    public Message(User sender, Channel channel, String content) {
        super();
        this.sender = sender;
        this.channel = channel;
        this.content = content;
    }

    public void updateContent(String newContent) {
        this.content = newContent;
        touch();
    }

    public User getSender() {
        return sender;
    }

    public Channel getChannel() {
        return channel;
    }

    public String getContent() {
        return content;
    }

    @Override
    public String toString() {
        Long updated = getUpdatedAt();
        long baseTime = (updated != null) ? updated : getCreatedAt();
        LocalTime time = Instant.ofEpochMilli(baseTime)
                .atZone(ZoneId.systemDefault())
                .toLocalTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        return sender.getUserName() + ": " + content + " (" + time.format(formatter) + ")";
    }
}
