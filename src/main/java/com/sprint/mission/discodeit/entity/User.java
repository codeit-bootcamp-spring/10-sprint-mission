package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class User {
    private final UUID id;
    private final Long createdAt;
    private Long updatedAt;

    private String userName;
    private String userEmail;
    private String userPassword;


    public User(String userName, String userEmail, String userPassword) {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = this.createdAt;

        this.userName = userName;
        this.userEmail = userEmail;
        this.userPassword = userPassword;
    }

    public UUID getId() {
        return id;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public void updateUserName(String userName) {
        this.userName = userName;
    }

    public void updateUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public void updateUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }


    public String getUserStatus() {
        return "이름: " + userName + ", 이메일: " + userEmail + ", 비번: " + userPassword;
    }


}
