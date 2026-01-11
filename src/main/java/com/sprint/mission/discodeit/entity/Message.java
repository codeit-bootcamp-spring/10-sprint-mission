package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Message {
    private UUID id;
    private Long createdAt;
    private Long updatedAt;
    private User sender;
    private Channel channel;
    private String content;

    public Message(User sender,Channel channel,String content) {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.content = content;
        this.sender = sender;
        this.channel = channel;
        //setChannel(channel);
    }
    public UUID getId() {
        return id;
    }
    public Long getCreatedAt() {
        return createdAt;
    }
    public Long getUpdatedAt() {
        return updatedAt;
    }
    public String getContent() {
        return content;
    }
    public User getSender() {
        return sender;
    }
    public Channel getChannel() {
        return channel;
    }
    private void setUpdatedAt(){
        this.updatedAt = System.currentTimeMillis();
    }
    public void setContent(String content) {
        this.content = content;
        setUpdatedAt();
    }
/*
    public void setChannel(Channel channel) {
        this.channel = channel;
        if(!channel.getMessages().contains(this)){
            channel.addMessage(this);
        }
    }
*/
    @Override
    public String toString() {
        String updated = this.updatedAt == null ? "수정 이력 없음" : this.updatedAt.toString();
        String message = this.getContent()+"-"
                        +this.getSender().getUserName()
                        +"(생성: "+this.getCreatedAt()
                        +" ,수정: "+updated+")";
        return message;
    }
}
