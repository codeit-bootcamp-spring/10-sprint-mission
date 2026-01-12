package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    Channel createChannel(String channelName, ChannelType channelType, String description);
    Channel getChannel(UUID channelId);
    List<Channel> getAllChannels();
    Channel updateChannel(Channel channel);
    Channel deleteChannel(Channel channel);
}
