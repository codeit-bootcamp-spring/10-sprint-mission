package com.sprint.mission.descodeit.entity;
import java.util.*;
import java.util.UUID;
import com.sprint.mission.descodeit.entity.User;

public class Channel extends BaseEntity {
    private String channelName;
    private List<UUID> userList;
    private List<UUID> messageList;

    public Channel(String channelName) {
        this.channelName = channelName;
        this.userList = new ArrayList<>();
        this.messageList = new ArrayList<>();
    }

    public String getChannelName(){
        return channelName;
    }

    public List<UUID> getMessageList(){
        return messageList;
    }

    public List<UUID> getUserList(){
        return userList;
    }

    public void addUsers(UUID userId){
        if(!this.getUserList().contains(userId)){
            this.getUserList().add(userId);
        }
    }

    public void addMessage(UUID messageId){
        if(!this.getMessageList().contains(messageId)){
            this.getMessageList().add(messageId);
        }
    }

    public void updateChannel(String newChannelName){
        this.channelName = newChannelName;
        updateTimeStamp();
    }

    @Override
    public String toString() {
        return this.getChannelName();
    }
}
