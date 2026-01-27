package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public class User extends Entity {
    private final String email;
    private String nickname;
    private final List<Channel> channels;
    private final List<Message> messages;

    public User(String nickname, String email) {
        super();
        this.nickname = nickname;
        this.email = email;
        this.channels = new ArrayList<>();
        this.messages = new ArrayList<>();
    }

    public List<Channel> getChannels() {
        return Collections.unmodifiableList(channels);
    }

    public List<Message> getMessages() {
        return Collections.unmodifiableList(messages);
    }

    public void joinChannel(Channel channel) {
        // 이미 가입한 채널이라면 return
        if (channels.contains(channel)) {
            return;
        }

        // 유저가 가입한 채널 목록에 채널 추가
        channels.add(channel);

        // 채널의 유저 목록에 유저가 없다면 유저 추가
        if (!channel.getUsers().contains(this)) {
            channel.addUser(this);
        }


        // 수정 시각 갱신
        super.update();
    }

    public void leaveChannel(Channel channel) {
        // 가입한 채널이 아니라면 return
        if (!channels.contains(channel)) {
            return;
        }

        // 유저가 가입한 채널 목록에서 채널 제거
        channels.remove(channel);

        // 채널의 유저 목록에 유저가 있다면 유저 제거
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

    public User updateUserNickname(String newNickname) {
        // 닉네임 변경
        this.nickname = newNickname;
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
