package com.sprint.mission.discodeit.entity;

import java.util.*;

public class User extends BaseEntity {
    private String email;
    private String nickName = "";
    private String userName;
    private String password;
    private String birthday = "";

    // 연관
    // 해당 유저가 owner인 채널 목록
    private final Set<Channel> ownerChannelList;
    // 해당 유저가 참여 중인 채널 목록
    private final Set<Channel> joinChannelList;
    // 해당 유저가 보낸 메시지 목록
    private final List<Message> writeMessageList;

    // 생성자
    public User(String email, String nickName, String userName, String password, String birthday) {
        this.email = email;
        this.nickName = nickName;
        this.userName = userName;
        this.password = password; // 해싱?
        this.birthday = birthday;

        ownerChannelList = new HashSet<>();
        joinChannelList = new HashSet<>();
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
//                "password = " + password + ", " +
//                "birthday = " + birthday + ", " +
//                "ownerChannelList = " + ownerChannelList + ", " +
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

    public List<Channel> getOwnerChannelList() {
        return ownerChannelList.stream().toList();
    }

    public List<Channel> getJoinChannelList() {
        return joinChannelList.stream().toList();
    }

    public List<Message> getWriteMessageList() {
        return writeMessageList;
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

    // owner 임명됨
    public void ownChannel(Channel channel) {
        this.ownerChannelList.add(channel);
        updateTime();
    }

    // owner 소유권 제거
    public void removeChannelOwner(Channel channel) {
        this.ownerChannelList.remove(channel);
        updateTime();
    }

    // 채널 참가
    public void joinChannel(Channel channel) {
        this.joinChannelList.add(channel);
        updateTime();
        channel.addChannelMembers(this);
    }

    // 채널 탈퇴
    public void leaveChannel(Channel channel) {
        this.joinChannelList.remove(channel);
        updateTime();
        channel.removeChannelMembers(this);
    }

    // 메시지 작성
    public void writeMessage(Message message) {
        this.writeMessageList.add(message);
        updateTime();
//        message.addUserWriteMessageList(this, message.getContent());
    }
}
