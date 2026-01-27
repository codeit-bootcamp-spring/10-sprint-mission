package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class Channel extends BaseEntity {
    private String channelName;
    private List<UUID> userList;
    private List<UUID> messageList;

    public Channel(String channelName) {
        this.channelName = channelName;
        this.userList = new ArrayList<>();
        this.messageList = new ArrayList<>();
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
