package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.*;

public class User extends Base implements Serializable {
    // 필드
    private static final long serialVersionUID = 1L;
    private String name;
    private final List<Channel> channelsList;
    private final List<Message> messageList;

    // 생성자
    public User(String name) {
        super();
        this.name = name;
        this.channelsList = new ArrayList<>();
        this.messageList = new ArrayList<Message>();
    }

    // Getter
    public String getName() {
        return name;
    }

    public List<Channel> getChannels() {
        return channelsList;
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
        channelsList.add(channel);
    }

    public void leaveChannel(Channel channel){
        channelsList.remove(channel);
    }

    public void addMessage(Message msg){
        messageList.add(msg);
    }

    public void removeMessage(Message msg){
        messageList.remove(msg);
    }
}

