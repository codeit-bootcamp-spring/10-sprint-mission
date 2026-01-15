package com.sprint.mission.discodeit.entity;

import java.util.ArrayList;
import java.util.List;

public class User extends Entity {
    private final String email;
    private String nickname;
    private final List<Channel> channels;
    private final List<Message> messages;

    public User(String nickname, String email) {
        super();
        this.nickname = nickname;
        this.email = email;
        this.channels = new ArrayList<>(); // 순서를 기억하고 보편적인 컬렉션을 사용하기 위해 선택
        this.messages = new ArrayList<>();
    }

    public String getNickname() {
        return nickname;
    }

    public String getEmail() {
        return email;
    }

    public List<Channel> getChannels() {
        return new ArrayList<>(channels);
    }

    public List<Message> getMessages() {
        return new ArrayList<>(messages);
    }

    public void joinChannel(Channel channel) {
        // 유저가 가입한 채널 목록에 채널 추가
        channels.add(channel);

        // 채널에 가입한 유저 목록에 해당 유저가 없으면 추가
        if (!channel.getUsers().contains(this)) {
            channel.addUser(this);
        }

        // 수정 시각 갱신
        super.update();
    }

    public void leaveChannel(Channel channel) {
        // 유저가 가입한 채널 목록에서 채널 제거
        channels.remove(channel);

        // 채널에 가입한 유저 목록에 해당 유저가 있으면 제거
        if (channel.getUsers().contains(this)) {
            channel.removeUser(this);
        }

        // 수정 시각 갱신
        super.update();
    }

    public void addMessage(Message message) {
        // 유저가 작성한 메시지 목록에 메시지 추가
        messages.add(message);
        // 수정 시각 갱신
        super.update();
    }

    public void removeMessage(Message message) {
        // 유저가 작성한 메시지 목록에서 메시지 제거
        messages.remove(message);
        // 수정 시각 갱신
        super.update();
    }

    public User updateUserNickname(String nickname) {
        // 닉네임 변경
        this.nickname = nickname;
        // 수정 시각 갱신
        super.update();
        return this;
    }

    @Override
    public String toString() {
        return String.format(
                "User [id=%s, nickname=%s, email=%s, joinedChannels=%s, messageCount=%s]",
                getId().toString().substring(0, 5),
                nickname,
                email,
                channels.stream()
                        .map(ch -> "[id=" + ch.getId().toString().substring(0,5) + ", name=" + ch.getName() + "]").toList(),
                messages.size()
        );
    }
}
