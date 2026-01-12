package com.sprint.mission.discodeit.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class User {
    // id는 변경될 수 없음(final)
    private final UUID id;
    private String userName;
    // 생성시기는 변경될 수 없음(final)
    private final long createdAt;
    private long updatedAt;
    // 참여 중인 채널
    private List<Channel> channels;
    // 작성한 메시지
    private List<Message> messages;

    public User(String userName) {
        this.id = UUID.randomUUID();
        this.userName = userName;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = createdAt;
        this.channels = new ArrayList<Channel>();
        this.messages = new ArrayList<Message>();
    }

    public void joinChannel(Channel channel) {
        if (!this.channels.contains(channel)) {
            this.channels.add(channel);
        }
        if (!channel.getJoinedUsers().contains(this)) {
            channel.addJoinedUser(this);
        }
    }
    public void leaveChannel(Channel channel) {
        this.channels.remove(channel);
        if (channel.getJoinedUsers().contains(this)) {
            channel.removeJoinedUser(this);
        }
    }

    public void addMessage(Message message, Channel channel) {
        this.messages.add(message);
        channel.addMessage(message);
        if (message.getUser() != this) {
            message.addUser(this);
        }
    }

    public void removeMessage(Message message, Channel channel) {
        this.messages.remove(message);
        channel.removeMessage(message);
        if (channel.getMessageList().contains(message)) {
            channel.removeMessage(message);
        }
    }


    // 각 필드 반환하는 getter 함수 정의
    public UUID getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public List<Channel> getChannels() {
        return channels;
    }

    public List<Message> getMessages() {
        return messages;
    }

    // 필드 수정하는 update 함수 정의
    public void setUserName(String userName) {
        this.userName = userName;
        setUpdatedAt();
    }
    public void setUpdatedAt() {
        this.updatedAt = System.currentTimeMillis();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof User user)) return false;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return  "{userName=" + userName + "}";
    }
}
