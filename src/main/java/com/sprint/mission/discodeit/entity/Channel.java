package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Channel {
    private final UUID id;
    private final Long createdAt;
    private Long updatedAt;

    private ChannelType type;
    private String channelName;
    private String channelDescription;

    public Channel(ChannelType type, String channelName, String channelDescription) {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = createdAt;

        this.type = type;
        this.channelName = channelName;
        this.channelDescription = channelDescription;

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

    public void updateChannelType(ChannelType type) {
        this.type = type;
    }

    public void updateChannelName(String channelName) {
        this.channelName = channelName;
    }

    public void updateChannelDescription(String channelDescription) {
        this.channelDescription = channelDescription;
    }

    public String getChannelStatus() {
        return "채널 타입: " + type + ", 채널 이름: " + channelName + ", 채널 설명: " + channelDescription;
    }
}
