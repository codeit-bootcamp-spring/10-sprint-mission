package com.sprint.mission.discodeit.entity;

import java.time.Instant;
import java.util.UUID;

public class Message {
    private final UUID id; // 객체 식별을 위한 id
    private final Long createdAt; // 객체 생성 시간(유닉스 타임스탬프)
    private Long updatedAt; // 객체 수정 시간(유닉스 타임스탬프)

    private final Channel messageChannel; // 메시지가 위치한 채널
    private final User author; // 메시지 작성자
    private String content; // 메시지 내용

    // 생성자
    public Message(Channel messageChannel, User author, String content) {
        // `id` 초기화
        this.id = UUID.randomUUID();
        // `createdAt` 초기화
        this.createdAt = Instant.now().toEpochMilli();
        this.updatedAt = this.createdAt;

        this.messageChannel = messageChannel;
        this.author = author;
        this.content = content;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id = " + id + ", " +
                "createdAt = " + createdAt + ", " +
                "updatedAt = " + updatedAt + ", " +
                "author = " + author + ", " +
                "content = " + content +
                "}";
    }

    // Getter

    public UUID getId() {
        return id;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public Channel getMessageChannel() {
        return messageChannel;
    }

    public User getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    // update 시간 메소드
    public void updateTime() {
        this.updatedAt = Instant.now().toEpochMilli();
    }

    // update
    public void updateContent(String content) {
        this.content = content;
        updateTime();
    }
}
