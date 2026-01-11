package com.sprint.mission.discodeit.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class User extends Commonness {
    private String nickName;
    private String userName;
    private String email;
    private String phoneNumber;
    private final List<Channel> channelList; // 특정 유저의 채널 소속 확인 용

    public User(String nickName, String userName, String email, String phoneNumber) {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.nickName = nickName;
        this.userName = userName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.channelList = new ArrayList<Channel>();
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

    public String getNickName() {
        return nickName;
    }

    public String getUserName() {
        return userName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public List<Channel> getChannelList() {
        return channelList;
    }

    public void updateUpdatedAt(Long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void updateNickName(String nickName) {
        this.nickName = nickName;
    }

    public void updateUserName(String userName) {
        this.userName = userName;
    }

    public void updateEmail(String email) {
        this.email = email;
    }

    public void updatePhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String toString() {
        return "{" +
            nickName + "(" + userName + ") / " +
            "email: " + email + " / " +
            "phoneNumber: " + phoneNumber +
            "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
