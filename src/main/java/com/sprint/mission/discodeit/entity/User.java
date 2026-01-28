package com.sprint.mission.discodeit.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

@Getter
@ToString(exclude = {"joinedChannels", "myMessages"})
public class User extends BaseEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String name;
    private String nickname;
    private String email;
    private String password;
    private String status;

    @Getter(AccessLevel.NONE)
    private final List<Channel> joinedChannels = new ArrayList<>();

    @Getter(AccessLevel.NONE)
    private final List<Message> myMessages = new ArrayList<>();


    public User(String name, String nickname, String email, String password) {
        super();
        this.name = name;
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.status = "OFFLINE";
    }

    // 유저 이름 수정
    public void updateName(String name) {
        this.name = name;
        this.updated();
    }

    // 유저 닉네임 수정
    public void updateNickname(String nickname) {
        this.nickname = nickname;
        this.updated();
    }

    // 유저 이메일 수정
    public void updateEmail(String email) {
        this.email = email;
        this.updated();
    }

    // 유저 상태 수정
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

    // 메시지 삭제
    public  void removeMessage(Message message){
        this.myMessages.remove(message);
    }

    // --- getter ---
    public List<Channel> getJoinedChannels() { return Collections.unmodifiableList(joinedChannels); }
    public List<Message> getMyMessages() { return Collections.unmodifiableList(myMessages); }
}