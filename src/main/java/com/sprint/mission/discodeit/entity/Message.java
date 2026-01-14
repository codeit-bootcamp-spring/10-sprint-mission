package com.sprint.mission.discodeit.entity;

import java.util.Optional;
import java.util.UUID;

public class Message extends BaseEntity {
    // field
    private String content;
    private final User user;
    private final Channel channel;

    // constructor
    public Message(String content, User user, Channel channel) {
        this.content = content;
        this.user = user;
        this.channel = channel;
    }

    // Getter, update
    public String getContent() {return content;}
    public UUID getUserId() {return user.getId();}
    public UUID getChannelId() {return channel.getId();}

    // 메세지 수정
    public void update(String content) {
        Optional.ofNullable(content).ifPresent(c -> {
            this.content = c;
            updateTimestamp();
        });
    }
}
