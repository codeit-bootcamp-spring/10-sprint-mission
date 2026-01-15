package com.sprint.mission.discodeit.entity;

import java.util.*;

public class User extends BaseEntity {
    private String email;
    private String userName;
    private String nickName;
    private String password;
    private String birthday;

    // 연관
    // 해당 유저가 owner인 채널 목록
    private final Set<Channel> ownerChannelList;
    // 해당 유저가 참여 중인 채널 목록
    private final Set<Channel> joinChannelList;
    // 해당 유저가 보낸 메시지 목록
    private final List<Message> writeMessageList;

    // 생성자
    public User(String email, String userName, String nickName, String password, String birthday) {
        this.email = email;
        this.userName = userName;
        this.nickName = nickName;
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
                "ownerChannelList = " + ownerChannelList.stream().map(channel -> channel.getId()).toList() + ", " +
                "joinChannelList = " + joinChannelList.stream().map(channel -> channel.getId()).toList() + ", " +
                "writeMessageList = " + writeMessageList.stream().map(message -> message.getId()).toList() +
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

    // 채널 생성 시 owner에 임명될 때 연관 관계로 owner인 user의 ownerChannelList에 추가
    public void ownChannel(Channel channel) {
        this.ownerChannelList.add(channel);
        updateTime();
    }

    // owner가 변경(업데이트)되거나 채널 삭제 시 소유권 제거
    public void removeChannelOwner(Channel channel) {
        this.ownerChannelList.remove(channel);
        updateTime();
    }

    // 채널 참가
    public void joinChannel(Channel channel) {
        this.joinChannelList.add(channel);
        updateTime();
        channel.addChannelUser(this);
    }

    // 채널 탈퇴
    public void leaveChannel(Channel channel) {
        this.joinChannelList.remove(channel);
        updateTime();
        channel.removeChannelUser(this);
    }

    // 메시지 작성
    public void addMessageInUser(Message message) {
        this.writeMessageList.add(message);
        updateTime();
//        message.addUserWriteMessageList(this, message.getContent());
    }

    // 유저가 작성한 메시지 삭제
    public void removeMessageInUser(Message message) {
        this.writeMessageList.remove(message);
        updateTime();
    }
}
