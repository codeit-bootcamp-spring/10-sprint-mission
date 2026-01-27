package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public class Channel extends Entity {
    private String name;
    private User owner;
    private final List<User> users;
    private final List<Message> messages;

    public Channel(String name, User owner) {
        super();
        if (owner == null) {
            throw new RuntimeException("유저가 존재하지 않습니다.");
        }

        this.name = name;
        this.owner = owner;
        this.users = new ArrayList<>();
        this.messages = new ArrayList<>();
        // 채널 목록에 유저(채널 생성자) 추가
        addUser(owner);
    }

    public List<User> getUsers() {
        return Collections.unmodifiableList(users);
    }

    public List<Message> getMessages() {
        return Collections.unmodifiableList(messages);
    }

    public void addUser(User user) {
        // 이미 가입된 유저라면 return
        if (users.contains(user)) {
            return;
        }

        // 채널에 가입한 유저 목록에 유저 추가
        users.add(user);
        
        // 유저의 채널 목록에 채널이 없다면 채널 추가
        if (!user.getChannels().contains(this)) {
            user.joinChannel(this);
        }

        // 수정 시각 갱신
        super.update();
    }

    public void removeUser(User user) {
        // 가입된 유저가 아니라면 return
        if (!users.contains(user)) {
            return;
        }

        // 채널에 가입한 유저 목록에서 유저 제거
        users.remove(user);

        // 유저의 채널 목록에 채널이 있다면 채널 제거
        if (user.getChannels().contains(this)) {
            user.leaveChannel(this);
        }

        // 수정 시각 갱신
        super.update();
    }

    public void addMessage(Message message) {
        // 채널의 메시지 목록에 메시지 추가
        messages.add(message);
        // 수정 시각 갱신
        super.update();
    }

    public void removeMessage(Message message) {
        // 채널의 메시지 목록에서 메시지 제거
        messages.remove(message);
        // 수정 시각 갱신
        super.update();
    }

    public Channel updateChannelName(String newName) {
        // 채널 이름 변경
        this.name = newName;
        // 수정 시각 갱신
        super.update();
        return this;
    }

    @Override
    public String toString() {
        return String.format(
                "Channel [id=%s, name=%s, owner=%s, messageCount=%s]",
                getId().toString().substring(0, 5),
                name,
                owner.getId().toString().substring(0, 5),
                messages.size()
        );
    }
}
