package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Message extends Common{
    String content;
    private Channel channel;
    private User sender;

    public Message(String content, User sender, Channel channel){
        super();
        this.content = content;
        this.sender = sender;
        sender.addMessage(this);
        this.channel = channel;
    }

    public String getContent(){
        return content;
    }

    public void setContent(String content){
        this.content = content;
    }

    public User getUserId(){
        return sender;
    }
    public Channel getChannelId(){
        return channel;
    }
}
