package com.sprint.mission.descodeit.entity;
import java.util.UUID;

public class Message extends BaseEntity{
    private User user;
    private String text;
    private Channel channel;

    public Message(User user, String text, Channel channel) {
        this.text = text;
        user.addMessage(this);
        channel.addMessage(this);
    }

    public User getUser(){
        return user;
    }

    public String getText(){
        return text;
    }

    public Channel getChannel(){
        return channel;
    }

    public void addUser(User user){
        this.user = user;
        if(!user.getMessageList().contains(this)){
            user.addMessage(this);
        }
    }

    public void addChannel(Channel channel){
        this.channel = channel;
        if(!channel.getMessageList().contains(this)){
            channel.addMessage(this);
        }
    }

    public void updateMessage(String newText){
        this.text = newText;
        updateTimeStamp();
    }

    @Override
    public String toString() {
        return this.getText();
    }
}
