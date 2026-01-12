package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    Channel createChannel(String name, UUID user);
    Channel findChannelById(UUID channelId);
    List<Channel> findAll();
    Channel updateChannelName(UUID channelId, String name);
    void deleteChannel(UUID channelId);
    void joinChannel(UUID channelId, UUID userId);
    void leaveChannel(UUID channelId, UUID userId);
}
