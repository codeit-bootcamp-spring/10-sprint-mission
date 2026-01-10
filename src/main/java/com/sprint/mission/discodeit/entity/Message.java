package com.sprint.mission.discodeit.entity;

public class Message extends DiscodeEntity {

    private String msgContext;
    private Channel channel;
    private User user;


    public Message(String context, Channel channel, User user){
        this.msgContext = context;
        this.channel = channel;
        this.user = user;
        updateTime();
    }

    public String getContext(){
        return this.msgContext;
    }

    public Channel getChannel(){
        return this.channel;
    }

    public User getUser(){
        return this.user;
    }

    public void updateContext(String context){
        this.msgContext = context;
        updateTime();
    }

    public void updateChannel(Channel channel){
        this.channel = channel;
        updateTime();
    }



    public String toString(){
        return "채널: " + this.channel.getName() + " / 작성자: " + this.user.getUserName() + " / 내용: " + this.msgContext;
    }

}
