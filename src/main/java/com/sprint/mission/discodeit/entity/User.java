package com.sprint.mission.discodeit.entity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class User extends BaseEntity {
    private String email;
    private String nickName;
    private String userName;
    private String password;
    private String birthday;

    // 연관
    // 해당 유저가 참여 중인 채널 목록
    private final List<Channel> channelList;
    // 해당 유저가 보낸 메시지 목록
    private final List<Message> messageList;

    // 생성자
    public User(String email, String nickName, String userName, String password, String birthday) {
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
                "userId = " + getId() + ", " +
                "createdAt = " + getCreatedAt() + ", " +
                "updatedAt = " + getUpdatedAt() + ", " +
                "email = " + email + ", " +
                "nickName = " + nickName + ", " +
                "userName = " + userName + ", " +
                "password = " + password + ", " +
                "birthday = " + birthday +
                "}";
    }

    // Getter
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
