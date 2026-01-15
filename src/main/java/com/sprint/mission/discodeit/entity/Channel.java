package com.sprint.mission.discodeit.entity;

import java.util.*;

public class Channel extends BaseEntity {
    private String channelName;
    private Boolean isPublic;

    // List는 멀티쓰레딩 환경에서 순서 보장 안됨
    // 나중에 리팩토링 가능성 존재
    private final Set<User> users = new HashSet<>();
    private final List<Message> messages = new ArrayList<>();

    public Channel(String channelName, boolean isPublic){
        super();
        validateName(channelName);
        this.channelName = channelName;
        this.isPublic = isPublic;
    }

    // Getters
    public String getChannelName() {
        return channelName;
    }
    public Boolean isPublic() {
        return isPublic;
    }

    // Updates
    public void updateChannel(String newName, Boolean isPublic) {
        if (newName != null) {
            validateName(newName);
            this.channelName = newName;
        }
        if (isPublic != null) {
            this.isPublic = isPublic;
        }
        updateTimestamp();
    }

    // validation
    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("채널 이름은 비어있을 수 없습니다.");
        }
    }

    // User Control
    void addUser(User user){
        if (this.users.contains(user)) {
            return;
        }
        this.users.add(user);
    }
    void removeUser(User user){
        if (!this.users.contains(user)) {
            return;
        }
        this.users.remove(user);
    }
    public Set<User> getUsers() {
        return new HashSet<>(this.users);
    }
    // Message Control
    public void addMessage(Message message) {
        this.messages.add(message);
    }
    public void removeMessage(Message message) {
        this.messages.remove(message);
    }
    public List<Message> getMessages() {
        return new  ArrayList<>(this.messages);
    }

    // toString
    @Override
    public String toString() {
        return "Channel{" +
                "id=" + id +
                ", name='" + channelName + '\'' +
                ", isPublic=" + isPublic +
                ", userCount=" + users.size() +
                ", messagesCount=" + messages.size() +
                '}';
    }

}