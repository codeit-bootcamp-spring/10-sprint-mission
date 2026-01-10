package com.sprint.mission.descodeit.entity;
import java.util.UUID;

public class Message {
    private final UUID id;
    private final Long createdAt;
    private Long updatedAt;
    private User user;
    private String text;
    private Channel channel;

    public Message(User user, String text, Channel channel) {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = this.createdAt;
        this.user = user;
        this.text = text;
        this.channel = channel;
    }

    public UUID getId(){
        return id;
    }

    public Long getCreatedAt(){
        return createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
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
            user.addMesage(this);
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
        this.updatedAt = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return this.getText();
    }
}
