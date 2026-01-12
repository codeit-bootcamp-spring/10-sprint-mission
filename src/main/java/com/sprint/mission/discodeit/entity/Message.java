package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Message {

    private UUID id;
    private String content;
    private UUID userId;
    private UUID channelId;
    private Long createdAt;
    private Long updatedAt;

    public Message(String content,UUID userId, UUID channelId){
        this.id = UUID.randomUUID();
        this.content = content;
        this.userId = userId;
        this.channelId = channelId;
        this.createdAt = System.currentTimeMillis();
    }

    public UUID getId() {
        return id;
    }


    public String getContent() {
        return content;
    }

    //내용 수정일때 update시간 필요
    public void setContent(String content) {
        this.content = content;
        this.updatedAt = System.currentTimeMillis();
    }

    public UUID getUserId() {
        return userId;
    }

    public UUID getChannelId() {
        return channelId;
    }


    public Long getCreatedAt() {
        return createdAt;
    }


    public Long getUpdatedAt() {
        return updatedAt;
    }

    public String toString(){
        return "Message{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", userId='" + userId + '\'' +
                ", channelId='" + channelId + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';

    }


}
