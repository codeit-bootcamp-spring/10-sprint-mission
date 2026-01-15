package com.sprint.mission.discodeit.entity;

import java.util.*;

public class Channel extends BaseEntity {

    // 채널에 속한 메세지 목록
    private final List<Message> messages = new ArrayList<>();
    // 채널에 참여한 유저 목록
    private final List<User> users = new ArrayList<>();

    private String name;
    private String description;

    // constructor
    public Channel(String name, String description) {
        this.name = name;
        this.description = description;
    }

    // Getter, update
    public List<User> getUsers() {
        return List.copyOf(this.users);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void updateName(String name) {
        this.name = name;
        updateTimestamp();
    }

    public void updateDescription(String description) {
        this.description = description;
        updateTimestamp();
    }

    // 유저 목록에 유저 추가
    public void addUser(User user) {
        if (user == null)
            return;
        if (!this.users.contains(user))
            this.users.add(user);
    }

    // 유저 목록에서 유저 삭제
    public void removeUser(User user) {
        users.remove(user);
    }

    // 메세지 목록에 메세지 추가 + 채널 수정 시간 갱신
    public void addMessage(Message message) {
        this.messages.add(message);
        this.updateTimestamp();
    }

    // 메세지 목록에서 메세지 삭제 + 채널 수정 시간 갱신
    public void removeMessage(UUID messageId) {
        this.messages.removeIf(m -> m.getId().equals(messageId));
        this.updateTimestamp();
    }

    // 채널 메세지 리스트 반환
    public List<Message> getMessages() {
        return Collections.unmodifiableList(messages);
    }
}
