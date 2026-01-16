package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    Channel createChannel(String channelName);

    List<Channel> getChannelList();

    List<Channel> getChannelsByUser(UUID userId);

    Channel getChannelInfoById(UUID channelId);

    Channel updateChannelName(UUID channelId, String newChannelName);

    void joinChannel(UUID channelId, UUID userId);

    void leaveChannel(UUID channelId, UUID userId);

    void deleteChannel(UUID channelId);
}
