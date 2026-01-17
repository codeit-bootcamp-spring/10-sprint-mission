package com.sprint.mission.discodeit.entity;

import java.util.*;

public class User extends BaseEntity {
    private String email;
    private String userName;
    private String nickName;
    private String password;
    private String birthday;

    // 연관
    // 해당 유저가 참여 중인 채널 List
    private final List<Channel> joinChannelList;
    // 해당 유저가 보낸 메시지 List
    private final List<Message> writeMessageList;

    // 생성자
    public User(String email, String userName, String nickName, String password, String birthday) {
        this.email = email;
        this.userName = userName;
        this.nickName = nickName;
        this.password = password; // 해싱?
        this.birthday = birthday;

        joinChannelList = new ArrayList<>();
        writeMessageList = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "User{" +
                "userId = " + getId() + ", " +
//                "createdAt = " + getCreatedAt() + ", " +
//                "updatedAt = " + getUpdatedAt() + ", " +
                "email = " + email + ", " +
                "nickName = " + nickName + ", " +
                "userName = " + userName + ", " +
                "password = " + password + ", " +
                "birthday = " + birthday + ", " +
//                "joinChannelList = " + joinChannelList + ", " +
//                "writeMessageList = " + writeMessageList +
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

    public List<Channel> getJoinChannelList() {
        return joinChannelList.stream().toList();
    }

    public List<Message> getWriteMessageList() {
        return writeMessageList.stream().toList();
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

    // 해시??
    public void updatePassword(String password) {
        this.password = password;
        updateTime();
    }

    public void updateBirthday(String birthday) {
        this.birthday = birthday;
        updateTime();
    }

    // 채널 참가
    public void joinChannel(Channel channel) {
        this.joinChannelList.add(channel);
        updateTime();
    }

    // 채널 탈퇴
    public void leaveChannel(UUID channelId) {
        this.joinChannelList.removeIf(channel -> channel.getId().equals(channelId));
        updateTime();
    }

    // 메시지 작성
    public void writeMessage(Message message) {
        this.writeMessageList.add(message);
        updateTime();
    }

    // 유저가 작성한 메시지 삭제
    public void removeUserMessage(UUID messageId) {
        this.writeMessageList.removeIf(message -> message.getId().equals(messageId));
        updateTime();
    }
}
