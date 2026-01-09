package com.sprint.mission.discodeit.entity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class User {
    private final UUID id; // 객체 식별을 위한 id
    private final Long createdAt; // 객체 생성 시간(유닉스 타임스탬프)
    private Long updatedAt; // 객체 수정 시간(유닉스 타임스탬프)

    private String email;
    private String nickName;
    private String userName;
    private String password;
    private String birthday;
    // 어느 채널에 소속되어 있는지
    private final List<Channel> channelList;
    // 보낸 메시지들?
    private final List<Message> messageList;

    // 생성자
    public User(String email, String nickName, String userName, String password, String birthday) {
        // `id` 초기화
        this.id = UUID.randomUUID();
        // `createdAt` 초기화
        this.createdAt = Instant.now().toEpochMilli();
        this.updatedAt = this.createdAt;

        this.email = email;
        this.nickName = nickName;
        this.userName = userName;
        this.password = password;
        this.birthday = birthday;

        channelList = new ArrayList<>();
        messageList = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "User{" +
                "id = " + id + ", " +
                "createdAt = " + createdAt + ", " +
                "updatedAt = " + updatedAt + ", " +
                "email = " + email + ", " +
                "nickName = " + nickName + ", " +
                "userName = " + userName + ", " +
                "password = " + password + ", " +
                "birthday = " + birthday +
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

    public String getEmail() {
        return email;
    }

    public String getNickName() {
        return nickName;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public String getBirthday() {
        return birthday;
    }

    public List<Channel> getChannelList() {
        return channelList;
    }

    public List<Message> getMessageList() {
        return messageList;
    }

    // update 시간 메소드
    public void updateTime() {
        this.updatedAt = Instant.now().toEpochMilli();
    }

    // update
    public void updateEmail(String email) {
        this.email = email;
        updateTime();
    }

    public void updateNickName(String nickName) {
        this.nickName = nickName;
        updateTime();
    }

    public void updateUserName(String userName) {
        this.userName = userName;
        updateTime();
    }

    public void updatePassword(String password) {
        this.password = password;
        updateTime();
    }

    public void updateBirthday(String birthday) {
        this.birthday = birthday;
        updateTime();
    }
}
