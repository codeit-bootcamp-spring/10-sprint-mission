package com.sprint.mission.descodeit.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class User extends BaseEntity{
    private String name;
    private final List<UUID> messageList;
    private final List<UUID> channelList;
    private final List<UUID> friendsList;

    public User(String name){
        this.name = name;
        this.messageList = new ArrayList<>();
        this.channelList = new ArrayList<>();
        this.friendsList = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public List<UUID> getMessageList(){
        return messageList;
    }

    public List<UUID> getChannelList(){
        return channelList;
    }

    public List<UUID> getFriendsList(){
        return friendsList;
    }
    public void addChannel(UUID channdId){
        if(!this.getChannelList().contains(channdId)){
            this.getChannelList().add(channdId);
        }
    }

    public void addMessage(UUID messaageId){
        if(!this.getMessageList().contains(messaageId)){
            this.getMessageList().add(messaageId);
        }
    }

    public void addFriend(UUID userId){
        if(!this.getFriendsList().contains(userId)){
            this.getFriendsList().add(userId);
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
