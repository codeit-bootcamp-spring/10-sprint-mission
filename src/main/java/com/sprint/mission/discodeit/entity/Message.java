package com.sprint.mission.discodeit.entity;

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
        channel.addMessage(this);
    }

    public String getContent(){
        return content;
    }

    public void setContent(String content){
        this.content = content;
    }

    public User getUser(){
        return sender;
    }

    public Channel getChannel(){
        return channel;
    }
}
