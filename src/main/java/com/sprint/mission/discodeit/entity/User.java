package com.sprint.mission.discodeit.entity;

import java.util.*;

public class User extends BaseEntity {
    private String email;
    private String userName;
    private String nickName;
    private String password;
    private String birthday;

    // 연관
    // 해당 유저가 참여 중인 채널 Ids
    private final Set<UUID> joinChannelIds;
    // 해당 유저가 보낸 메시지 Ids
    private final List<UUID> writeMessageIds;

    // 생성자
    public User(String email, String userName, String nickName, String password, String birthday) {
        this.email = email;
        this.userName = userName;
        this.nickName = nickName;
        this.password = password; // 해싱?
        this.birthday = birthday;

        joinChannelIds = new HashSet<>();
        writeMessageIds = new ArrayList<>();
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
                "joinChannelIds = " + joinChannelIds + ", " +
                "writeMessageIds = " + writeMessageIds +
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

    public List<UUID> getJoinChannelIds() {
        return joinChannelIds.stream().toList();
    }

    public List<UUID> getWriteMessageIds() {
        return writeMessageIds.stream().toList();
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
    public void joinChannel(UUID channelId) {
        this.joinChannelIds.add(channelId);
        updateTime();
    }

    // 채널 탈퇴
    public void leaveChannel(UUID channelId) {
        this.joinChannelIds.remove(channelId);
        updateTime();
    }

    // 메시지 작성(메세지 Ids 추가)
    public void writeMessage(UUID messageId) {
        this.writeMessageIds.add(messageId);
        updateTime();
//        message.addUserWriteMessageList(this, message.getContent());
    }

    // 유저가 작성한 메시지 삭제(메세지 Ids 삭제)
    public void removeUserMessage(UUID messageId) {
        this.writeMessageIds.remove(messageId);
        updateTime();
    }
}
