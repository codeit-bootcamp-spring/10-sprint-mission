package com.sprint.mission.discodeit.entity;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

public class Message extends BaseEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    // Getter, update
    // field
    @Getter
    private String content;
    @Getter
    private final User user;
    @Getter
    private final Channel channel;

    // constructor
    public Message(String content, User user, Channel channel) {
        this.content = content;
        this.user = user;
        this.channel = channel;
    }

    public UUID getUserId() {
        return user.getId();
    }

    public UUID getChannelId() {
        return channel.getId();
    }

    // 메세지 수정
    public void updateContent(String content) {
        this.content = content;
        updateTimestamp();
    }
}
