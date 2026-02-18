package com.sprint.mission.discodeit.entity;

import lombok.Getter;

@Getter
public class Channel extends BaseEntity {

    private final ChannelType type;
    private String name;
    private String description;

    public Channel(ChannelType type, String name, String description) {
        this.type = type;
        this.name = name;
        this.description = description;
    }

    public void updateChannelName(String channelName) {
        this.name = channelName;
        setUpdatedAt();
    }

    public void updateDescription(String description) {
        this.description = description;
        setUpdatedAt();
    }
}
