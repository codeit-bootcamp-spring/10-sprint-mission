package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    public Channel createChannel(String channelName, UUID userId, ChannelType channelType);

    public Channel searchChannel(UUID targetChannelId);

    public List<Channel> searchChannelAll();

    public Channel updateChannel(UUID targetChannelId, String newChannelName);

    public void deleteChannel(UUID targetChannelId);
}