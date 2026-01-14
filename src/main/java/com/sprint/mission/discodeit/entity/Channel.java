package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Channel extends CommonEntity {
    private ChannelType type;
    private String channelName;
    private String channelDescription;

    public Channel(ChannelType type, String channelName, String channelDescription) {
        this.type = type;
        this.channelName = channelName;
        this.channelDescription = channelDescription;

    }


    public void updateChannelType(ChannelType type) {
        this.type = type;
        update();
    }

    public void updateChannelName(String channelName) {
        this.channelName = channelName;
        update();
    }

    public void updateChannelDescription(String channelDescription) {
        this.channelDescription = channelDescription;
        update();
    }

    public String getChannelStatus() {
        return "채널 타입: " + type + ", 채널 이름: " + channelName + ", 채널 설명: " + channelDescription;
    }
}
