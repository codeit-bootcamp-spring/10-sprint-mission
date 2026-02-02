package com.sprint.mission.discodeit.entity;

import lombok.Getter;

public class Channel extends BaseEntity {
    @Getter
    private final ChannelType type;
    @Getter
    private String channelName;
    @Getter
    private String description;

    public Channel(ChannelType type, String channelName, String description) {
        this.type = type;
        this.channelName = channelName;
        this.description = description;
    }

    public void updateChannelName(String channelName) {
        this.channelName = channelName;
        setUpdateAt();
    }

    public void updateDescription(String description) {
        this.description = description;
        setUpdateAt();
    }

    @Override
    public String toString() {
        return channelName;
    }
}
