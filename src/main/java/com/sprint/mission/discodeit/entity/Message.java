package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class Message extends CommonEntity{
    private static final long serialVersionUID = 1L;
    private String content;
    private final UUID senderId;
    private final UUID channelId;

    public Message(String content, UUID sender, UUID channel) {
        this.content = content;
        this.senderId = sender;
        this.channelId = channel;
    }

    public void updateContent(String content) {
        this.content = content;
        this.updateAt = Instant.now();
    }
}
