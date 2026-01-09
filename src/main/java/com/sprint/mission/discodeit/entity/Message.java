package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Message {
    private final UUID id;
    private final Long createdAt;
    private Long updatedAt;
    private String userId;
    private String text;

    public Message(String userId, String text) {
        this.id = UUID.randomUUID(); // 생성자에서 초기화
        this.createdAt = System.currentTimeMillis(); // 생성자에서 초기화
        this.updatedAt = this.createdAt; // 생성 시점으로 초기화
        this.userId = userId;
        this.text = text;
    }

    // Getter 메소드
    public UUID getId() { return id; }
    public Long getCreatedAt() { return createdAt; }
    public Long getUpdatedAt() { return updatedAt; }
    public String getUserId() { return userId; }
    public String getText() { return text; }


    // update 메소드
    public void update(String userId, String text) {
        this.userId = userId;
        this.text = text;
        this.updatedAt = System.currentTimeMillis(); // 업데이트 시각 갱신
    }
    public void updateId(String userId) {
        this.userId = userId;
        this.updatedAt = System.currentTimeMillis(); // 업데이트 시각 갱신
    }
    public void updateText(String text) {
        this.text = text;
        this.updatedAt = System.currentTimeMillis(); // 업데이트 시각 갱신
    }

}
