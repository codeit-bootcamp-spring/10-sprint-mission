package com.sprint.mission.discodeit.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Channel extends Common{

    private String channelName;
    private String type;
    private User owner;
    private List<User> userList;
    private List<Message> messageList;


    public Channel(String channelName, String type, User owner){
        //super();
        this.channelName = channelName;
        this.type = type;
        this.owner  = owner;
        this.userList = new ArrayList<>();
        this.messageList = new ArrayList<>();
    }

    public String getChannelName() {
        return channelName;
    }

    public String getType() {
        return type;
    }

    public User getOwner() {
        return owner;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setOwner(User ownerId){
        this.owner = owner;
    }

    public List<User> getUserList(){
        return userList;
    }

    public List<Message> getMessageList(){
        return messageList;
    }

    @Override
    public String toString() {
        return String.format(
                "\n(채널명:%s, 타입:%s, 방장:%s)\n",
                channelName,
                type,
                owner.getUserName()
        );
    }

}