package com.sprint.mission.discodeit.entity;

import java.util.*;

public class Channel extends BaseEntity {
    private String channelName;
    private boolean isPublic;

    // List는 멀티쓰레딩 환경에서 순서 보장 안됨
    // 나중에 리팩토링 가능성 존재
    private final List<User> users = new ArrayList<>();
    private final List<Message> messages = new ArrayList<>();

    public Channel(String channelName, boolean isPublic){
        super();
        this.channelName = channelName;
        this.isPublic = isPublic;
    }

    // Getters
    public String getChannelName() {
        return channelName;
    }

    public boolean isPublic() {
        return isPublic;
    }

    // Updates
    public void updateName(String newName){
        this.channelName = newName;
        updateTimestamp();
    }

    public void updatePublic(boolean isPublic){
        this.isPublic = isPublic;
        updateTimestamp();
    }

    // Message Control
    public void addMessage(Message message) {
        this.messages.add(message);
    }

    public List<Message> getMessages() {
        return this.messages;
    }

}