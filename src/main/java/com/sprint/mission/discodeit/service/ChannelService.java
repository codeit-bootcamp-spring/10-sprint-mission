package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    Channel createChannel(String channelName, ChannelType channelType, String description);
    Channel getChannel(UUID channelId);
    List<Channel> getAllChannels();
    List<Channel> getChannelsByUserId(UUID userId);
    Channel updateChannel(UUID channelId, String channelName, ChannelType channelType, String description);
    void deleteChannel(UUID channelId);
    void joinChannel(UUID channelId, UUID userId);
    void leaveChannel(UUID channelId, UUID userId);
}
