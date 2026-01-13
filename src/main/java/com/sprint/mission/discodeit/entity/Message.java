package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Message {
    // 필드
    private UUID id;
    private String msg;
    private long createdAt;
    private long updatedAt;

    // 생성자
    public Message() {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
    }
    public Message(UUID id) {
        this.id = id;
        this.createdAt = System.currentTimeMillis();
    }
    public Message(UUID id, long createdAt) {
        this.id = id;
        this.createdAt = createdAt;
    }

    // 메소드
    public UUID getId() {
        return id;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void update(UUID id) {
        this.id = id;
        this.updatedAt = System.currentTimeMillis();
    }

    public String toString() {
        return "이 메시지의 id: " + this.id + "\n"
                + "생성일: " + this.createdAt + "\n"
                + "변경일: " + this.updatedAt;
    }
}
