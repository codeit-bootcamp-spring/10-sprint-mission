package com.sprint.mission.discodeit.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class User extends Base {
    private final List<Channel> channelList; // 특정 유저의 채널 소속 확인 용
    private final List<Message> messageList; // 특정 유저가 생성한 모든 메시지
    private String nickName;
    private String userName;
    private String email;
    private String phoneNumber;

    public User(String nickName, String userName, String email, String phoneNumber) {
        this.nickName = nickName;
        this.userName = userName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.channelList = new ArrayList<Channel>();
        this.messageList = new ArrayList<Message>();
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

    public List<Message> getMessageList() {
        return messageList;
    }

    public void updateNickName(String nickName) {
        this.nickName = nickName;
        updateUpdatedAt(System.currentTimeMillis());
    }

    public void updateUserName(String userName) {
        this.userName = userName;
        updateUpdatedAt(System.currentTimeMillis());
    }

    public void updateEmail(String email) {
        this.email = email;
        updateUpdatedAt(System.currentTimeMillis());
    }

    public void updatePhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        updateUpdatedAt(System.currentTimeMillis());
    }

    public void addChannel(Channel channel) {
        this.channelList.add(channel);
    }

    public void addMessage(Message message) {
        this.messageList.add(message);
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
        return Objects.equals(getId(), user.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
