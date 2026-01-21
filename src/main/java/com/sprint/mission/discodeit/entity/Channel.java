package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.service.file.Identifiable;

import java.io.Serializable;
import java.util.*;

public class Channel extends BaseEntity implements Serializable, Identifiable {
    private static final long serialVersionUID = 1L;

    public enum ChannelType {
        PUBLIC,
        PRIVATE;
    }
    private String channelName;
    private String description;
    private ChannelType channelType;

    // List는 멀티쓰레딩 환경에서 순서 보장 안됨
    // 나중에 리팩토링 가능성 존재
    private final Set<User> users = new HashSet<>();
    private final List<Message> messages = new ArrayList<>();

    public Channel(String channelName, String description, ChannelType channelType) {
        super();
        validateName(channelName);
        this.description = description;
        this.channelName = channelName;
        this.channelType = channelType;
    }

    // Getters
    public String getChannelName() {
        return channelName;
    }
    public String getDescription() {
        return description;
    }
    public ChannelType getChannelVisibility() {
        return channelType;
    }

    // Updates
    public void updateName(String newName) {
        validateName(newName);
        this.channelName = newName;
        updateTimestamp();
    }

    public void updateDescription(String newDescription) {
        this.description = newDescription;
        updateTimestamp();

    }

    public void updateVisibility(ChannelType visibility) {
        this.channelType = visibility;
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
    public void updateMessageInList(Message updatedMessage) {
        for (int i = 0; i < this.messages.size(); i++) {
            // 리스트를 돌며 ID가 같은 메시지를 찾습니다.
            if (this.messages.get(i).getId().equals(updatedMessage.getId())) {
                // 해당 인덱스의 메시지 객체를 수정된 객체로 교체합니다.
                this.messages.set(i, updatedMessage);
                return;
            }
        }
    }

    // Convenience
    public void updateUserInSet(User updatedUser) {
        if (this.users.contains(updatedUser)) {
            this.users.remove(updatedUser); // 수정 전 객체 삭제
            this.users.add(updatedUser); // 수정 후 객체 등록
        }
    }

    // toString
    @Override
    public String toString() {
        return "Channel{" +
                "id=" + id +
                ", name='" + channelName + '\'' +
                ", description='" + description + '\'' +
                ", channelVisibility=" + channelType +
                ", userCount=" + users.size() +
                ", messagesCount=" + messages.size() +
                '}';
    }

}