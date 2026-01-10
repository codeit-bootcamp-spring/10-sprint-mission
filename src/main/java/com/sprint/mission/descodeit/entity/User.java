package com.sprint.mission.descodeit.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class User {
    private final UUID id;
    private final Long createdAt;
    private Long updatedAt;
    private String name;
    private List<Message> messageList;
    private List<Channel> channelList;

    public User(String name, String nickName, int age, String gender){
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = this.createdAt;
        this.name = name;
        this.messageList = new ArrayList<>();
        this.channelList = new ArrayList<>();
    }

    public UUID getId(){
        return id;
    }

    public Long getCreatedAt(){
        return createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
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

    public void addMesage(Message message){
        this.messageList.add(message);
        if(message.getUser() != this){
            message.addUser(this);
        }
    }

    public void updateUser(String newName){
        this.name = newName;
        this.updatedAt = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return name;
    }
}
