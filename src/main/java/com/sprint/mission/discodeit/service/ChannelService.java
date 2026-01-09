package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.HashSet;
import java.util.UUID;

public interface ChannelService {
    public void read(UUID id);
    public void readAll();
    public Channel create(String channelName, String channelDescription);
    public void delete(Channel channel);
    public void update(Channel originchannel, Channel afterchannel);
}
