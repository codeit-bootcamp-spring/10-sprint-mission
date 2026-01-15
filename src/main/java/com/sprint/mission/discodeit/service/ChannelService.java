package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    Channel createChannel(String name);
    Channel findChannelById(UUID channelId);
    Channel findChannelByName(String name);
    List<Channel> findAllChannel();
    List<Channel> findChannelsByUser(UUID userId);
    Channel updateChannel(UUID channelId, String name);
    void deleteChannel(UUID channelId);
    void joinChannel(UUID userId, UUID channelId);
    void leaveChannel(UUID userId, UUID channelId);
}
