package com.sprint.mission.discodeit.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class User extends Common{
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

    public void setUserName(String userName){
        this.userName = userName;
        setUpdatedAt();
    }

    public String getEmail(){
        return email;
    }

    public void setEmail(String email){
        this.email = email;
        setUpdatedAt();
    }

    public List<Channel> getChannelId(){
        return channelList;
    }

    public void addChannelId(Channel channel){
        channelList.add(channel);
    }

    public List<Message> getMessageList(){
        return messageList;
    }
    public void addMessage(Message message){
        messageList.add(message);
    }

    public void addMessages(Message message) {
            this.messageList.add(message);
    }
}
