package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class User {
    private final UUID id;
    private final Long createdAt;
    private Long updatedAt;
    private String name;
    private String email;

    public User(String name, String email) {
        this.id = UUID.randomUUID(); // 생성자에서 초기화
        this.createdAt = System.currentTimeMillis(); // 생성자에서 초기화
        this.updatedAt = this.createdAt; // 생성 시점으로 초기화
        this.name = name;
        this.email = email;
    }

    // Getter 메소드
    public UUID getId() { return id; }
    public Long getCreatedAt() { return createdAt; }
    public Long getUpdatedAt() { return updatedAt; }
    public String getName() { return name; }
    public String getEmail() { return email; }


    // update 메소드
    public void update(String name, String email) {
        this.name = name;
        this.email = email;
        this.updatedAt = System.currentTimeMillis(); // 업데이트 시각 갱신
    }
    public void updateName(String name) {
        this.name = name;
        this.updatedAt = System.currentTimeMillis();
    }
    public void updateEmail(String email) {
        this.email = email;
        this.updatedAt = System.currentTimeMillis();
    }

}
