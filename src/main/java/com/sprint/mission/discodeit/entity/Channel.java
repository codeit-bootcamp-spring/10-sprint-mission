package com.sprint.mission.discodeit.entity;

import java.util.*;

public class Channel extends BaseEntity {
    private String channelName;

    // 서버에 참여중인 user들
    private List<User> joinedUsers;
    // 서버에 작성되는 message들
    private List<Message> messages;

    public Channel(String channelName) {
        super();
        this.channelName = channelName;
        this.joinedUsers = new ArrayList<User>();
        this.messages = new ArrayList<Message>();
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
        messages.add(message);
    }
    public void removeMessage(Message message) {
        messages.remove(message);
    }

    // 각 필드 반환하는 getter 함수

    public String getChannelName() {
        return channelName;
    }

    public List<User> getJoinedUsers() {
        return joinedUsers;
    }

    public List<Message> getMessageList() {
        return messages;
    }
    // 필드 수정하는 update 함수
    public void setChannelName(String channelName) {
        this.channelName = channelName;
        setUpdatedAt();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Channel channel)) return false;
        return Objects.equals(this.getId(), channel.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.getId());
    }

    @Override
    public String toString() {
        return "{channelName=" + channelName + '}';
    }
}
