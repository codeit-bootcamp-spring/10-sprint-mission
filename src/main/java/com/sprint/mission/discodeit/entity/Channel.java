package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Channel extends Common{

    private String channelName;
    private String type;
    private User user;


    public Channel(String channelName, String type, User user){
        //super();
        this.channelName = channelName;
        this.type = type;
        this.user = user; //채널의 소유주
    }

    public String getChannelName() {
        return channelName;
    }

    public String getType() {
        return type;
    }

    public User getUser(){
        return user;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setUser(User user){
        this.user = user;
    }

    @Override
    public String toString() {
        return String.format(
                "User{id=%s, channelName='%s', type='%s', ownerId='%s', 생성일자=%d, 수정일자=%d}",
                getId(),
                channelName,
                type,
                user.getId(),
                getCreatedAt(),
                getUpdatedAt()
        );
    }
}
