package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class Message extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private String message;         // 메시지 내용 (변경 가능)
    private Channel channel;        // 메시지를 주고 받은 채널 id (변경 불가능)
    private MessageType type;       // 메시지 타입 - 채팅, 디엠 (변경 불가능)
    private User user;              // 보낸 사람 id (변경 불가능)

    public Message(String message, User user, Channel channel, MessageType type) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.message = message;
        this.user = user;
        this.channel = channel;
        this.type = type;
    }

    public void updateMessage(String message) {
        this.message = message;
        this.updatedAt = Instant.now();
    }
}