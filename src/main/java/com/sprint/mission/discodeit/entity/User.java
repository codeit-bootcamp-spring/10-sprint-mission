package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class User extends Basic implements Serializable {

    private static final long serialVersionUID = 1L;
    private UUID profileId;

    private String userName; // 동명이인가능 (중복 허용)

    public String alias; // 닉네임(중복 x)
    private String email;
    private String password;

    //내가 작성한 메세지 리스트.....추가!!!?? 유지가 메세지를 작성하면.... -> 메세지에 리스트항목에도 동시에 추가되어야한다...
    private List<UUID> messageIds = new ArrayList<>();
    private List<Channel> joinedChannels = new ArrayList<>();




    // user 생성자
    public User(String userName, String alias, String email, String password) {
        super(); // user 만드는 메소드 ... -> ID 와 CreatedAt 할당.
        this.userName = userName;
        this.alias = alias;
        this.email = email;
        this.password = password;
    }

    // 필드별 수정 메서드 분리
    public void changeUserName(String newUserName) {
        this.userName = newUserName;
        super.update(); // Basic의 update()를 명시적으로 호출
    }

    public void changeAlias(String newAlias) {
        this.alias = newAlias;
        super.update();
    }
    // 메세지 관련!!!!
    // public List<Message> getMessages() {
//        return messages;
//    }

    public void addMessage(UUID messageId) {
        // 중복 방지
        if(messageId == null) throw new IllegalArgumentException("messageId이 null");
        if (!messageIds.contains(messageId)) {
            messageIds.add(messageId); // list 에 mesaage add하고,
            super.update();
        }
    }

    // 채널 관련!!
    // public List<Channel> getJoinedChannels() {
//        return joinedChannels;
//    }

    // 채널 참가
    public void joinChannel(Channel channel){
        if(!joinedChannels.contains(channel)){
            joinedChannels.add(channel);
            channel.addParticipant(this);
        }
    }
    // 채널 탈퇴
    public void leaveChannel(Channel channel) {
        if (joinedChannels.contains(channel)) {
            joinedChannels.remove(channel);
            channel.removeParticipant(this); // 반대쪽에서도 제거
        }
    }

    // 프로필 아이디 //
    public void changeProfileId(UUID profileId) {
        this.profileId = profileId;
        super.update();
    }

    // Email&Password 변경
    public void changeEmail(String newEmail) {
        this.email = newEmail;
        super.update();
    }
    public void changePassword(String newPassword) {
        this.password = newPassword;
        super.update();
    }


    @Override
    public String toString() {
        return userName;
    }

}
