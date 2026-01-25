package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class User extends Common implements Serializable {
    private String userName;
    private String email;
    private List<Channel> channelList;
    private List<Message> messageList;

    public User(String userName, String email){
        super();
        this.userName = userName;
        this.email = email;
        this.channelList = new ArrayList<>();
        this.messageList = new ArrayList<>();
    }

    public String getUserName(){
        return userName;
    }

    public String getEmail(){
        return email;
    }

    public void update(UUID id, String userName, String email) {
        boolean changed = false;
        if (userName != null && !userName.trim().isEmpty()) {
            this.userName = userName;
            changed = true;
        }
        if (email != null && !email.trim().isEmpty()) {
            this.email = email;
            changed = true;
        }
        if (changed) {
            setUpdatedAt();
        }
    }

    // 채널, 메세지 부분
    // 상호참조
    public List<Channel> getChannelList(){
        return channelList;
    }

    public void addChannel(Channel channel){
        this.channelList.add(channel);
        if(!channel.getUserList().contains(this)) {
            channel.addUserList(this);
        }
    }

    public List<Message> getMessageList(){
        return messageList;
    }

    public void addMessage(Message message){
        this.messageList.add(message);
    }

    @Override
    public String toString() {
        return "User{name='" + userName + "', email='" + email + "', " + super.toString() + "}";
    }
}
