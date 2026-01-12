package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Channel {

    private UUID id;
    private String channelName;
    private String type;
    private UUID ownerId;

    private Long createdAt;
    private Long updatedAt;


    public Channel(String channelName, String type, UUID ownerId){
        this.id = UUID.randomUUID();
        this.channelName = channelName;
        this.type = type;
        this.ownerId = ownerId;
        this.createdAt = System.currentTimeMillis();
    }

    public UUID getId() {
        return id;
    }

    public String getChannelName() {
        return channelName;
    }

    public String getType() {
        return type;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

//    public void setId(UUID id) {
//        this.id = id;
//    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setOwnerId(UUID ownerId) {
        this.ownerId = ownerId;
    }

//    public void setCreatedAt(Long createdAt) {
//        this.createdAt = createdAt;
//    }

    public void setUpdatedAt(Long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String toString(){
        return "Channel{" +
                "id=" + id +
                ", channelName='" + channelName + '\'' +
                ", type='" + type + '\'' +
                ", ownerId='" + ownerId + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';

    }
}
