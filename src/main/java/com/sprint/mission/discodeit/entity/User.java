package com.sprint.mission.discodeit.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class User extends BaseEntity {
    private String email;                    // 이메일 (변경 불가능)
    private String password;                 // 비밀번호 (변경 가능)
    private String nickname;                 // 닉네임 (변경 가능)
    // private Image profileImage;           // 프로필 사진 (변경 가능)
    private UserStatusType userStatus;       // 접속 상태 - 온라인, 접속 중 등등 (변경 가능)

    private List<Channel> channels;          // 사용자가 속해있는 채널 목록
    private List<Message> messages;          // 사용자가 전송한 메시지 목록

    public User(String email, String password, String nickname, UserStatusType userStatus) {
        this.channels = new ArrayList<>();
        this.messages = new ArrayList<>();

        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.userStatus = userStatus;
    }

    public void updatePassword(String newPassword) {
        this.password = newPassword;
        this.updatedAt = System.currentTimeMillis();
    }

    public void updateNickname(String newNickname) {
        this.nickname = newNickname;
        this.updatedAt = System.currentTimeMillis();
    }

    public void updateUserStatus(UserStatusType newUserStatus) {
        this.userStatus = newUserStatus;
        this.updatedAt = System.currentTimeMillis();
    }

    public void addChannel(Channel channel) {
        this.channels.add(channel);
    }

    public void removeChannel(Channel channel) {
        this.channels.remove(channel);
    }

    public void addMessage(Message message) {
        this.messages.add(message);
    }

    public void removeMessage(Message message) {
        this.messages.remove(message);
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

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getNickname() {
        return nickname;
    }

    public UserStatusType getUserStatus() {
        return userStatus;
    }

    public List<Channel> getChannels() {
        return channels;
    }

    public List<Message> getMessages() {
        return messages;
    }

    // public Image getImage() {
    // return image;
    // }
}
