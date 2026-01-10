package com.sprint.mission.discodeit.entity;

import java.time.Instant;
import java.util.UUID;

public class Message extends BaseEntity {
    private final Channel messageChannel; // 메시지가 위치한 채널
    private final User author; // 메시지 작성자
    private String content; // 메시지 내용

    // 생성자
    public Message(Channel messageChannel, User author, String content) {
        this.messageChannel = messageChannel;
        this.author = author;
        this.content = content;
    }

    @Override
    public String toString() {
        return "Message{" +
                "messageId = " + getId() + ", " +
                "createdAt = " + getCreatedAt() + ", " +
                "updatedAt = " + getUpdatedAt() + ", " +
                "author = " + author + ", " +
                "content = " + content +
                "}";
    }

    // Getter
    public Channel getMessageChannel() {
        return messageChannel;
    }

    public User getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    // update
    public void updateContent(String content) {
        this.content = content;
        updateTime();
    }
}
