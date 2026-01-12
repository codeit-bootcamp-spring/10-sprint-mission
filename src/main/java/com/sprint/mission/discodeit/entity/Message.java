package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Message extends BaseEntity{
    private String message;         // 메시지 내용 (변경 가능)
    private Channel channel;        // 메시지를 주고 받은 채널 id (변경 불가능)
    private MessageType type;       // 메시지 타입 - 채팅, 디엠 (변경 불가능)
    private User user;              // 보낸 사람 id (변경 불가능)

    public Message(String message, User user, Channel channel, MessageType type) {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
        this.message = message;
        this.user = user;
        this.channel = channel;
        this.type = type;
    }

    public void updateMessage(String message) {
        this.message = message;
        this.updatedAt = System.currentTimeMillis();
    }

    public UUID getId() {   return id;  }
    public Long getCreatedAt() {    return createdAt;   }
    public Long getUpdatedAt() {    return updatedAt;   }
    public String getMessage() {    return message;     }
    public User getUser() {       return user;      }
    public Channel getChannel() {        return channel;      }
    public MessageType getType() {      return type;     }
}