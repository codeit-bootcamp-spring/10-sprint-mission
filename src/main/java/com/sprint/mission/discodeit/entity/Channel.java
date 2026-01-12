package com.sprint.mission.discodeit.entity;

import java.util.*;

public class Channel {
    private final UUID id;
    private String channelName;
    private final long createdAt;
    private long updatedAt;

    // 서버에 참여중인 user들
    private List<User> joinedUsers;
    // 서버에 작성되는 message들
    private List<Message> messageList;

    public Channel(String channelName) {
        this.id = UUID.randomUUID();
        this.channelName = channelName;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = createdAt;
        this.joinedUsers = new ArrayList<User>();
        this.messageList = new ArrayList<Message>();
    }

    public void addJoinedUser(User user) {
        if (!joinedUsers.contains(user)) {
            joinedUsers.add(user);
        }
        if (!user.getChannels().contains(this)) {
            user.getChannels().add(this);
        }
    }
    public void removeJoinedUser(User user) {
        joinedUsers.remove(user);
        user.getChannels().remove(this);
    }

    public void addMessage(Message message) {
        messageList.add(message);
    }
    public void removeMessage(Message message) {
        messageList.remove(message);
    }

    // 각 필드 반환하는 getter 함수
    public UUID getId() {
        return id;
    }

    public String getChannelName() {
        return channelName;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public List<User> getJoinedUsers() {
        return joinedUsers;
    }

    public List<Message> getMessageList() {
        return messageList;
    }
    // 필드 수정하는 update 함수
    public void setChannelName(String channelName) {
        this.channelName = channelName;
        setUpdatedAt();
    }

    public void setUpdatedAt() {
        this.updatedAt = System.currentTimeMillis();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Channel channel)) return false;
        return Objects.equals(id, channel.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "{channelName=" + channelName + '}';
    }
}
