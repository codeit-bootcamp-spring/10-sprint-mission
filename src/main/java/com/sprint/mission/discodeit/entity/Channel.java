package com.sprint.mission.discodeit.entity;

import java.util.ArrayList;
import java.util.UUID;

public class Channel extends BaseEntity{
    private String channelName;         // 채널 이름 (변경 가능)
    private UUID ownerId;               // 채널 소유자 id (변경 불가능)
    private ArrayList<UUID> users;      // 채널 참가자 (변경 가능)
    private ChannelType type;           // CHAT, VOICE (변경 불가능)

    // 생성자
    public Channel(String channelName, UUID ownerId, ChannelType channelType) {
        this.users = new ArrayList<>();

        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
        this.channelName = channelName;
        this.ownerId = ownerId;
        this.users.add(ownerId);
        this.type = channelType;
    }

    // update
    public void updateChannelName(String channelName) {
        this.channelName = channelName;
        this.updatedAt = System.currentTimeMillis();
    }

    // getter
    public UUID getId() {   return id;  }
    public Long getCreatedAt() {    return createdAt;   }
    public Long getUpdatedAt() {    return updatedAt;   }
    public String getChannelName() {    return channelName;  }
    public UUID getOwnerId() {    return ownerId;   }
    public ArrayList<UUID> getUsers() { return users; }
    public ChannelType getChannelType() { return type; }
}