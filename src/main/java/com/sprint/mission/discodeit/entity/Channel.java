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

    @Override
    public String toString() {
        return String.format(
                "Channel{id=%s, channelName='%s', type='%s', ownerId='%s', 생성일자=%d, 수정일자=%d}",
                getId(),
                channelName,
                type,
                owner.getId(),
                getCreatedAt(),
                getUpdatedAt()
        );
    }

}