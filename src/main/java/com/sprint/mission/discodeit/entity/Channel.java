package com.sprint.mission.discodeit.entity;

import java.util.ArrayList;
import java.util.List;

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
        this.users.add(owner);
        owner.joinChannel(this);
    }

    public String getName() {
        return name;
    }

    public User getOwner() {
        return owner;
    }

    public List<User> getUsers() {
        return new ArrayList<>(users);
    }

    public List<Message> getMessages() {
        return new ArrayList<>(messages);
    }

    public void addUser(User user) {
        // 채널에 가입한 유저 목록에 유저 추가
        users.add(user);

        // 유저가 가입한 채널 목록에 해당 채널이 없으면 추가
        if (!user.getChannels().contains(this)) {
            user.joinChannel(this);
        }

        // 수정 시각 갱신
        super.update();
    }

    public void removeUser(User user) {
        // 채널에 가입한 유저 목록에서 유저 제거
        users.remove(user);

        // 유저가 가입한 채널 목록에 해당 채널이 있으면 제거
        if (user.getChannels().contains(this)) {
            user.leaveChannel(this);
        }

        // 수정 시각 갱신
        super.update();
    }

    public void addMessage(Message message) {
        // 메시지 null 체크
        if (message == null) {
            throw new RuntimeException("메시지가 존재하지 않습니다.");
        }

        // 채널 일치 여부 확인
        if (!message.getChannel().equals(this)) {
            throw new RuntimeException("이 채널에 추가할 수 없는 메시지입니다.");
        }

        // 메시지 추가
        messages.add(message);
        // 수정 시각 갱신
        super.update();
    }

    public void removeMessage(Message message) {
        // 메시지 null 체크, 채널 및 유저 검증은 MessageService에서 진행
        if (message == null) {
            throw new RuntimeException("메시지가 존재하지 않습니다.");
        }

        // 존재하는 메시지인 경우에만 제거 및 수정 시각 갱신
        boolean removed = messages.remove(message);
        if (!removed) {
            throw new RuntimeException("메시지가 존재하지 않습니다.");
        }

        super.update();
    }

    public Channel updateChannelName(String name) {
        // 채널 이름 변경
        this.name = name;
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
