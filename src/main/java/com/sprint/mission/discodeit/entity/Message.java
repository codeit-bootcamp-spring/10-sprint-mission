package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Message extends BaseDomain {
    // 필드
    private String msg;

    // 생성자
    public Message(String msg) {
        super();
        this.msg = msg;
    }

    // 메소드
    public UUID getId() {
        return this.id;
    }

    public long getCreatedAt() {
        return this.createdAt;
    }

    public long getUpdatedAt() {
        return this.updatedAt;
    }

    public void updateText(String msg) {
        this.msg = msg;
        this.updatedAt = System.currentTimeMillis();
    }

    public String toString() {
        return "이 메시지의 id: " + this.id + "\n"
                + "메시지 내용: " + this.msg + "\n"
                + "생성일: " + this.createdAt + "\n"
                + "변경일: " + this.updatedAt;
    }
}
