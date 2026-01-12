package com.sprint.mission.descodeit.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class User extends BaseEntity{
    private String name;
    private List<Message> messageList;
    private List<Channel> channelList;

    public User(String name){
        this.name = name;
        this.messageList = new ArrayList<>();
        this.channelList = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public List<Message> getMessageList(){
        return messageList;
    }

    public List<Channel> getChannelList(){
        return channelList;
    }

    public void addChannel(Channel channel){
        this.channelList.add(channel);
        if(!channel.getUserList().contains(this)){
            channel.addUsers(this);
        }
    }

    public void addMessage(Message message){
        this.messageList.add(message);
        if(message.getUser() != this){
            message.addUser(this);
        }
    }

    public void updateUser(String newName){
        this.name = newName;
        updateTimeStamp();
    }

    @Override
    public String toString() {
        return name;
    }
}
