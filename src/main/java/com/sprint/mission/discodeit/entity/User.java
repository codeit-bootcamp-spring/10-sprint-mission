package com.sprint.mission.discodeit.entity;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class User extends BaseEntity {
    private String name;
    private String nickname;
    private String email;
    private String password;
    private String status;
    private final List<Channel> joinedChannels = new ArrayList<>();
    private final List<Message> myMessages = new ArrayList<>();


    public User(String name, String nickname, String email, String password) {
        super();
        this.name = name;
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.status = "OFFLINE";
    }

    // 유저 정보 수정 (이름, 닉네임, 이메일)
    public void update(String name, String nickname, String email) {
        this.name = name;
        this.nickname = nickname;
        this.email = email;
        this.updated(); // BaseEntity의 시간 갱신
    }

    // 유저 상태만 따로 수정
    public void updateStatus(String status) {
        this.status = status;
        this.updated();
    }

    // 비밀번호 변경
    public void updatePassword(String password) {
        this.password = password;
        this.updated();
    }

    // 채널 가입
    public void addJoinedChannel(Channel channel){
        this.joinedChannels.add(channel);
    }

    // 채널 탈퇴
    public void leaveChannel (Channel channel){
        this.joinedChannels.remove(channel);
    }

    // 메시지 작성
    public void addMessage(Message message){
        this.myMessages.add(message);
    }

    // --- getter ---
    public String getName() { return name; }
    public String getNickname() { return nickname; }
    public String getEmail() { return email; }
    public String getStatus() { return status; }
    public List<Channel> getJoinedChannels() { return Collections.unmodifiableList(joinedChannels); }
    public List<Message> getMyMessages() { return Collections.unmodifiableList(myMessages); }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", nickname='" + nickname + '\'' +
                ", email='" + email + '\'' +
                ", status='" + status + '\'' +
                ", updatedAt=" + updatedAt +
                '}';
    }
}