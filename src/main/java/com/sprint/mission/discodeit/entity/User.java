package com.sprint.mission.discodeit.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class User extends Common{
    private String userName;
    private String email;
    private List<UUID> channelList = new ArrayList<>();
    private List<Message> messageList = new ArrayList<>();

    public User(String userName, String email){
        super();
        this.userName = userName;
        this.email = email;
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

    public List<UUID> getChannelId(){
        return channelList;
    }

    public void addChannelId(UUID channelId){
        channelList.add(channelId);
    }

    public List<Message> getMessageList(){
        return messageList;
    }
    public void addMessage(Message message){
        messageList.add(message);
    }

}
