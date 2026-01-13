package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.util.UUID;

public class Channel {
    // 필드
    private UUID id;
    private JCFUserService userMap;
    private long createdAt;
    private long updatedAt;

    // 생성자
    public Channel() {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.userMap = new JCFUserService();
    }
    public Channel(UUID id) {
        this.id = id;
        this.createdAt = System.currentTimeMillis();
    }
    public Channel(UUID id, long createdAt) {
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
        return "이 채널의 id: " + this.id + "\n"
                + "생성일: " + this.createdAt + "\n"
                + "변경일: " + this.updatedAt;
    }
}
