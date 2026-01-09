package com.sprint.mission.discodeit.entity;

public class Channel extends Entity {
    private String channelId;

    public Channel(String channelId) {
        super();
        this.channelId = channelId;
    }

    public String getChannelId() {
        return channelId;
    }

    @Override
    public void update(String channelId) {
        this.channelId = channelId;
        updateTime();
    }
}
