package com.sprint.mission.discodeit.entity;

public class Message extends DiscordEntity {

    private String msgContext;
    private Channel channel;
    private User user;


    public Message(String context, Channel channel, User user){
        this.msgContext = context;
        this.channel = channel;
        this.addAuthor(user);
        updateTime();
    }

    public String getContext(){
        return this.msgContext;
    }

    public Channel getChannel(){
        return this.channel;
    }

    public User getUser(){
        //System.out.printf("%s 메시지의 작성자: %s %n",this.msgContext, this.user);
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

    public void addAuthor(User user){
        this.user = user;
        user.addMsg(this);
        updateTime();
    }




    public String toString(){
        return String.format("[Message] context: %s | channel: %s | author: %s %n", this.msgContext, this.channel.getName(), this.user.getUserId());
    }

}
