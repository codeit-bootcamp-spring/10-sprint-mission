package com.sprint.mission.discodeit.entity;

import java.util.*;

public class Channel extends Common{
    private String channelName;
    private List<User> userList = new ArrayList<>();
    private List<Message> messageList = new ArrayList<>();

    public Channel(String channelName){
        super();
        this.channelName = channelName;
    }

    public String getChannelName(){
        return channelName;
    }

    public void setChannelName(String channelName){
        this.channelName = channelName;
        setUpdatedAt();
    }

    public List<User> getUserList(){
        return userList;
    }

    public void addUserList(User user){
        this.userList.add(user);
        if (!user.getChannelList().contains(this)){
            user.addChannel(this);
        }
    }

    public List<Message> getMessageList(){
        return messageList;
    }

    public void addMessage(Message message){
        messageList.add(message);
    }
}
