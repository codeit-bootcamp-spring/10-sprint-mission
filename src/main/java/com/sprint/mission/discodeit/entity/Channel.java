package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Channel {
    private final UUID id;
    private final Long createdAt;
    private Long updatedAt;
    private String name;

    public Channel(String name) {
        this.id = UUID.randomUUID(); // 생성자에서 초기화
        this.createdAt = System.currentTimeMillis(); // 생성자에서 초기화
        this.updatedAt = this.createdAt; // 생성 시점으로 초기화
        this.name = name;
    }

    // Getter 메소드
    public UUID getId() { return id; }
    public Long getCreatedAt() { return createdAt; }
    public Long getUpdatedAt() { return updatedAt; }
    public String getName() { return name; }

    // update 메소드
    public void update(String name) {
        this.name = name;
        this.updatedAt = System.currentTimeMillis(); // 업데이트 시각 갱신
    }

}
