package com.sprint.mission.discodeit.entity;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;


public class Message {
    private final UUID messageId;    // 메시지 식별자
    private final User sender;       // 보낸 사람
    private String content;          // 메시지 내용
    private final Long messageCreatedAt;    // 생성 시각
    private Long messageUpdatedAt;          // 수정 시각 (없으면 null)

    public Message(User sender, String content) {
        this.messageId = UUID.randomUUID();
        this.sender = sender;
        this.content = content;
        this.messageCreatedAt = System.currentTimeMillis();
        this.messageUpdatedAt = null;  // 수정 전에는 null
    }

    // 메시지 내용 수정
    public void updateContent(String newContent) {
        this.content = newContent;
        this.messageUpdatedAt = System.currentTimeMillis();
    }

    // 외부에서 값 확인
    public UUID getMessageId() {
        return messageId;
    }

    public User getSender() {
        return sender;
    }

    public String getContent() {
        return content;
    }

    public Long getCreatedAt() {
        return messageCreatedAt;
    }

    public Long getUpdatedAt() {
        return messageUpdatedAt;
    }

    @Override
    public String toString() {
        long displayTime = (messageUpdatedAt != null) ? messageUpdatedAt : messageCreatedAt;
        java.time.LocalTime time = java.time.Instant.ofEpochMilli(displayTime)
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalTime();
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss");
        return sender.getUserName() + ": " + content + " (" + time.format(formatter) + ")";
    }
}
