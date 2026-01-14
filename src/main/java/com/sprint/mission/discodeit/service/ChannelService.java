package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    Channel createChannel (String name, String intro);
    Channel findChannelById(UUID channelId);
    List<Channel> findAllChannels();
    void deleteChannel(UUID channelId);
}
