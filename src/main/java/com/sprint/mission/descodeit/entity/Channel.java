package com.sprint.mission.descodeit.entity;
import java.util.*;
import java.util.UUID;
import com.sprint.mission.descodeit.entity.User;

public class Channel {
    private final UUID id;
    private final Long createdAt;
    private Long updatedAt;
    private String channelName;
    private List<User> userList;
    private List<Message> messageList;

    public Channel(String channelName) {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = this.createdAt;
        this.channelName = channelName;
        this.userList = new ArrayList<>();
        this.messageList = new ArrayList<>();
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

    public String getChannelName(){
        return channelName;
    }

    public List<Message> getMessageList(){
        return messageList;
    }

    public List<User> getUserList(){
        return userList;
    }

    public void addUsers(User user){
        this.userList.add(user);
        if(!user.getChannelList().contains(this)){
            user.addChannel(this);
        }
    }

    public void addMessage(Message message){
        this.messageList.add(message);
        if(message.getChannel() != this){
            message.addChannel(this);
        }
    }

    public void updateChannel(String newChannelName){
        this.channelName = newChannelName;
        this.updatedAt = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return this.getChannelName();
    }
}
