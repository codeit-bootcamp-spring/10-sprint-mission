package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;


@Getter
public class Message extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

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

    @Override
    public String toString() {
        Instant baseTime = (getUpdatedAt() != null) ? getUpdatedAt() : getCreatedAt();

        LocalTime time = baseTime
                .atZone(ZoneId.systemDefault())
                .toLocalTime();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        return sender.getName() + ": " + content + " (" + time.format(formatter) + ")";
    }
}