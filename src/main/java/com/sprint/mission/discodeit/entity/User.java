package com.sprint.mission.discodeit.entity;

import java.util.*;

public class User extends Base {
    // 필드
    private String name;
    private final Set<Channel> channels;
    private final List<Message> messageList;

    // 생성자
    public User(String name) {
        super();
        this.name = name;
        this.channels = new HashSet<>();
        this.messageList = new ArrayList<Message>();
    }

    // Getter
    public String getName() {
        return name;
    }

    public Set<Channel> getChannels() {
        return channels;
    }

    public List<Message> getMessageList(){
        return messageList;
    }

    // Setter
    public void updateName(String name) {
        this.name = name;
        update();
    }

    // other
    public void joinChannel(Channel channel) {
        channels.add(channel);
    }

    public void leaveChannel(Channel channel){
        channels.remove(channel);
    }

    public void addMessage(Message msg){
        messageList.add(msg);
    }

    public void removeMessage(Message msg){
        messageList.remove(msg);
    }
}

