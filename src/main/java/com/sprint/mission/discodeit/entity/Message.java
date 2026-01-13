package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Message extends Common{
    String content;
    private User userId;
    private Channel channelId;
    private User sender;

    public Message(String content, User sender, Channel channel){
        super();
        this.content = content;
        this.sender = sender;
        this.channelId = channel;
    }

    public String getContent(){
        return content;
    }

    public void setContent(String content){
        this.content = content;
    }

    public User getUserId(){
        return userId;
    }
    public Channel getChannelId(){
        return channelId;
    }
}
