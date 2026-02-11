package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class Channel extends CommonEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private ChannelType type;
    private String channelName;
    private String channelDescription;
    private final ArrayList<UUID> joinedUserIds = new ArrayList<>();
    private final ArrayList<UUID> messageIds = new ArrayList<>();

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



}
